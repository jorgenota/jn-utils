/*
 * Copyright 2002-2013 the original author or authors.
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

import com.jorgenota.utils.messaging.converter.MessageConverter;
import org.springframework.lang.Nullable;

/**
 * Extends {@link MessageSendingOperations} and adds operations for sending messages
 * to a destination specified as a (resolvable) String name.
 */
public interface DestinationResolvingMessageSendingOperations<T, U extends MessageHeaders, V, D> extends MessageSendingOperations<T, U, V, D> {

    /**
     * Resolve the given destination name to a destination and send a message to it.
     *
     * @param destinationName the destination name to resolve
     * @param message         the message to send
     */
    void send(String destinationName, Message<T, U> message) throws MessagingException;

    /**
     * Resolve the given destination name to a destination, convert the payload Object
     * to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message and send it to the resolved destination.
     *
     * @param destinationName the destination name to resolve
     * @param payload         the Object to use as payload
     */
    void convertAndSend(String destinationName, V payload) throws MessagingException;

    /**
     * Resolve the given destination name to a destination, convert the payload
     * Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers and send it to the resolved
     * destination.
     *
     * @param destinationName the destination name to resolve
     * @param payload         the Object to use as payload
     * @param headers      headers for the message to send
     */
    void convertAndSend(String destinationName, V payload, @Nullable U headers)
            throws MessagingException;

    /**
     * Resolve the given destination name to a destination, convert the payload
     * Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message, apply the given post processor, and send the resulting
     * message to the resolved destination.
     *
     * @param destinationName the destination name to resolve
     * @param payload         the Object to use as payload
     * @param postProcessor   the post processor to apply to the message
     */
    void convertAndSend(String destinationName, V payload, MessagePostProcessor<T, U> postProcessor)
            throws MessagingException;

    /**
     * Resolve the given destination name to a destination, convert the payload
     * Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers, apply the given post processor,
     * and send the resulting message to the resolved destination.
     *
     * @param destinationName the destination name to resolve
     * @param payload         the Object to use as payload
     * @param headers      headers for the message to send
     * @param postProcessor   the post processor to apply to the message
     */
    void convertAndSend(String destinationName, V payload, @Nullable U headers,
                        MessagePostProcessor<T, U> postProcessor) throws MessagingException;

}
