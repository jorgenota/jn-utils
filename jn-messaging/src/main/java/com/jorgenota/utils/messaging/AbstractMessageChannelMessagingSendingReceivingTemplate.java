package com.jorgenota.utils.messaging;

/**
 * Abstract base class for AWS services messaging templates
 */
public abstract class AbstractMessageChannelMessagingSendingReceivingTemplate<T, U extends MessageHeaders, V, D extends PollableChannel<T, U>> extends AbstractMessageChannelMessagingSendingTemplate<T, U, V, D> implements DestinationResolvingMessageReceivingOperations<T, U, V, D> {

    protected AbstractMessageChannelMessagingSendingReceivingTemplate(DestinationResolver<String> destinationResolver) {
        super(destinationResolver);
    }


    @Override
    public Message<T, U> receive() throws MessagingException {
        return receive(getRequiredDefaultDestination());
    }

    @Override
    public Message<T, U> receive(D destination) throws MessagingException {
        return destination.receive();
    }

    @Override
    public V receiveAndConvert(Class<V> targetClass) throws MessagingException {
        return receiveAndConvert(getRequiredDefaultDestination(), targetClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V receiveAndConvert(D destination, Class<V> targetClass) throws MessagingException {
        Message<T, U> message = receive(destination);
        return (V) getMessageConverter().fromMessage(message, targetClass);
    }

    @Override
    public Message<T, U> receive(String destinationName) throws MessagingException {
        return resolveMessageChannelByLogicalName(destinationName).receive();
    }

    @Override
    public V receiveAndConvert(String destinationName, Class<V> targetClass) throws MessagingException {
        D destination = resolveMessageChannelByLogicalName(destinationName);
        return receiveAndConvert(destination, targetClass);
    }

}
