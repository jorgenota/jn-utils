package com.jorgenota.utils.base.messaging;


/**
 * A contract for processing a {@link Message} after it has been created, either
 * returning a modified (effectively new) message or returning the same.
 *
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @see MessageSendingOperations
 */
public interface MessagePostProcessor<T, U extends MessageHeaders> {

    /**
     * Process the given message.
     *
     * @param message the message to process
     * @return a post-processed variant of the message,
     * or simply the incoming message; never {@code null}
     */
    Message<T, U> postProcessMessage(Message<T, U> message);

}
