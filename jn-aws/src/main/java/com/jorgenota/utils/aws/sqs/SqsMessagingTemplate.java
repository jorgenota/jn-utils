package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.jorgenota.utils.messaging.AbstractMessageChannelMessagingSendingReceivingTemplate;
import com.jorgenota.utils.messaging.DestinationResolver;
import com.jorgenota.utils.messaging.converter.MessageConverter;

import static com.jorgenota.utils.messaging.converter.ConversionUtils.getDefaultMessageConverter;

public class SqsMessagingTemplate<V> extends AbstractMessageChannelMessagingSendingReceivingTemplate<String, SqsMessageHeaders, V, SqsMessageChannel> {

    private final AmazonSQSAsync amazonSqs;

    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs) {
        this(amazonSqs, getDefaultMessageConverter(String.class));
    }

    /**
     * Initializes the messaging template by configuring the message
     * converter. Uses the {@link DynamicSqsUrlDestinationResolver} with the default configuration to
     * resolve destination names.
     *
     * @param amazonSqs        The {@link AmazonSQS} client, cannot be {@code null}.
     * @param messageConverter A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs, MessageConverter<String, SqsMessageHeaders, V> messageConverter) {
        this(amazonSqs, new DynamicSqsUrlDestinationResolver(amazonSqs), messageConverter);
    }

    /**
     * Initializes the messaging template by configuring the destination resolver as well as the message
     * converter.
     *
     * @param amazonSqs           The {@link AmazonSQS} client, cannot be {@code null}.
     * @param destinationResolver A destination resolver implementation to resolve queue names into queue urls. The
     *                            destination resolver will be wrapped into a {@link com.jorgenota.utils.messaging.CachingDestinationResolverProxy}
     *                            to avoid duplicate queue url resolutions.
     * @param messageConverter    A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public SqsMessagingTemplate(AmazonSQSAsync amazonSqs, DestinationResolver<String> destinationResolver, MessageConverter<String, SqsMessageHeaders, V> messageConverter) {
        super(destinationResolver);
        this.amazonSqs = amazonSqs;
        setMessageConverter(messageConverter);
    }

    @Override
    protected SqsMessageChannel resolveMessageChannel(String physicalResourceIdentifier) {
        return new SqsMessageChannel(this.amazonSqs, physicalResourceIdentifier);
    }
}