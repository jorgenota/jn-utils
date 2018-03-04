/*
 * Copyright 2002-2017 the original author or authors.
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

/**
 * The base exception for any failures related to messaging.
 */
@SuppressWarnings("serial")
public class MessagingException extends RuntimeException {

    @Nullable
    private final Message<?, ?> failedMessage;


    public MessagingException(Message<?, ?> message) {
        super((String) null);
        this.failedMessage = message;
    }

    public MessagingException(String description) {
        super(description);
        this.failedMessage = null;
    }

    public MessagingException(String description, Throwable cause) {
        super(description, cause);
        this.failedMessage = null;
    }

    public MessagingException(Message<?, ?> message, String description) {
        super(description);
        this.failedMessage = message;
    }

    public MessagingException(Message<?, ?> message, Throwable cause) {
        super(null, cause);
        this.failedMessage = message;
    }

    public MessagingException(Message<?, ?> message, String description, Throwable cause) {
        super(description, cause);
        this.failedMessage = message;
    }

    @Nullable
    public Message<?, ?> getFailedMessage() {
        return this.failedMessage;
    }

    @Override
    public String toString() {
        return super.toString() + (this.failedMessage == null ? ""
                : (", failedMessage=" + this.failedMessage));
    }

}
