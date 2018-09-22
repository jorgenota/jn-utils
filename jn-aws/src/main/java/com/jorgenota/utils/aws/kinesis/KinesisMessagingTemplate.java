package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.jorgenota.utils.messaging.AbstractMessageChannelMessagingSendingTemplate;
import com.jorgenota.utils.messaging.DestinationResolver;
import com.jorgenota.utils.messaging.IdentityDestinationResolver;
import com.jorgenota.utils.messaging.converter.MessageConverter;

import static com.jorgenota.utils.messaging.converter.ConversionUtils.getDefaultMessageConverter;

public class KinesisMessagingTemplate<V> extends AbstractMessageChannelMessagingSendingTemplate<byte[], KinesisMessageHeaders, V, KinesisMessageChannel> {

    private final AmazonKinesisAsync amazonKinesis;

    public KinesisMessagingTemplate(AmazonKinesisAsync amazonKinesis) {
        this(amazonKinesis, getDefaultMessageConverter(byte[].class));
    }

    /**
     * Initializes the messaging template by configuring the message
     * converter. Uses the {@link IdentityDestinationResolver} with the default configuration to
     * resolve destination names.
     *
     * @param amazonKinesis    The {@link AmazonKinesis} client, cannot be {@code null}.
     * @param messageConverter A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public KinesisMessagingTemplate(AmazonKinesisAsync amazonKinesis, MessageConverter<byte[], KinesisMessageHeaders, V> messageConverter) {
        this(amazonKinesis, new IdentityDestinationResolver(), messageConverter);
    }

    /**
     * Initializes the messaging template by configuring the destination resolver as well as the message
     * converter.
     *
     * @param amazonKinesis       The {@link AmazonKinesis} client, cannot be {@code null}.
     * @param destinationResolver A destination resolver implementation to resolve queue names into queue urls. The
     *                            destination resolver will be wrapped into a {@link com.jorgenota.utils.messaging.CachingDestinationResolverProxy}
     *                            to avoid duplicate queue url resolutions.
     * @param messageConverter    A {@link MessageConverter} that is going to be added to the composite converter.
     */
    public KinesisMessagingTemplate(AmazonKinesisAsync amazonKinesis, DestinationResolver<String> destinationResolver, MessageConverter<byte[], KinesisMessageHeaders, V> messageConverter) {
        super(destinationResolver);
        this.amazonKinesis = amazonKinesis;
        setMessageConverter(messageConverter);
    }

    @Override
    protected KinesisMessageChannel resolveMessageChannel(String physicalResourceIdentifier) {
        return new KinesisMessageChannel(this.amazonKinesis, physicalResourceIdentifier);
    }
}