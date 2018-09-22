package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.jorgenota.utils.aws.sqs.listener.SqsMessageAcknowledgment;
import com.jorgenota.utils.messaging.MessageHeaders;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Implementation of the {@link MessageHeaders} interface for SQS Messages.
 */
@Data
public class SqsMessageHeaders implements MessageHeaders {

    @Nullable
    private String messageId;

    /**
     * This parameter applies only to FIFO (first-in-first-out) queues.
     * The tag that specifies that a message belongs to a specific message group. Messages that
     * belong to the same message group are processed in a FIFO manner (however,
     * messages in different message groups might be processed out of order)
     */
    @Nullable
    private String messageGroupId;

    /**
     * This parameter applies only to FIFO (first-in-first-out) queues.
     * The token used for deduplication of sent messages. If a message with a particular
     * MessageDeduplicationId is sent successfully, any messages sent with the same
     * MessageDeduplicationId are accepted successfully but aren't delivered during the
     * 5-minute deduplication interval.
     */
    @Nullable
    private String messageDeduplicationId;

    /**
     * The length of time, in seconds, for which to delay a specific message.
     * Valid values: 0 to 900. Maximum: 15 minutes. Messages with a positive DelaySeconds
     * value become available for processing after the delay period is finished.
     */
    @Nullable
    private Integer delaySeconds;

    @Nullable
    private Map<String, MessageAttributeValue> messageAttributes;

    @Nullable
    private SqsMessageAcknowledgment messageAcknowledgment;

    @Nullable
    private Map<String, String> attributes;
}
