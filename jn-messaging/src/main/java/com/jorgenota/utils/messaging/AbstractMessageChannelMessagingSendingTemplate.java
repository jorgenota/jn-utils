package com.jorgenota.utils.messaging;

import org.springframework.lang.Nullable;

/**
 * Abstract base class for AWS services messaging templates
 */
public abstract class AbstractMessageChannelMessagingSendingTemplate<T, U extends MessageHeaders, V, D extends MessageChannel<T, U>> extends AbstractMessageSendingTemplate<T, U, V, D> implements DestinationResolvingMessageSendingOperations<T, U, V, D> {

    private final DestinationResolver<String> destinationResolver;

    protected AbstractMessageChannelMessagingSendingTemplate(DestinationResolver<String> destinationResolver) {
        this.destinationResolver = new CachingDestinationResolverProxy<>(destinationResolver);
    }

    public void setDefaultDestinationName(String defaultDestination) {
        super.setDefaultDestination(resolveMessageChannelByLogicalName(defaultDestination));
    }

    @Override
    protected void doSend(D destination, Message<T, U> message) {
        destination.send(message);
    }

    @Override
    public void send(String destinationName, Message<T, U> message) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        doSend(channel, message);
    }

    @Override
    public void convertAndSend(String destinationName, V payload) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload);
    }

    @Override
    public void convertAndSend(String destinationName, V payload, @Nullable U headers) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, headers);
    }

    @Override
    public void convertAndSend(String destinationName, V payload, MessagePostProcessor<T, U> postProcessor) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, postProcessor);
    }

    @Override
    public void convertAndSend(String destinationName, V payload, @Nullable U headers, MessagePostProcessor<T, U> postProcessor) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, headers, postProcessor);
    }

    protected D resolveMessageChannelByLogicalName(String destination) {
        String physicalResourceId = this.destinationResolver.resolveDestination(destination);
        return resolveMessageChannel(physicalResourceId);
    }

    protected abstract D resolveMessageChannel(String physicalResourceIdentifier);
}
