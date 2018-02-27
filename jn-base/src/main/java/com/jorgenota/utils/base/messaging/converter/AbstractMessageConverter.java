/*
 * Copyright 2002-2015 the original author or authors.
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

package com.jorgenota.utils.base.messaging.converter;

import com.jorgenota.utils.base.messaging.GenericMessage;
import com.jorgenota.utils.base.messaging.Message;
import com.jorgenota.utils.base.messaging.MessageHeaders;

import static com.jorgenota.utils.base.Preconditions.isTrue;

/**
 * Abstract base class for {@link MessageConverter} implementations including
 * support for common properties and a partial implementation of the conversion methods,
 * mainly to check if the converter supports the conversion based on the payload class
 * and MIME type.
 */
public abstract class AbstractMessageConverter<T, U extends MessageHeaders, V> implements MessageConverter<T, U, V> {

    private Class<?> serializedPayloadClass = byte[].class;


    /**
     * Construct an {@code AbstractMessageConverter}
     */
    protected AbstractMessageConverter() {
    }

    /**
     * Return the configured preferred serialization payload class.
     */
    public Class<?> getSerializedPayloadClass() {
        return this.serializedPayloadClass;
    }

    /**
     * Configure the preferred serialization class to use (byte[] or String) when
     * converting an Object payload to a {@link com.jorgenota.utils.base.messaging.Message}.
     * <p>The default value is byte[].
     *
     * @param payloadClass either byte[] or String
     */
    public void setSerializedPayloadClass(Class<T> payloadClass) {
        isTrue(byte[].class == payloadClass || String.class == payloadClass,
                "Payload class must be byte[] or String: " + payloadClass);
        this.serializedPayloadClass = payloadClass;
    }

    @Override
    public final V fromMessage(Message<T, U> message, Class<V> targetClass) {
        try {
            return convertFromInternal(message, targetClass);
        } catch (Exception e) {
            if (e instanceof MessageConversionException) throw (MessageConversionException) e;
            throw new MessageConversionException("Error converting message payload to class " + targetClass.getName(), e);
        }
    }

    @Override
    public final Message<T, U> toMessage(V payload, U attributes) {
        try {
            if (attributes == null) return null;
            T convertedPayload = convertToInternal(payload, attributes);
            return new GenericMessage<T, U>(convertedPayload, attributes);
        } catch (Exception e) {
            if (e instanceof MessageConversionException) throw (MessageConversionException) e;
            throw new MessageConversionException("Error converting payload to class " + payload.getClass().getName(), e);
        }
    }

    /**
     * Convert the message payload from serialized form to an Object.
     *
     * @param message     the input message
     * @param targetClass the target class for the conversion
     *                    e.g. the associated {@code MethodParameter} (may be {@code null}}
     * @return the result of the conversion
     * @throws Exception if the converter cannot perform the conversion
     */
    protected abstract V convertFromInternal(Message<T, U> message, Class<V> targetClass) throws Exception;

    /**
     * Convert the payload object to serialized form.
     *
     * @param payload    the Object to convert
     * @param attributes optional attributes for the message (may be {@code null})
     * @return the resulting payload for the message
     * @throws Exception if the converter does not support the Object type or the target media type
     */
    protected abstract T convertToInternal(V payload, U attributes) throws Exception;
}
