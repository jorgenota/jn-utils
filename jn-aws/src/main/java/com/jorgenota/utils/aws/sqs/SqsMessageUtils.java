package com.jorgenota.utils.aws.sqs;

import com.jorgenota.utils.aws.sqs.listener.SqsMessageAcknowledgment;
import com.jorgenota.utils.messaging.GenericMessage;
import com.jorgenota.utils.messaging.Message;
import org.springframework.lang.Nullable;

public final class SqsMessageUtils {

    private SqsMessageUtils() {
        // Avoid instantiation
    }

    public static Message<String, SqsMessageHeaders> createMessage(com.amazonaws.services.sqs.model.Message message) {
        return createMessage(message, null);
    }

    public static Message<String, SqsMessageHeaders> createMessage(
            com.amazonaws.services.sqs.model.Message message,
            @Nullable SqsMessageAcknowledgment messageAcknowledgment) {

        SqsMessageHeaders messageAttributes = new SqsMessageHeaders();
        messageAttributes.setMessageId(message.getMessageId());
        messageAttributes.setAttributes(message.getAttributes());
        messageAttributes.setMessageAttributes(message.getMessageAttributes());
        if (messageAcknowledgment != null) {
            messageAttributes.setMessageAcknowledgment(messageAcknowledgment);
        }
        return new GenericMessage<>(message.getBody(), new SqsMessageHeaders());
    }
}
