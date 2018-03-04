/*
 * Copyright 2002-2014 the original author or authors.
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

import com.jorgenota.utils.messaging.GenericMessage;
import com.jorgenota.utils.messaging.Message;
import com.jorgenota.utils.messaging.MessageHeaders;
import org.springframework.lang.Nullable;

/**
 * A simple converter that simply unwraps the message payload as long as it matches the
 * expected target class. Or reversely, simply wraps the payload in a message.
 * <p>
 * <p>Note that this converter ignores any content type information that may be present in
 * message headers and should not be used if payload conversion is actually required.
 */
public class SimpleMessageConverter<T, U extends MessageHeaders, V> implements MessageConverter<T, U, V> {

    @Override
    @SuppressWarnings("unchecked")
    public V fromMessage(Message<T, U> message, Class<V> targetClass) throws MessageConversionException {
        try {
            return (V) message.getPayload();
        } catch (ClassCastException e) {
            throw new MessageConversionException("Error converting payload to class " + targetClass.getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<T, U> toMessage(V payload, @Nullable U attributes) throws MessageConversionException {
        try {
            return new GenericMessage<>((T) payload, attributes);
        } catch (ClassCastException e) {
            throw new MessageConversionException("Error converting payload from class " + payload.getClass().getName());
        }
    }
}
