package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.jorgenota.utils.aws.support.ResourceIdResolver;
import com.jorgenota.utils.messaging.AbstractMessageChannelMessagingSendingReceivingTemplate;
import com.jorgenota.utils.messaging.DestinationResolver;
import com.jorgenota.utils.messaging.DestinationResolvingMessageReceivingOperations;
import com.jorgenota.utils.messaging.converter.CompositeMessageConverter;
import com.jorgenota.utils.messaging.converter.MessageConverter;
import com.jorgenota.utils.messaging.converter.StringMessageConverter;

/**
 * <b>IMPORTANT</b>: For the message conversion this class always tries to first use the {@link StringMessageConverter}
 * as it fits the underlying message channel type. If a message converter is set through the constructor then it is
 * added to a composite converter already containing the {@link StringMessageConverter}.
 * If {@link SqsMessagingTemplate#setMessageConverter(MessageConverter)} is used, then the {@link CompositeMessageConverter}
 * containing the {@link StringMessageConverter} will not be used anymore and the {@code String} payloads are also going
 * to be converted with the set converter.
 */
public class SqsMessagingTemplate extends AbstractMessageChannelMessagingSendingReceivingTemplate<SqsMessageChannel> implements DestinationResolvingMessageReceivingOperations<SqsMessageChannel> {

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

    protected void initMessageConverter(MessageConverter messageConverter) {

        setMessageConverter(SqsMessageUtils.getMessageConverter(messageConverter));
    }
}