package com.jorgenota.utils.messaging.converter;

import com.jorgenota.utils.messaging.MessageHeaders;
import org.springframework.util.ClassUtils;

/**
 * @author Jorge Alonso
 */
public final class ConversionUtils {

    private static final boolean JACKSON_2_PRESENT = ClassUtils.isPresent(
            "com.fasterxml.jackson.databind.ObjectMapper", ConversionUtils.class.getClassLoader());

    public static <T, U extends MessageHeaders, V> MessageConverter<T, U, V> getDefaultMessageConverter(Class<T> payloadClass) {

        if (ConversionUtils.JACKSON_2_PRESENT) {
            MappingJackson2MessageConverter<T, U, V> mappingJackson2MessageConverter = new MappingJackson2MessageConverter<>();
            mappingJackson2MessageConverter.setSerializedPayloadClass(payloadClass);
            return mappingJackson2MessageConverter;
        }

        return new SimpleMessageConverter<>();
    }

}
