package com.jorgenota.utils.aws.sqs.listener;

import com.jorgenota.utils.aws.support.ChannelMessageHandler;

/**
 * @author Jorge Alonso
 */
public interface SqsMessageHandler extends ChannelMessageHandler {

    String LOGICAL_RESOURCE_ID = "LogicalResourceId";
    String ACKNOWLEDGMENT = "Acknowledgment";

    public SqsMessageDeletionPolicy getDeletionPolicy();
}
