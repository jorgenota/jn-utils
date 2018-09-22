package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.*;
import com.jorgenota.utils.aws.support.AbstractAwsPollableMessageChannel;
import com.jorgenota.utils.messaging.Message;
import com.jorgenota.utils.messaging.MessageDeliveryException;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.jorgenota.utils.aws.sqs.SqsMessageUtils.createMessage;

public class SqsMessageChannel extends AbstractAwsPollableMessageChannel<String, SqsMessageHeaders> {

    private static final String ATTRIBUTE_NAMES = "All";
    private static final String MESSAGE_ATTRIBUTE_NAMES = "All";
    private final AmazonSQSAsync amazonSqs;
    private final String queueUrl;

    public SqsMessageChannel(AmazonSQSAsync amazonSqs, String queueUrl) {
        this.amazonSqs = amazonSqs;
        this.queueUrl = queueUrl;
    }

    @Override
    public void sendMessageAndWaitForResult(Message<String, SqsMessageHeaders> message, long timeout) throws Exception {
        SendMessageRequest request = createSendMessageRequest(message);
        if (timeout > 0) {
            Future<SendMessageResult> sendMessageFuture = this.amazonSqs.sendMessageAsync(request);
            sendMessageFuture.get(timeout, TimeUnit.MILLISECONDS);
        } else {
            this.amazonSqs.sendMessage(request);
        }
    }

    private SendMessageRequest createSendMessageRequest(Message<String, SqsMessageHeaders> message) {
        SendMessageRequest request = new SendMessageRequest(this.queueUrl, String.valueOf(message.getPayload()));

        SqsMessageHeaders headers = message.getHeaders();
        if (headers != null) {
            if (headers.getMessageGroupId() != null) {
                request.setMessageGroupId(headers.getMessageGroupId());
            }
            if (headers.getMessageDeduplicationId() != null) {
                request.setMessageDeduplicationId(headers.getMessageDeduplicationId());
            }
            if (headers.getDelaySeconds() != null) {
                request.setDelaySeconds(headers.getDelaySeconds());
            }
            if (headers.getMessageAttributes() != null) {
                request.setMessageAttributes(headers.getMessageAttributes());
            }
        }

        return request;
    }

    @Override
    public Message<String, SqsMessageHeaders> receiveInternal(long timeout) {
        ReceiveMessageResult result = this.amazonSqs.receiveMessage(createReceiveMessageRequest(timeout));
        List<com.amazonaws.services.sqs.model.Message> messages = result.getMessages();
        if (messages.isEmpty()) {
            throw new MessageDeliveryException("No message was received");
        }
        com.amazonaws.services.sqs.model.Message amazonMessage = messages.get(0);
        Message<String, SqsMessageHeaders> message = createMessage(amazonMessage);
        this.amazonSqs.deleteMessage(new DeleteMessageRequest(this.queueUrl, amazonMessage.getReceiptHandle()));
        return message;
    }

    private ReceiveMessageRequest createReceiveMessageRequest(long timeout) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(this.queueUrl).
                withMaxNumberOfMessages(1).
                withAttributeNames(ATTRIBUTE_NAMES).
                withMessageAttributeNames(MESSAGE_ATTRIBUTE_NAMES);
        if (timeout > 0) {
            request.setWaitTimeSeconds(Long.valueOf(timeout).intValue());
        }
        return request;
    }

}
