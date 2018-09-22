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

package com.jorgenota.utils.aws.support;

import com.amazonaws.AmazonServiceException;
import com.jorgenota.utils.messaging.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Abstract base class for AWS {@link MessageChannel} implementations.
 *
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 */
public abstract class AbstractAwsMessageChannel<T, U extends MessageHeaders> implements MessageChannel<T, U> {

    @Override
    public final void send(Message<T, U> message, long timeout) {
        try {
            sendMessageAndWaitForResult(message, timeout);
        } catch (AmazonServiceException e) {
            throw new MessageDeliveryException(message, e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new MessageDeliveryException(message, e.getMessage(), e.getCause());
        } catch (TimeoutException e) {
            throw new MessageTimeoutException(message, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessageDeliveryException("Thread was interrupted while receiving a message", e);
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                throw (MessagingException) e;
            }
            throw new MessageDeliveryException(message, e.getMessage(), e);
        }
    }

    protected abstract void sendMessageAndWaitForResult(Message<T, U> message, long timeout) throws Exception;
}
