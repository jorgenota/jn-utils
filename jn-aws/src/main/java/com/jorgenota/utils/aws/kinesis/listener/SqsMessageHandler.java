package com.jorgenota.utils.aws.kinesis.listener;

import com.jorgenota.utils.aws.sqs.SqsMessageHeaders;
import com.jorgenota.utils.messaging.DestinationMessageHandler;

/**
 * @author Jorge Alonso
 */
public interface SqsMessageHandler extends DestinationMessageHandler<String, SqsMessageHeaders> {

    SqsMessageDeletionPolicy getDeletionPolicy();
}
