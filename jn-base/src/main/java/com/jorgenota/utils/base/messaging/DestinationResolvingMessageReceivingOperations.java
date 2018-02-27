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

package com.jorgenota.utils.base.messaging;

/**
 * Extends {@link MessageReceivingOperations} and adds operations for receiving messages
 * from a destination specified as a (resolvable) String name.
 *
 * @see DestinationResolver
 */
public interface DestinationResolvingMessageReceivingOperations<T, U extends MessageHeaders, V, D> extends MessageReceivingOperations<T, U, V, D> {

    /**
     * Resolve the given destination name and receive a message from it.
     *
     * @param destinationName the destination name to resolve
     */
    Message<T, U> receive(String destinationName) throws MessagingException;

    /**
     * Resolve the given destination name, receive a message from it, convert the
     * payload to the specified target type.
     *
     * @param destinationName the destination name to resolve
     * @param targetClass     the target class for the converted payload
     */
    V receiveAndConvert(String destinationName, Class<V> targetClass) throws MessagingException;

}
