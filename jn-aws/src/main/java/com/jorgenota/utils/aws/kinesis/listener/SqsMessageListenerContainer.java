/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jorgenota.utils.aws.kinesis.listener;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.*;
import com.jorgenota.utils.aws.sqs.DynamicSqsUrlDestinationResolver;
import com.jorgenota.utils.aws.sqs.SqsMessageHeaders;
import com.jorgenota.utils.messaging.CachingDestinationResolverProxy;
import com.jorgenota.utils.messaging.DestinationResolutionException;
import com.jorgenota.utils.messaging.DestinationResolver;
import com.jorgenota.utils.messaging.MessagingException;
import com.jorgenota.utils.messaging.listener.AbstractMessageListenerContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.jorgenota.utils.aws.sqs.SqsMessageUtils.createMessage;
import static com.jorgenota.utils.base.Preconditions.*;
import static org.springframework.aop.interceptor.ExposeBeanNameAdvisors.getBeanName;

@Slf4j
public class SqsMessageListenerContainer extends AbstractMessageListenerContainer {

    private static final String DEFAULT_THREAD_NAME_PREFIX =
            ClassUtils.getShortName(SqsMessageListenerContainer.class) + "-";
    private static final String RECEIVING_ATTRIBUTES = "All";
    private static final String RECEIVING_MESSAGE_ATTRIBUTES = "All";
    private static final int DEFAULT_MAX_NUMBER_OF_MESSAGES = 10;
    private final List<SqsMessageHandler> messageHandlers;
    private final DestinationResolver<String> destinationResolver;
    private final AmazonSQSAsync amazonSqs;

    private final Map<String, QueueAttributes> registeredQueues = new HashMap<>();

    private long backOffTime = 10000;
    private long queueStopTimeout = 10000;
    private int maxNumberOfMessages = DEFAULT_MAX_NUMBER_OF_MESSAGES;
    private ThreadPoolTaskExecutor taskExecutor = createDefaultTaskExecutor();
    private Map<String, Future<?>> scheduledFutureByQueue = Collections.emptyMap();
    private Map<String, Boolean> runningStateByQueue = Collections.emptyMap();

    //Optional settings with no defaults
    @Nullable
    private Integer visibilityTimeout;
    @Nullable
    private Integer waitTimeOut;

    public SqsMessageListenerContainer(AmazonSQSAsync amazonSqs, List<SqsMessageHandler> messageHandlers) {
        this.amazonSqs = notNull(amazonSqs, "amazonSqs must not be null");
        this.messageHandlers = notEmpty(messageHandlers, "amazonSqs must not be empty");
        this.destinationResolver = new CachingDestinationResolverProxy<>(new DynamicSqsUrlDestinationResolver(this.amazonSqs));
    }

    /**
     * Configure the maximum number of messages that should be retrieved during one poll to the Amazon SQS system. This
     * number must be a positive, non-zero number that has a maximum number of 10. Values higher then 10 are currently
     * not supported by the queueing system.
     *
     * @param maxNumberOfMessages the maximum number of messages (between 1-10)
     */
    public void setMaxNumberOfMessages(int maxNumberOfMessages) {
        isTrue((maxNumberOfMessages >= 1) && (maxNumberOfMessages <= DEFAULT_MAX_NUMBER_OF_MESSAGES), "maxNumberOfMessages must not be between 1-10");
        this.maxNumberOfMessages = maxNumberOfMessages;
    }

    /**
     * @return The number of milliseconds the polling thread must wait before trying to recover when an error occurs
     * (e.g. connection timeout)
     */
    public long getBackOffTime() {
        return this.backOffTime;
    }

    /**
     * The number of milliseconds the polling thread must wait before trying to recover when an error occurs
     * (e.g. connection timeout). Default is 10000 milliseconds.
     *
     * @param backOffTime in milliseconds
     */
    public void setBackOffTime(long backOffTime) {
        this.backOffTime = backOffTime;
    }

    /**
     * @return The number of milliseconds the {@link SqsMessageListenerContainer#stop(String)} method waits for a queue
     * to stop before interrupting the current thread. Default value is 10000 milliseconds (10 seconds).
     */
    public long getQueueStopTimeout() {
        return this.queueStopTimeout;
    }

    /**
     * The number of milliseconds the {@link SqsMessageListenerContainer#stop(String)} method waits for a queue
     * to stop before interrupting the current thread. Default value is 10000 milliseconds (10 seconds).
     *
     * @param queueStopTimeout in milliseconds
     */
    public void setQueueStopTimeout(long queueStopTimeout) {
        this.queueStopTimeout = queueStopTimeout;
    }

