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

package com.jorgenota.utils.messaging;

/**
 * Operations for receiving messages from a destination.
 *
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @param <V> the type of the payload to convert
 * @param <D> the type of destination to receive messages from
 */
public interface MessageReceivingOperations<T, U extends MessageHeaders, V, D> {

    /**
     * Receive a message from a default destination.
     *
     * @return the received message, possibly {@code null} if the message could not
     * be received, for example due to a timeout
     * @throws MessagingException if the message couldn't be received
     */
    Message<T, U> receive() throws MessagingException;

    /**
     * Receive a message from the given destination.
     *
     * @param destination the target destination
     * @return the received message, possibly {@code null} if the message could not
     * be received, for example due to a timeout
     * @throws MessagingException if the message couldn't be received
     */
    Message<T, U> receive(D destination) throws MessagingException;

    /**
     * Receive a message from a default destination and convert its payload to the
     * specified target class.
     *
     * @param targetClass the target class to convert the payload to
     * @return the converted payload of the reply message, possibly {@code null} if
     * the message could not be received, for example due to a timeout
     * @throws MessagingException if the message couldn't be received
     */
    V receiveAndConvert(Class<V> targetClass) throws MessagingException;

    /**
     * Receive a message from the given destination and convert its payload to the
     * specified target class.
     *
     * @param destination the target destination
     * @param targetClass the target class to convert the payload to
     * @return the converted payload of the reply message, possibly {@code null} if
     * the message could not be received, for example due to a timeout
     * @throws MessagingException if the message couldn't be received
     */
    V receiveAndConvert(D destination, Class<V> targetClass) throws MessagingException;
}
