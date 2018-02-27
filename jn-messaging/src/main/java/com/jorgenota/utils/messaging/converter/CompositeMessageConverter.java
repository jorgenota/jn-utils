/*
 * Copyright 2002-2015 the original  author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.jorgenota.utils.base.Preconditions.notEmpty;

/**
 * A {@link MessageConverter} that delegates to a list of registered converters
 * to be invoked until one of them returns a non-null result.
 */
public class CompositeMessageConverter<T, U extends MessageHeaders, V> implements MessageConverter<T, U, V> {

    private final List<MessageConverter> converters;


    /**
     * Create an instance with the given converters.
     */
    public CompositeMessageConverter(Collection<MessageConverter> converters) {
        notEmpty(converters, "Converters must not be empty");
        this.converters = new ArrayList<MessageConverter>(converters);
    }


    @Override
    public V fromMessage(Message<T, U> message, Class<V> targetClass) {
        for (MessageConverter converter : getConverters()) {
            try {
                return (V) converter.fromMessage(message, targetClass);
            } catch (Exception e) {
                continue;
            }
        }
        throw new MessageConversionException("Converters couldn't successfully convert the message payload");
    }

    @Override
    public Message<T, U> toMessage(V payload, U attributes) {
        for (MessageConverter converter : getConverters()) {
            try {
                return converter.toMessage(payload, attributes);
            } catch (Exception e) {
                continue;
            }
        }
        throw new MessageConversionException("Converters couldn't successfully convert the payload");
    }

    /**
     * Return the underlying list of delegate converters.
     */
    public List<MessageConverter> getConverters() {
        return this.converters;
    }

    @Override
    public String toString() {
        return "CompositeMessageConverter[converters=" + getConverters() + "]";
    }

}
