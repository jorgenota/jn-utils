/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * Defines methods for sending messages.
 *
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 */
@FunctionalInterface
public interface MessageChannel<T, U extends MessageHeaders> {

    /**
     * Constant for sending a message without a prescribed timeout.
     */
    long INDEFINITE_TIMEOUT = -1;


    /**
     * Send a {@link Message} to this channel. If the message cannot be sent the
     * method must throw a MessagingException.
     * <p>This method may block indefinitely, depending on the implementation.
     * To provide a maximum wait time, use {@link #send(Message, long)}.
     *
     * @param message the message to send
     * @throws MessagingException if the message couldn't be sent
     */
    default void send(Message<T, U> message) throws MessagingException {
        send(message, INDEFINITE_TIMEOUT);
    }

    /**
     * Send a message, blocking until either the message is accepted or the
     * specified timeout period elapses. If the message cannot be sent the
     * method must throw a MessagingException.
     *
     * @param message the message to send
     * @param timeout the timeout in milliseconds or {@link #INDEFINITE_TIMEOUT}
     * @throws MessagingException if the message couldn't be sent
     */
    void send(Message<T, U> message, long timeout) throws MessagingException;

}
