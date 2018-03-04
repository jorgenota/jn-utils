package com.jorgenota.utils.aws.sqs;

import com.jorgenota.utils.aws.sqs.listener.SqsMessageAcknowledgment;
import com.jorgenota.utils.aws.utils.ConversionUtils;
import com.jorgenota.utils.messaging.GenericMessage;
import com.jorgenota.utils.messaging.Message;
import com.jorgenota.utils.messaging.converter.CompositeMessageConverter;
import com.jorgenota.utils.messaging.converter.MappingJackson2MessageConverter;
import com.jorgenota.utils.messaging.converter.MessageConverter;
import com.jorgenota.utils.messaging.converter.StringMessageConverter;

import java.util.ArrayList;
import java.util.List;

public final class SqsMessageUtils {

    private static final String RECEIPT_HANDLE_MESSAGE_ATTRIBUTE_NAME = "ReceiptHandle";
    private static final String MESSAGE_ID_MESSAGE_ATTRIBUTE_NAME = "MessageId";

    private SqsMessageUtils() {
        // Avoid instantiation
    }

    public static Message<String, SqsMessageHeaders> createMessage(com.amazonaws.services.sqs.model.Message message) {
        return createMessage(message, null);
    }

    public static Message<String, SqsMessageHeaders> createMessage(com.amazonaws.services.sqs.model.Message message, SqsMessageAcknowledgment messageAcknowledgment) {

        SqsMessageHeaders messageAttributes = new SqsMessageHeaders();
        messageAttributes.setMessageId(message.getMessageId());
        messageAttributes.setAttributes(message.getAttributes());
        messageAttributes.setMessageAttributes(message.getMessageAttributes());
        if (messageAcknowledgment != null) {
            messageAttributes.setMessageAcknowledgment(messageAcknowledgment);
        }
        return new GenericMessage<String, SqsMessageHeaders>(message.getBody(), new SqsMessageHeaders());
    }


    public static MessageConverter getMessageConverter(MessageConverter messageConverter) {
        StringMessageConverter stringMessageConverter = new StringMessageConverter();
        stringMessageConverter.setSerializedPayloadClass(String.class);

        List<MessageConverter> messageConverters = new ArrayList<>();
        if (messageConverter != null) {
            messageConverters.add(messageConverter);
        } else if (ConversionUtils.JACKSON_2_PRESENT) {
            MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
            mappingJackson2MessageConverter.setObjectMapper(Jackson2ObjectMapperBuilder.json().build());
            mappingJackson2MessageConverter.setSerializedPayloadClass(String.class);
            messageConverters.add(mappingJackson2MessageConverter);
        }
        messageConverters.add(stringMessageConverter);
        return new CompositeMessageConverter(messageConverters);
    }
}
