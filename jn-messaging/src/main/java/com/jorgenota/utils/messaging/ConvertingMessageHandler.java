package com.jorgenota.utils.messaging;


import com.jorgenota.utils.messaging.converter.MessageConverter;

/**
 * @author Jorge Alonso
 */
public interface ConvertingMessageHandler<T, U extends MessageHeaders, V> extends MessageHandler<T, U> {

    @Override
    default void handleMessage(Message<T, U> message) throws MessagingException {
        doHandleInternal(getMessageConverter().fromMessage(message, getTargetClass()));
    }

    void doHandleInternal(V payload);

    Class<V> getTargetClass();

    MessageConverter<T, U, V> getMessageConverter();
}
