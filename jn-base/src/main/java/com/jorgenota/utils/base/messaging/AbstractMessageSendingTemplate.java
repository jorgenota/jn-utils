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

package com.jorgenota.utils.base.messaging;

import com.jorgenota.utils.base.messaging.converter.MessageConverter;
import com.jorgenota.utils.base.messaging.converter.SimpleMessageConverter;
import lombok.extern.slf4j.Slf4j;

import static com.jorgenota.utils.base.Preconditions.notNull;
import static com.jorgenota.utils.base.Preconditions.state;

/**
 * Abstract base class for implementations of {@link MessageSendingOperations}.
 */
@Slf4j
public abstract class AbstractMessageSendingTemplate<T, U extends MessageHeaders, V, D> implements MessageSendingOperations<T, U, V, D> {

    private volatile D defaultDestination;
    private volatile MessageConverter converter = new SimpleMessageConverter();

    /**
     * Return the configured default destination.
     */
    public D getDefaultDestination() {
        return this.defaultDestination;
    }

    /**
     * Configure the default destination to use in send methods that don't have
     * a destination argument. If a default destination is not configured, send methods
     * without a destination argument will raise an exception if invoked.
     */
    public void setDefaultDestination(D defaultDestination) {
        this.defaultDestination = defaultDestination;
    }

    /**
     * Return the configured {@link MessageConverter}.
     */
    public MessageConverter getMessageConverter() {
        return this.converter;
    }

    /**
     * Set the {@link MessageConverter} to use in {@code convertAndSend} methods.
     * <p>By default, {@link SimpleMessageConverter} is used.
     *
     * @param messageConverter the message converter to use
     */
    public void setMessageConverter(MessageConverter messageConverter) {
        this.converter = notNull(messageConverter, "MessageConverter must not be null");
    }

    @Override
    public void send(Message<T, U> message) {
        send(getRequiredDefaultDestination(), message);
    }

    protected final D getRequiredDefaultDestination() {
        state(this.defaultDestination != null, "No 'defaultDestination' configured");
        return this.defaultDestination;
    }

    @Override
    public void send(D destination, Message<T, U> message) {
        doSend(destination, message);
    }

    protected abstract void doSend(D destination, Message<T, U> message);


    @Override
    public void convertAndSend(V payload) throws MessagingException {
        convertAndSend(payload, null);
    }

    @Override
    public void convertAndSend(D destination, V payload) throws MessagingException {
        convertAndSend(destination, payload, (U) null);
    }

    @Override
    public void convertAndSend(D destination, V payload, U attributes) throws MessagingException {
        convertAndSend(destination, payload, attributes, null);
    }

    @Override
    public void convertAndSend(V payload, MessagePostProcessor<T, U> postProcessor) throws MessagingException {
        convertAndSend(getRequiredDefaultDestination(), payload, postProcessor);
    }

    @Override
    public void convertAndSend(D destination, V payload, MessagePostProcessor<T, U> postProcessor)
            throws MessagingException {

        convertAndSend(destination, payload, null, postProcessor);
    }

    @Override
    public void convertAndSend(D destination, V payload, U attributes,
                               MessagePostProcessor<T, U> postProcessor) throws MessagingException {

        Message<T, U> message = doConvert(payload, attributes, postProcessor);
        send(destination, message);
    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter}, wrap it as a message with the given
     * attributes and apply the given post processor.
     *
     * @param payload       the Object to use as payload
     * @param attributes    attributes for the message to send
     * @param postProcessor the post processor to apply to the message
     * @return the converted message
     */
    protected Message<T, U> doConvert(V payload, U attributes, MessagePostProcessor<T, U> postProcessor) {

        Message<T, U> message = getMessageConverter().toMessage(payload, attributes);
        if (postProcessor != null) {
            message = postProcessor.postProcessMessage(message);
        }
        return message;
    }

    /**
     * Provides access to the map of input attributes before a send operation.
     * Subclasses can modify the attributes and then return the same or a different map.
     * <p>This default implementation in this class returns the input map.
     *
     * @param attributes the attributes to send (or {@code null} if none)
     * @return the actual attributes to send (or {@code null} if none)
     */
    protected U processattributesToSend(U attributes) {
        return attributes;
    }

}