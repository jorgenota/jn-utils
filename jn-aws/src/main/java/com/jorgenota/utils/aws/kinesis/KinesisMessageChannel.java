package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.jorgenota.utils.aws.support.AbstractAwsMessageChannel;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class KinesisMessageChannel extends AbstractAwsMessageChannel<byte[], KinesisMessageHeaders> {

    private final AmazonKinesisAsync amazonKinesis;
    private final String streamName;

    public KinesisMessageChannel(AmazonKinesisAsync amazonKinesis, String streamName) {
        this.amazonKinesis = amazonKinesis;
        this.streamName = streamName;
    }

    @Override
    public void sendMessageAndWaitForResult(com.jorgenota.utils.messaging.Message<byte[], KinesisMessageHeaders> message, long timeout) throws Exception {
        PutRecordRequest request = createPutRecordRequest(message);
        if (timeout > 0) {
            Future<PutRecordResult> resultFuture = this.amazonKinesis.putRecordAsync(request);
            resultFuture.get(timeout, TimeUnit.MILLISECONDS);
        } else {
            this.amazonKinesis.putRecord(request);
        }
    }

    private PutRecordRequest createPutRecordRequest(com.jorgenota.utils.messaging.Message<byte[], KinesisMessageHeaders> message) {
        PutRecordRequest request = new PutRecordRequest();
        request.setStreamName(streamName);
        request.setData(ByteBuffer.wrap(message.getPayload()));

        KinesisMessageHeaders headers = message.getHeaders();
        if (headers != null) {
            if (headers.getPartitionKey() != null) {
                request.setPartitionKey(headers.getPartitionKey());
            }
        }

        return request;
    }
}
