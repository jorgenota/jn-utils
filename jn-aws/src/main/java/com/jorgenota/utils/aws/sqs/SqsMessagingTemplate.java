package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.jorgenota.utils.aws.support.AbstractMessageChannelMessagingSendingTemplate;
import com.jorgenota.utils.aws.support.ResourceIdResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.messaging.core.DestinationResolvingMessageReceivingOperations;

/**
 * <b>IMPORTANT</b>: For the message conversion this class always tries to first use the {@link StringMessageConverter}
 * as it fits the underlying message channel type. If a message converter is set through the constructor then it is
 * added to a composite converter already containing the {@link StringMessageConverter}.
 * If {@link SqsMessagingTemplate#setMessageConverter(MessageConverter)} is used, then the {@link CompositeMessageConverter}
 * containing the {@link StringMessageConverter} will not be used anymore and the {@code String} payloads are also going
 * to be converted with the set converter.
 */
public class SqsMessagingTemplate extends AbstractMessageChannelMessagingSendingTemplate<SqsMessageChannel> implements DestinationResolvingMessageReceivingOperations<SqsMessageChannel> {

    private final AmazonSQSAsync amazonSqs;

    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs) {
        this(amazonSqs, (ResourceIdResolver) null, null);
    }

    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs, ResourceIdResolver resourceIdResolver) {
        this(amazonSqs, resourceIdResolver, null);
    }

    /**
     * Initializes the messaging template by configuring the resource Id resolver as well as the message
     * converter. Uses the {@link DynamicSqsUrlDestinationResolver} with the default configuration to
     * resolve destination names.
     *
     * @param amazonSqs          The {@link AmazonSQS} client, cannot be {@code null}.
     * @param resourceIdResolver The {@link ResourceIdResolver} to be used for resolving logical queue names.
     * @param messageConverter   A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs, ResourceIdResolver resourceIdResolver, MessageConverter messageConverter) {
        this(amazonSqs, new DynamicSqsUrlDestinationResolver(amazonSqs, resourceIdResolver), messageConverter);
    }

    /**
     * Initializes the messaging template by configuring the destination resolver as well as the message
     * converter. Uses the {@link DynamicSqsUrlDestinationResolver} with the default configuration to
     * resolve destination names.
     *
     * @param amazonSqs           The {@link AmazonSQS} client, cannot be {@code null}.
     * @param destinationResolver A destination resolver implementation to resolve queue names into queue urls. The
     *                            destination resolver will be wrapped into a {@link org.springframework.messaging.core.CachingDestinationResolverProxy}
     *                            to avoid duplicate queue url resolutions.
     * @param messageConverter    A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs, DestinationResolver<String> destinationResolver, MessageConverter messageConverter) {
        super(destinationResolver);
        this.amazonSqs = amazonSqs;
        initMessageConverter(messageConverter);
    }

    @Override
    protected SqsMessageChannel resolveMessageChannel(String physicalResourceIdentifier) {
        return new SqsMessageChannel(this.amazonSqs, physicalResourceIdentifier);
    }

    @Override
    public Message<String> receive() throws MessagingException {
        return receive(getRequiredDefaultDestination());
    }

    @Override
    public Message<String> receive(SqsMessageChannel destination) throws MessagingException {
        return destination.receive();
    }

    @Override
    public <T> T receiveAndConvert(Class<T> targetClass) throws MessagingException {
        return receiveAndConvert(getRequiredDefaultDestination(), targetClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T receiveAndConvert(SqsMessageChannel destination, Class<T> targetClass) throws MessagingException {
        Message<String> message = receive(destination);
        if (message != null) {
            return (T) getMessageConverter().fromMessage(message, targetClass);
        } else {
            return null;
        }
    }

    @Override
    public Message<String> receive(String destinationName) throws MessagingException {
        return resolveMessageChannelByLogicalName(destinationName).receive();
    }

    @Override
    public <T> T receiveAndConvert(String destinationName, Class<T> targetClass) throws MessagingException {
        SqsMessageChannel channel = resolveMessageChannelByLogicalName(destinationName);
        return receiveAndConvert(channel, targetClass);
    }
}