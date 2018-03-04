package com.jorgenota.utils.aws.support;

import com.jorgenota.utils.aws.sqs.SqsMessageUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.core.*;

import java.util.Map;

/**
 * Abstract base class for AWS services messaging templates
 */
public abstract class AbstractMessageChannelMessagingSendingTemplate<D extends MessageChannel> extends AbstractMessageSendingTemplate<D> implements DestinationResolvingMessageSendingOperations<D> {

    private final DestinationResolver<String> destinationResolver;

    protected AbstractMessageChannelMessagingSendingTemplate(DestinationResolver<String> destinationResolver) {
        this.destinationResolver = new CachingDestinationResolverProxy<>(destinationResolver);
    }

    public void setDefaultDestinationName(String defaultDestination) {
        super.setDefaultDestination(resolveMessageChannelByLogicalName(defaultDestination));
    }

    @Override
    protected void doSend(D destination, Message<?> message) {
        destination.send(message);
    }

    @Override
    public void send(String destinationName, Message<?> message) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        doSend(channel, message);
    }

    @Override
    public <T> void convertAndSend(String destinationName, T payload) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload);
    }

    @Override
    public <T> void convertAndSend(String destinationName, T payload, Map<String, Object> headers) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, headers);
    }

    @Override
    public <T> void convertAndSend(String destinationName, T payload, MessagePostProcessor postProcessor) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, postProcessor);
    }

    @Override
    public <T> void convertAndSend(String destinationName, T payload, Map<String, Object> headers, MessagePostProcessor postProcessor) throws MessagingException {
        D channel = resolveMessageChannelByLogicalName(destinationName);
        convertAndSend(channel, payload, headers, postProcessor);
    }

    protected D resolveMessageChannelByLogicalName(String destination) {
        String physicalResourceId = this.destinationResolver.resolveDestination(destination);
        return resolveMessageChannel(physicalResourceId);
    }

    protected void initMessageConverter(MessageConverter messageConverter) {

        setMessageConverter(SqsMessageUtils.getMessageConverter(messageConverter));
    }

    protected abstract D resolveMessageChannel(String physicalResourceIdentifier);
}
