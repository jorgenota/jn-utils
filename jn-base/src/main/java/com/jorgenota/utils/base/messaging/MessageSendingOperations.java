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

package com.jorgenota.utils.base.messaging;

import com.jorgenota.utils.base.messaging.converter.MessageConverter;

/**
 * Operations for sending messages to a destination.
 *
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @param <V> the type of the payload to convert
 * @param <D> the type of destination to receive messages from
 */
public interface MessageSendingOperations<T, U extends MessageHeaders, V, D> {

    /**
     * Send a message to a default destination.
     *
     * @param message the message to send
     */
    void send(Message<T, U> message) throws MessagingException;

    /**
     * Send a message to the given destination.
     *
     * @param destination the target destination
     * @param message     the message to send
     */
    void send(D destination, Message<T, U> message) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message and send it to a default destination.
     *
     * @param payload the Object to use as payload
     */
    void convertAndSend(V payload) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message and send it to the given destination.
     *
     * @param destination the target destination
     * @param payload     the Object to use as payload
     */
    void convertAndSend(D destination, V payload) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers and send it to
     * the given destination.
     *
     * @param destination the target destination
     * @param payload     the Object to use as payload
     * @param attributes  attributes for the message to send
     */
    void convertAndSend(D destination, V payload, U attributes) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message, apply the given post processor, and send
     * the resulting message to a default destination.
     *
     * @param payload       the Object to use as payload
     * @param postProcessor the post processor to apply to the message
     */
    void convertAndSend(V payload, MessagePostProcessor<T, U> postProcessor) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message, apply the given post processor, and send
     * the resulting message to the given destination.
     *
     * @param destination   the target destination
     * @param payload       the Object to use as payload
     * @param postProcessor the post processor to apply to the message
     */
    void convertAndSend(D destination, V payload, MessagePostProcessor<T, U> postProcessor) throws MessagingException;

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers, apply the given post processor,
     * and send the resulting message to the given destination.
     *
     * @param destination   the target destination
     * @param payload       the Object to use as payload
     * @param attributes    attributes for the message to send
     * @param postProcessor the post processor to apply to the message
     */
    void convertAndSend(D destination, V payload, U attributes, MessagePostProcessor<T, U> postProcessor)
            throws MessagingException;

}