    /**
     * Configures the duration (in seconds) that the received messages are hidden from
     * subsequent poll requests after being retrieved from the system.
     *
     * @param visibilityTimeout the visibility timeout in seconds
     */
    public void setVisibilityTimeout(Integer visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    /**
     * Configures the wait timeout that the poll request will wait for new message to arrive if the are currently no
     * messages on the queue. Higher values will reduce poll request to the system significantly.
     *
     * @param waitTimeOut - the wait time out in seconds
     */
    public void setWaitTimeOut(Integer waitTimeOut) {
        this.waitTimeOut = waitTimeOut;
    }

    @Override
    protected void doInitialize() {

        for (SqsMessageHandler messageHandler : this.messageHandlers) {
            for (String queue : messageHandler.getDestinations()) {
                QueueAttributes queueAttributes = queueAttributes(queue, messageHandler);

                if (queueAttributes != null) {
                    this.registeredQueues.put(queue, queueAttributes);
                }
            }
        }

        this.runningStateByQueue = new ConcurrentHashMap<>(this.registeredQueues.size());
        for (String queueName : this.registeredQueues.keySet()) {
            this.runningStateByQueue.put(queueName, false);
        }

        this.scheduledFutureByQueue = new ConcurrentHashMap<>(this.registeredQueues.size());
    }

    @Nullable
    private QueueAttributes queueAttributes(String queue, SqsMessageHandler messageHandler) {
        String destinationUrl;
        try {
            destinationUrl = this.destinationResolver.resolveDestination(queue);
        } catch (DestinationResolutionException e) {
            log.warn("Ignoring queue with name '" + queue + "' as it does not exist.");
            return null;
        }

        GetQueueAttributesResult queueAttributes = this.amazonSqs.getQueueAttributes(new GetQueueAttributesRequest(destinationUrl)
                .withAttributeNames(QueueAttributeName.RedrivePolicy));
        boolean hasRedrivePolicy = queueAttributes.getAttributes().containsKey(QueueAttributeName.RedrivePolicy.toString());

        return new QueueAttributes(messageHandler, hasRedrivePolicy, destinationUrl, this.maxNumberOfMessages, this.visibilityTimeout, this.waitTimeOut);
    }

    @Override
    protected void doStart() {
        scheduleMessageListeners();
    }

    private void scheduleMessageListeners() {
        for (Map.Entry<String, QueueAttributes> registeredQueue : this.registeredQueues.entrySet()) {
            startQueue(registeredQueue.getKey(), registeredQueue.getValue());
        }
    }

    @Override
    protected void doStop() {
        notifyRunningQueuesToStop();
        waitForRunningQueuesToStop();
    }

    private void notifyRunningQueuesToStop() {
        for (Map.Entry<String, Boolean> runningStateByQueue : this.runningStateByQueue.entrySet()) {
            if (runningStateByQueue.getValue()) {
                stopQueue(runningStateByQueue.getKey());
            }
        }
    }

    private void waitForRunningQueuesToStop() {
        for (Map.Entry<String, Boolean> queueRunningState : this.runningStateByQueue.entrySet()) {
            String logicalQueueName = queueRunningState.getKey();
            Future<?> queueSpinningThread = this.scheduledFutureByQueue.get(logicalQueueName);

            if (queueSpinningThread != null) {
                try {
                    queueSpinningThread.get(getQueueStopTimeout(), TimeUnit.SECONDS);
                } catch (ExecutionException | TimeoutException e) {
                    log.warn("An exception occurred while stopping queue '" + logicalQueueName + "'", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    protected void doDestroy() {
        this.taskExecutor.destroy();
    }

    /**
     * Create a default TaskExecutor. Called if no explicit TaskExecutor has been specified.
     * <p>The default implementation builds a {@link org.springframework.core.task.SimpleAsyncTaskExecutor}
     * with the specified bean name (or the class name, if no bean name specified) as thread name prefix.
     *
     * @return a {@link org.springframework.core.task.SimpleAsyncTaskExecutor} configured with the thread name prefix
     * @see org.springframework.core.task.SimpleAsyncTaskExecutor#SimpleAsyncTaskExecutor(String)
     */
    protected ThreadPoolTaskExecutor createDefaultTaskExecutor() {
        String beanName = getBeanName();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(DEFAULT_THREAD_NAME_PREFIX);
        int spinningThreads = this.registeredQueues.size();

        if (spinningThreads > 0) {
            threadPoolTaskExecutor.setCorePoolSize(spinningThreads * 2);

            int maxNumberOfMessagePerBatch = this.maxNumberOfMessages;
            threadPoolTaskExecutor.setMaxPoolSize(spinningThreads * (maxNumberOfMessagePerBatch + 1));
        }

        // No use of a thread pool executor queue to avoid retaining message to long in memory
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.afterPropertiesSet();

        return threadPoolTaskExecutor;

    }

    protected void executeMessage(com.jorgenota.utils.messaging.Message<String, SqsMessageHeaders> stringMessage, String logicalQueueName) {
        QueueAttributes queueAttributes = this.registeredQueues.get(logicalQueueName);
        queueAttributes.getMessageHandler().handleMessage(stringMessage);
    }

    /**
     * Stops and waits until the specified queue has stopped. If the wait timeout specified by {@link SqsMessageListenerContainer#getQueueStopTimeout()}
     * is reached, the current thread is interrupted.
     *
     * @param logicalQueueName the name as defined on the listener method
     */
    public void stop(String logicalQueueName) {
        stopQueue(logicalQueueName);

        try {
            if (isRunning(logicalQueueName)) {
                Future<?> future = this.scheduledFutureByQueue.remove(logicalQueueName);
                if (future != null) {
                    future.get(this.queueStopTimeout, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            log.warn("Error stopping queue with name: '" + logicalQueueName + "'", e);
        }
    }

    protected void stopQueue(String logicalQueueName) {
        isTrue(this.runningStateByQueue.containsKey(logicalQueueName), "Queue with name %s does not exist", logicalQueueName);
        this.runningStateByQueue.put(logicalQueueName, false);
    }

    public void start(String logicalQueueName) {
        isTrue(this.runningStateByQueue.containsKey(logicalQueueName), "Queue with name %s does not exist", logicalQueueName);

        QueueAttributes queueAttributes = this.registeredQueues.get(logicalQueueName);
        startQueue(logicalQueueName, queueAttributes);
    }

    /**
     * Checks if the spinning thread for the specified queue {@code logicalQueueName} is still running (polling for new
     * messages) or not.
     *
     * @param logicalQueueName the name as defined on the listener method
     * @return {@code true} if the spinning thread for the specified queue is running otherwise {@code false}.
     */
    public boolean isRunning(String logicalQueueName) {
        Future<?> future = this.scheduledFutureByQueue.get(logicalQueueName);
        return future != null && !future.isCancelled() && !future.isDone();
    }

    protected void startQueue(String queueName, QueueAttributes queueAttributes) {
        if (this.runningStateByQueue.getOrDefault(queueName, false)) {
            return;
        }

        this.runningStateByQueue.put(queueName, true);
        Future<?> future = this.taskExecutor.submit(new AsynchronousMessageListener(queueName, queueAttributes));
        this.scheduledFutureByQueue.put(queueName, future);
    }

    private static class SignalExecutingRunnable implements Runnable {

        private final CountDownLatch countDownLatch;
        private final Runnable runnable;

        private SignalExecutingRunnable(CountDownLatch endSignal, Runnable runnable) {
            this.countDownLatch = endSignal;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                this.runnable.run();
            } finally {
                this.countDownLatch.countDown();
            }
        }
    }

    private static class QueueAttributes {

        private final SqsMessageHandler messageHandler;
        private final boolean hasRedrivePolicy;
        private final SqsMessageDeletionPolicy deletionPolicy;
        private final String destinationUrl;
        private final Integer maxNumberOfMessages;
        @Nullable
        private final Integer visibilityTimeout;
        @Nullable
        private final Integer waitTimeOut;

        QueueAttributes(SqsMessageHandler messageHandler, boolean hasRedrivePolicy, String destinationUrl,
                        Integer maxNumberOfMessages, @Nullable Integer visibilityTimeout,
                        @Nullable Integer waitTimeOut) {
            this.messageHandler = messageHandler;
            this.hasRedrivePolicy = hasRedrivePolicy;
            this.deletionPolicy = messageHandler.getDeletionPolicy();
            this.destinationUrl = destinationUrl;
            this.maxNumberOfMessages = maxNumberOfMessages;
            this.visibilityTimeout = visibilityTimeout;
            this.waitTimeOut = waitTimeOut;
        }

        boolean hasRedrivePolicy() {
            return this.hasRedrivePolicy;
        }

        ReceiveMessageRequest getReceiveMessageRequest() {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.destinationUrl).
                    withAttributeNames(RECEIVING_ATTRIBUTES).
                    withMessageAttributeNames(RECEIVING_MESSAGE_ATTRIBUTES);

            receiveMessageRequest.withMaxNumberOfMessages(this.maxNumberOfMessages);

            if (this.visibilityTimeout != null) {
                receiveMessageRequest.withVisibilityTimeout(this.visibilityTimeout);
            }

            if (this.waitTimeOut != null) {
                receiveMessageRequest.setWaitTimeSeconds(this.waitTimeOut);
            }

            return receiveMessageRequest;
        }

        SqsMessageDeletionPolicy getDeletionPolicy() {
            return this.deletionPolicy;
        }

        SqsMessageHandler getMessageHandler() {
            return this.messageHandler;
        }
    }

    private class AsynchronousMessageListener implements Runnable {

        private final QueueAttributes queueAttributes;
        private final String logicalQueueName;

        private AsynchronousMessageListener(String logicalQueueName, QueueAttributes queueAttributes) {
            this.logicalQueueName = logicalQueueName;
            this.queueAttributes = queueAttributes;
        }

        @Override
        public void run() {
            while (isQueueRunning()) {
                try {
                    ReceiveMessageResult receiveMessageResult = amazonSqs.receiveMessage(this.queueAttributes.getReceiveMessageRequest());
                    CountDownLatch messageBatchLatch = new CountDownLatch(receiveMessageResult.getMessages().size());
                    for (Message message : receiveMessageResult.getMessages()) {
                        if (isQueueRunning()) {
                            MessageExecutor messageExecutor = new MessageExecutor(this.logicalQueueName, message, this.queueAttributes);
                            taskExecutor.execute(new SignalExecutingRunnable(messageBatchLatch, messageExecutor));
                        } else {
                            messageBatchLatch.countDown();
                        }
                    }
                    try {
                        messageBatchLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } catch (Exception e) {
                    log.warn("An Exception occurred while polling queue '{}'. The failing operation will be " +
                            "retried in {} milliseconds", this.logicalQueueName, getBackOffTime(), e);
                    try {
                        //noinspection BusyWait
                        Thread.sleep(getBackOffTime());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            SqsMessageListenerContainer.this.scheduledFutureByQueue.remove(this.logicalQueueName);
        }

        private boolean isQueueRunning() {
            if (SqsMessageListenerContainer.this.runningStateByQueue.containsKey(this.logicalQueueName)) {
                return SqsMessageListenerContainer.this.runningStateByQueue.get(this.logicalQueueName);
            } else {
                log.warn("Stopped queue '" + this.logicalQueueName + "' because it was not listed as running queue.");
                return false;
            }
        }
    }

    private class MessageExecutor implements Runnable {

        private final Message message;
        private final String logicalQueueName;
        private final String queueUrl;
        private final boolean hasRedrivePolicy;
        private final SqsMessageDeletionPolicy deletionPolicy;

        private MessageExecutor(String logicalQueueName, Message message, QueueAttributes queueAttributes) {
            this.logicalQueueName = logicalQueueName;
            this.message = message;
            this.queueUrl = queueAttributes.getReceiveMessageRequest().getQueueUrl();
            this.hasRedrivePolicy = queueAttributes.hasRedrivePolicy();
            this.deletionPolicy = queueAttributes.getDeletionPolicy();
        }

        @Override
        public void run() {
            String receiptHandle = this.message.getReceiptHandle();
            com.jorgenota.utils.messaging.Message<String, SqsMessageHeaders> queueMessage = getMessageForExecution();
            try {
                executeMessage(queueMessage, logicalQueueName);
                applyDeletionPolicyOnSuccess(receiptHandle);
            } catch (MessagingException messagingException) {
                applyDeletionPolicyOnError(receiptHandle, messagingException);
            }
        }

        private void applyDeletionPolicyOnSuccess(String receiptHandle) {
            if (this.deletionPolicy == SqsMessageDeletionPolicy.ON_SUCCESS ||
                    this.deletionPolicy == SqsMessageDeletionPolicy.ALWAYS ||
                    this.deletionPolicy == SqsMessageDeletionPolicy.NO_REDRIVE) {
                deleteMessage(receiptHandle);
            }
        }

        private void applyDeletionPolicyOnError(String receiptHandle, MessagingException messagingException) {
            if (this.deletionPolicy == SqsMessageDeletionPolicy.ALWAYS ||
                    (this.deletionPolicy == SqsMessageDeletionPolicy.NO_REDRIVE && !this.hasRedrivePolicy)) {
                deleteMessage(receiptHandle);
            } else if (this.deletionPolicy == SqsMessageDeletionPolicy.ON_SUCCESS) {
                log.error("Exception encountered while processing message.", messagingException);
            }
        }

        private void deleteMessage(String receiptHandle) {
            amazonSqs.deleteMessageAsync(new DeleteMessageRequest(this.queueUrl, receiptHandle));
        }

        private com.jorgenota.utils.messaging.Message<String, SqsMessageHeaders> getMessageForExecution() {
            SqsMessageAcknowledgment acknowledgment = null;
            if (this.deletionPolicy == SqsMessageDeletionPolicy.NEVER) {
                String receiptHandle = this.message.getReceiptHandle();
                acknowledgment = new SqsMessageAcknowledgment(SqsMessageListenerContainer.this.amazonSqs, this.queueUrl, receiptHandle);
            }

            return createMessage(this.message, acknowledgment);
        }
    }
}
