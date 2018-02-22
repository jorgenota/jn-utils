package com.jorgenota.utils.aws.sqs.listener;

import com.jorgenota.utils.aws.sqs.QueueMessageUtils;
import com.jorgenota.utils.aws.support.ConvertingChannelMessageHandler;
import com.jorgenota.utils.base.Preconditions;
import org.springframework.messaging.converter.MessageConverter;

import java.util.Set;

/**
 * @author Jorge Alonso
 */
public abstract class AbstractSqsMessageHandler<T> implements SqsMessageHandler, ConvertingChannelMessageHandler {

    private final MessageConverter messageConverter;
    private final SqsMessageDeletionPolicy deletionPolicy;
    private final Set<String> channels;

    public AbstractSqsMessageHandler(Set<String> channels) {
        this(null, null, channels);
    }

    public AbstractSqsMessageHandler(SqsMessageDeletionPolicy deletionPolicy, MessageConverter messageConverter, Set<String> channels) {
        this.deletionPolicy = (deletionPolicy != null) ? deletionPolicy : SqsMessageDeletionPolicy.NO_REDRIVE;
        this.messageConverter = QueueMessageUtils.getMessageConverter(messageConverter);
        this.channels = Preconditions.notNull(channels, "channels mustn't be null");
    }

    @Override
    public SqsMessageDeletionPolicy getDeletionPolicy() {
        return this.deletionPolicy;
    }

    @Override
    public Set<String> getChannels() {
        return this.channels;
    }

    @Override
    public MessageConverter getMessageConverter() {
        return this.messageConverter;
    }
}
