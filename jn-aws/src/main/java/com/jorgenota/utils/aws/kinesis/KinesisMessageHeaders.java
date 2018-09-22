package com.jorgenota.utils.aws.kinesis;

import com.jorgenota.utils.messaging.MessageHeaders;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Implementation of the {@link MessageHeaders} interface for Kinesis Messages.
 */
@Data
public class KinesisMessageHeaders implements MessageHeaders {

    /**
     * Determines which shard in the stream the data record is assigned to
     */
    @Nullable
    String partitionKey;
}
