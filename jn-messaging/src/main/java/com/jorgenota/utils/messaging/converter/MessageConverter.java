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

package com.jorgenota.utils.messaging.converter;

import com.jorgenota.utils.messaging.Message;
import com.jorgenota.utils.messaging.MessageHeaders;
import org.springframework.lang.Nullable;

/**
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @param <V> the type of the payload to convert
 *            <p>
 *            A converter to turn the payload of a {@link Message} from serialized form to a typed
 *            Object and vice versa. The message header may be
 *            used to specify the media type of the message content.
 */
public interface MessageConverter<T, U extends MessageHeaders, V> {

    /**
     * Convert the payload of a {@link Message} from a serialized form to a typed Object
     * of the specified target class.
     * <p>If the converter does not support the specified media type or cannot perform
     * the conversion, it should throw {@code MessageConversionException}.
     *
     * @param message     the input message. Never {@code null}
     * @param targetClass the target class for the conversion
     * @return the result of the conversion. Never {@code null}
     * @throws MessageConversionException if the converter cannot
     *                                    perform the conversion
     */
    V fromMessage(Message<T, U> message, Class<V> targetClass) throws MessageConversionException;

    /**
     * Create a {@link Message} whose payload is the result of converting the given
     * payload Object to serialized form. The optional attributes parameter
     * may contain additional attributes to be added
     * to the message.
     * <p>If the converter does not support the specified media type or cannot perform
     * the conversion, it should throw {@code MessageConversionException}.
     *
     * @param payload    the Object to convert. Never  {@code null}
     * @param headers optional headers for the message (may be {@code null})
     * @return the new message
     * @throws MessageConversionException if the converter does not support the
     *                                    Object type or cannot perform the conversion
     */
    Message<T, U> toMessage(V payload, @Nullable U headers) throws MessageConversionException;

}
