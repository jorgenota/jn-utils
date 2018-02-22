package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.jorgenota.utils.aws.support.MessageAttributeDataTypes;
import com.jorgenota.utils.aws.utils.ConversionUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.MimeType;
import org.springframework.util.NumberUtils;

import java.util.*;

public final class QueueMessageUtils {

    private static final String RECEIPT_HANDLE_MESSAGE_ATTRIBUTE_NAME = "ReceiptHandle";
    private static final String MESSAGE_ID_MESSAGE_ATTRIBUTE_NAME = "MessageId";

    private QueueMessageUtils() {
        // Avoid instantiation
    }

    public static Message<String> createMessage(com.amazonaws.services.sqs.model.Message message) {
        return createMessage(message, Collections.emptyMap());
    }

    public static Message<String> createMessage(com.amazonaws.services.sqs.model.Message message, Map<String, Object> additionalHeaders) {
        HashMap<String, Object> messageHeaders = new HashMap<>();
        messageHeaders.put(MESSAGE_ID_MESSAGE_ATTRIBUTE_NAME, message.getMessageId());
        messageHeaders.put(RECEIPT_HANDLE_MESSAGE_ATTRIBUTE_NAME, message.getReceiptHandle());

        messageHeaders.putAll(additionalHeaders);
        messageHeaders.putAll(getAttributesAsMessageHeaders(message));
        messageHeaders.putAll(getMessageAttributesAsMessageHeaders(message));

        return new GenericMessage<>(message.getBody(), new SqsMessageHeaders(messageHeaders));
    }

    private static Map<String, Object> getAttributesAsMessageHeaders(com.amazonaws.services.sqs.model.Message message) {
        Map<String, Object> messageHeaders = new HashMap<>();
        for (Map.Entry<String, String> attributeKeyValuePair : message.getAttributes().entrySet()) {
            messageHeaders.put(attributeKeyValuePair.getKey(), attributeKeyValuePair.getValue());
        }

        return messageHeaders;
    }

    private static Map<String, Object> getMessageAttributesAsMessageHeaders(com.amazonaws.services.sqs.model.Message message) {
        Map<String, Object> messageHeaders = new HashMap<>();
        for (Map.Entry<String, MessageAttributeValue> messageAttribute : message.getMessageAttributes().entrySet()) {
            if (MessageHeaders.CONTENT_TYPE.equals(messageAttribute.getKey())) {
                messageHeaders.put(MessageHeaders.CONTENT_TYPE, MimeType.valueOf(messageAttribute.getValue().getStringValue()));
            } else if (MessageHeaders.ID.equals(messageAttribute.getKey())) {
                messageHeaders.put(MessageHeaders.ID, UUID.fromString(messageAttribute.getValue().getStringValue()));
            } else if (MessageAttributeDataTypes.STRING.equals(messageAttribute.getValue().getDataType())) {
                messageHeaders.put(messageAttribute.getKey(), messageAttribute.getValue().getStringValue());
            } else if (messageAttribute.getValue().getDataType().startsWith(MessageAttributeDataTypes.NUMBER)) {
                Object numberValue = getNumberValue(messageAttribute.getValue());
                if (numberValue != null) {
                    messageHeaders.put(messageAttribute.getKey(), numberValue);
                }
            } else if (MessageAttributeDataTypes.BINARY.equals(messageAttribute.getValue().getDataType())) {
                messageHeaders.put(messageAttribute.getKey(), messageAttribute.getValue().getBinaryValue());
            }
        }

        return messageHeaders;
    }

    private static Object getNumberValue(MessageAttributeValue value) {
        String numberType = value.getDataType().substring(MessageAttributeDataTypes.NUMBER.length() + 1);
        try {
            Class<? extends Number> numberTypeClass = Class.forName(numberType).asSubclass(Number.class);
            return NumberUtils.parseNumber(value.getStringValue(), numberTypeClass);
        } catch (ClassNotFoundException e) {
            throw new MessagingException(String.format("Message attribute with value '%s' and data type '%s' could not be converted " +
                "into a Number because target class was not found.", value.getStringValue(), value.getDataType()), e);
        }
    }

    public static MessageConverter getMessageConverter(MessageConverter messageConverter) {
        StringMessageConverter stringMessageConverter = new StringMessageConverter();
        stringMessageConverter.setSerializedPayloadClass(String.class);

        List<MessageConverter> messageConverters = new ArrayList<>();
        messageConverters.add(stringMessageConverter);

        if (messageConverter != null) {
            messageConverters.add(messageConverter);
        } else if (ConversionUtils.JACKSON_2_PRESENT) {
            MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
            mappingJackson2MessageConverter.setObjectMapper(Jackson2ObjectMapperBuilder.json().build());
            mappingJackson2MessageConverter.setSerializedPayloadClass(String.class);
            messageConverters.add(mappingJackson2MessageConverter);
        }
        return new CompositeMessageConverter(messageConverters);
    }
}
