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

import com.jorgenota.utils.base.messaging.Message;
import com.jorgenota.utils.base.messaging.MessageAttributes;

import java.nio.charset.Charset;

/**
 * A {@link MessageConverter} that supports MIME type "text/plain" with the
 * payload converted to and from a String.
 */
public class StringMessageConverter<T, U extends MessageAttributes> extends AbstractMessageConverter<T, U, String> {

    private final Charset charset;


    public StringMessageConverter() {
        this(Charset.forName("UTF-8"));
    }


    public StringMessageConverter(Charset charset) {
        super();
        this.charset = charset;
    }

    @Override
    public String convertFromInternal(Message<T, U> message, Class<String> targetClass) {
        Object payload = message.getPayload();
        return (payload instanceof String ? (String) payload : new String((byte[]) payload, charset));
    }

    @Override
    protected T convertToInternal(String payload, U attributes) {
        if (byte[].class == getSerializedPayloadClass()) {
            return (T) payload.getBytes(charset);
        }
        return (T) payload;
    }
}
