package com.jorgenota.utils.aws.sqs;

import org.springframework.messaging.MessageHeaders;

import java.util.Map;

/**
 * Specialization of the {@link MessageHeaders} class that allows to set an ID. This was done to support cases where the
 * ID sent by the producer must be restored on the consumer side for traceability.
 */
public class SqsMessageHeaders extends MessageHeaders {

    public static final String SQS_DELAY_HEADER = "delay";
    public static final String SQS_GROUP_ID_HEADER = "message-group-id";
    public static final String SQS_DEDUPLICATION_ID_HEADER = "message-deduplication-id";


    public SqsMessageHeaders(Map<String, Object> headers) {
        super(headers);

        if (headers.containsKey(ID)) {
            this.getRawHeaders().put(ID, headers.get(ID));
        }
    }
}
