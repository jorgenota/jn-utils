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

/**
 * A {@link MessageChannel} from which messages may be actively received through polling.
 */
public interface PollableChannel<T, U extends MessageHeaders> extends MessageChannel<T, U> {

    /**
     * Receive a message from this channel, blocking indefinitely if necessary.
     *
     * @return the next available {@link Message} or {@code null} if interrupted
     * @throws MessagingException if an error occurs trying to receive the message
     */
    default Message<T, U> receive() throws MessagingException {
        return receive(INDEFINITE_TIMEOUT);
    }

    /**
     * Receive a message from this channel, blocking until either a message is available
     * or the specified timeout period elapses.
     *
     * @param timeout the timeout in milliseconds or {@link MessageChannel#INDEFINITE_TIMEOUT}.
     * @return the next available {@link Message}
     * period elapses or the message reception is interrupted
     * @throws MessagingException if an error occurs trying to receive the message
     */
    Message<T, U> receive(long timeout) throws MessagingException;

}
