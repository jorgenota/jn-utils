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

package com.jorgenota.utils.messaging;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * An implementation of {@link Message} with a generic payload.
 * Once created, a GenericMessage is immutable.
 */
public class GenericMessage<T, U extends MessageHeaders> implements Message<T, U>, Serializable {

    private static final long serialVersionUID = 4268801052358035098L;

    private final T payload;
    @Nullable
    private final U attributes;

    /**
     * Create a new message with the given payload.
     *
     * @param payload the message payload (never {@code null})
     */
    public GenericMessage(T payload) {
        this(payload, null);
    }

    /**
     * Create a new message with the given payload and attributes.
     * The content of the given header map is copied.
     *
     * @param payload    the message payload (never {@code null})
     * @param attributes message attributes to use for initialization
     */
    public GenericMessage(T payload, @Nullable U attributes) {
        this.payload = notNull(payload, "Payload must not be null");
        this.attributes = attributes;
    }

    @Override
    public T getPayload() {
        return this.payload;
    }

    @Override
    @Nullable
    public U getAttributes() {
        return this.attributes;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericMessage)) {
            return false;
        }
        GenericMessage<?, ?> otherMsg = (GenericMessage<?, ?>) other;
        // Using nullSafeEquals for proper array equals comparisons
        return (ObjectUtils.nullSafeEquals(this.payload, otherMsg.payload) && ObjectUtils.nullSafeEquals(this.attributes, otherMsg.attributes));
    }

    public int hashCode() {
        // Using nullSafeHashCode for proper array hashCode handling
        return (ObjectUtils.nullSafeHashCode(this.payload) * 23 + ObjectUtils.nullSafeHashCode(this.attributes));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [payload=");
        if (this.payload instanceof byte[]) {
            sb.append("byte[").append(((byte[]) this.payload).length).append("]");
        } else {
            sb.append(this.payload);
        }
        sb.append(", attributes=").append(this.attributes).append("]");
        return sb.toString();
    }

}
