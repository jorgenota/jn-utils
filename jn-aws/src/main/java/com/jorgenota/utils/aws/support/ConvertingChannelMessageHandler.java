package com.jorgenota.utils.aws.support;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;

/**
 * @author Jorge Alonso
 */
public interface ConvertingChannelMessageHandler<T> extends ChannelMessageHandler {

    @Override
    default void handleMessage(Message<?> message) throws MessagingException {
        doHandleInternal((T) getMessageConverter().fromMessage(message, getTargetClass()));
    }

    void doHandleInternal(T payload);

    Class<T> getTargetClass();

    MessageConverter getMessageConverter();
}
