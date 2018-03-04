package com.jorgenota.utils.aws.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.*;
import com.jorgenota.utils.aws.support.MessageAttributeDataTypes;
import com.jorgenota.utils.messaging.Message;
import com.jorgenota.utils.messaging.MessageHeaders;
import com.jorgenota.utils.messaging.PollableChannel;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.NumberUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.jorgenota.utils.aws.sqs.SqsMessageUtils.createMessage;

public class SqsMessageChannel implements PollableChannel<String, SqsMessageHeaders> {

    static final String ATTRIBUTE_NAMES = "All";
    private static final String MESSAGE_ATTRIBUTE_NAMES = "All";
    private final AmazonSQSAsync amazonSqs;
    private final String queueUrl;

    public SqsMessageChannel(AmazonSQSAsync amazonSqs, String queueUrl) {
        this.amazonSqs = amazonSqs;
        this.queueUrl = queueUrl;
    }

    public final boolean send(org.springframework.messaging.Message<?> message, long timeout) {
        Assert.notNull(message, "Message must not be null");
        AbstractMessageChannel.ChannelInterceptorChain chain = new AbstractMessageChannel.ChannelInterceptorChain();
        boolean sent = false;
        try {
            message = chain.applyPreSend(message, this);
            if (message == null) {
                return false;
            }
            sent = sendInternal(message, timeout);
            chain.applyPostSend(message, this, sent);
            chain.triggerAfterSendCompletion(message, this, sent, null);
            return sent;
        } catch (Exception ex) {
            chain.triggerAfterSendCompletion(message, this, sent, ex);
            if (ex instanceof MessagingException) {
                throw (MessagingException) ex;
            }
            throw new MessageDeliveryException(message, "Failed to send message to " + this, ex);
        } catch (Throwable err) {
            MessageDeliveryException ex2 =
                    new MessageDeliveryException(message, "Failed to send message to " + this, err);
            chain.triggerAfterSendCompletion(message, this, sent, ex2);
            throw ex2;
        }
    }

