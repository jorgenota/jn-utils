package com.jorgenota.utils.aws.sqs.listener;

import com.jorgenota.utils.aws.support.ChannelMessageHandler;

/**
 * @author Jorge Alonso
 */
public interface SqsMessageHandler extends ChannelMessageHandler {

    public SqsMessageDeletionPolicy getDeletionPolicy();
}
