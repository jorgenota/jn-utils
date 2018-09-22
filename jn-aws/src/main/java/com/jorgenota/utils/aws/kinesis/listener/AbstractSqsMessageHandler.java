package com.jorgenota.utils.aws.kinesis.listener;

import com.jorgenota.utils.aws.sqs.SqsMessageHeaders;
import com.jorgenota.utils.base.Preconditions;
import com.jorgenota.utils.messaging.ConvertingMessageHandler;
import com.jorgenota.utils.messaging.converter.MessageConverter;

import java.util.Set;

import static com.jorgenota.utils.messaging.converter.ConversionUtils.getDefaultMessageConverter;

/**
 * @author Jorge Alonso
 */
public abstract class AbstractSqsMessageHandler<V> implements SqsMessageHandler, ConvertingMessageHandler<String, SqsMessageHeaders, V> {

    private final MessageConverter<String, SqsMessageHeaders, V> messageConverter;
    private final SqsMessageDeletionPolicy deletionPolicy;
    private final Set<String> channels;

    public AbstractSqsMessageHandler(Set<String> channels) {
        this(SqsMessageDeletionPolicy.NO_REDRIVE, getDefaultMessageConverter(String.class), channels);
    }

    public AbstractSqsMessageHandler(SqsMessageDeletionPolicy deletionPolicy, MessageConverter<String, SqsMessageHeaders, V> messageConverter, Set<String> channels) {
        this.deletionPolicy = Preconditions.notNull(deletionPolicy, "deletionPolicy mustn't be null");
        this.messageConverter = Preconditions.notNull(messageConverter, "messageConverter mustn't be null");
        this.channels = Preconditions.notNull(channels, "channels mustn't be null");
    }

    @Override
    public SqsMessageDeletionPolicy getDeletionPolicy() {
        return this.deletionPolicy;
    }

    @Override
    public Set<String> getDestinations() {
        return this.channels;
    }

    @Override
    public MessageConverter<String, SqsMessageHeaders, V> getMessageConverter() {
        return this.messageConverter;
    }
}