    protected boolean sendInternal(Message message, long timeout) {
        try {
            sendMessageAndWaitForResult(prepareSendMessageRequest(message), timeout);
        } catch (AmazonServiceException e) {
            throw new MessageDeliveryException(message, e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new MessageDeliveryException(message, e.getMessage(), e.getCause());
        } catch (TimeoutException e) {
            return false;
        }

        return true;
    }

    private SendMessageRequest prepareSendMessageRequest(Message<String, SqsMessageHeaders> message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest(this.queueUrl, String.valueOf(message.getPayload()));

        if (message.getHeaders().containsKey(SqsMessageHeaders.SQS_GROUP_ID_HEADER)) {
            sendMessageRequest.setMessageGroupId(message.getHeaders().get(SqsMessageHeaders.SQS_GROUP_ID_HEADER, String.class));
        }

        if (message.getHeaders().containsKey(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER)) {
            sendMessageRequest.setMessageDeduplicationId(message.getHeaders().get(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, String.class));
        }

        if (message.getHeaders().containsKey(SqsMessageHeaders.SQS_DELAY_HEADER)) {
            sendMessageRequest.setDelaySeconds(message.getHeaders().get(SqsMessageHeaders.SQS_DELAY_HEADER, Integer.class));
        }

        Map<String, MessageAttributeValue> messageAttributes = getMessageAttributes(message);
        if (!messageAttributes.isEmpty()) {
            sendMessageRequest.withMessageAttributes(messageAttributes);
        }

        return sendMessageRequest;
    }

    private void sendMessageAndWaitForResult(SendMessageRequest sendMessageRequest, long timeout) throws ExecutionException, TimeoutException {
        if (timeout > 0) {
            Future<SendMessageResult> sendMessageFuture = this.amazonSqs.sendMessageAsync(sendMessageRequest);

            try {
                sendMessageFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            this.amazonSqs.sendMessage(sendMessageRequest);
        }
    }

    private Map<String, MessageAttributeValue> getMessageAttributes(Message<String, SqsMessageHeaders> message) {
        HashMap<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        for (Map.Entry<String, Object> messageHeader : message.getHeaders().entrySet()) {
            String messageHeaderName = messageHeader.getKey();
            if (isSkipHeader(messageHeaderName)) {
                continue;
            }

            Object messageHeaderValue = messageHeader.getValue();

            if (MessageHeaders.CONTENT_TYPE.equals(messageHeaderName) && messageHeaderValue != null) {
                messageAttributes.put(messageHeaderName, getContentTypeMessageAttribute(messageHeaderValue));
            } else if (MessageHeaders.ID.equals(messageHeaderName) && messageHeaderValue != null) {
                messageAttributes.put(messageHeaderName, getStringMessageAttribute(messageHeaderValue.toString()));
            } else if (messageHeaderValue instanceof String) {
                messageAttributes.put(messageHeaderName, getStringMessageAttribute((String) messageHeaderValue));
            } else if (messageHeaderValue instanceof Number) {
                messageAttributes.put(messageHeaderName, getNumberMessageAttribute(messageHeaderValue));
            } else if (messageHeaderValue instanceof ByteBuffer) {
                messageAttributes.put(messageHeaderName, getBinaryMessageAttribute((ByteBuffer) messageHeaderValue));
            } else {
                this.logger.warn(String.format("Message header with name '%s' and type '%s' cannot be sent as" +
                        " message attribute because it is not supported by SQS.", messageHeaderName,
                    messageHeaderValue != null ? messageHeaderValue.getClass().getName() : ""));
            }
        }

        return messageAttributes;
    }

    private static boolean isSkipHeader(String headerName) {
        return SqsMessageHeaders.SQS_DELAY_HEADER.equals(headerName) ||
            SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER.equals(headerName) ||
            SqsMessageHeaders.SQS_GROUP_ID_HEADER.equals(headerName);
    }

    private MessageAttributeValue getBinaryMessageAttribute(ByteBuffer messageHeaderValue) {
        return new MessageAttributeValue().withDataType(MessageAttributeDataTypes.BINARY).withBinaryValue(messageHeaderValue);
    }

    private MessageAttributeValue getContentTypeMessageAttribute(Object messageHeaderValue) {
        if (messageHeaderValue instanceof MimeType) {
            return new MessageAttributeValue().withDataType(MessageAttributeDataTypes.STRING).withStringValue(messageHeaderValue.toString());
        } else if (messageHeaderValue instanceof String) {
            return new MessageAttributeValue().withDataType(MessageAttributeDataTypes.STRING).withStringValue((String) messageHeaderValue);
        }
        return null;
    }

    private MessageAttributeValue getStringMessageAttribute(String messageHeaderValue) {
        return new MessageAttributeValue().withDataType(MessageAttributeDataTypes.STRING).withStringValue(messageHeaderValue);
    }

    private MessageAttributeValue getNumberMessageAttribute(Object messageHeaderValue) {
        Assert.isTrue(NumberUtils.STANDARD_NUMBER_TYPES.contains(messageHeaderValue.getClass()), "Only standard number types are accepted as message header.");

        return new MessageAttributeValue().withDataType(MessageAttributeDataTypes.NUMBER + "." + messageHeaderValue.getClass().getName()).withStringValue(messageHeaderValue.toString());
    }

    @Override
    public Message<String, SqsMessageHeaders> receive() {
        return this.receive(0);
    }

    @Override
    public Message<String, SqsMessageHeaders> receive(long timeout) {
        ReceiveMessageResult receiveMessageResult = this.amazonSqs.receiveMessage(
            new ReceiveMessageRequest(this.queueUrl).
                withMaxNumberOfMessages(1).
                withWaitTimeSeconds(Long.valueOf(timeout).intValue()).
                withAttributeNames(ATTRIBUTE_NAMES).
                withMessageAttributeNames(MESSAGE_ATTRIBUTE_NAMES));
        if (receiveMessageResult.getMessages().isEmpty()) {
            return null;
        }
        com.amazonaws.services.sqs.model.Message amazonMessage = receiveMessageResult.getMessages().get(0);
        Message<String, SqsMessageHeaders> message = createMessage(amazonMessage);
        this.amazonSqs.deleteMessage(new DeleteMessageRequest(this.queueUrl, amazonMessage.getReceiptHandle()));
        return message;
    }

}
