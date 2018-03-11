package com.jorgenota.utils.messaging;

import org.springframework.lang.Nullable;

/**
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message headers. Must be a subclass of MessageHeaders
 * @author Jorge Alonso
 */
public interface Message<T, U extends MessageHeaders> {
    /**
     * Return the message payload. Never {@code null}.
     */
    T getPayload();

    /**
     * Return the message headers. May be {@code null}.
     */
    @Nullable
    U getHeaders();
}
