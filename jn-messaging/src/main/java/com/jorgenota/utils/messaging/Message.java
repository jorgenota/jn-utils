package com.jorgenota.utils.messaging;

/**
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @author Jorge Alonso
 */
public interface Message<T, U extends MessageHeaders> {
    /**
     * Return the message payload. Never {@code null}.
     */
    T getPayload();

    /**
     * Return the message attributes. May be {@code null}.
     */
    U getAttributes();
}
