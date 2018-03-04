package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jorgenota.utils.base.service.ServiceException;
import com.jorgenota.utils.retry.*;

import java.nio.ByteBuffer;

import static com.jorgenota.utils.base.ObjectMappingUtils.OBJECT_MAPPER;
import static com.jorgenota.utils.base.Preconditions.notNull;


/**
 * @author Jorge Alonso
 */
public class SimpleKinesisClient {

    private final Retrier retrier;
    private final AmazonKinesis kinesis;

    public SimpleKinesisClient(AmazonKinesis kinesis) {
        this(kinesis, WaitStrategies.noWait(), StopStrategies.stopAfterAttempt(1));
    }

    public SimpleKinesisClient(AmazonKinesis kinesis, int attemptNumber, long sleepTime) {
        this(kinesis, WaitStrategies.fixedWait(sleepTime), StopStrategies.stopAfterAttempt(attemptNumber));
    }

    public SimpleKinesisClient(AmazonKinesis kinesis, WaitStrategy waitStrategy, StopStrategy stopStrategy) {
        this.kinesis = notNull(kinesis, "Kinesis client mustn't be null");
        this.retrier = RetrierBuilder.newBuilder()
            .withWaitStrategy(waitStrategy)
            .withStopStrategy(stopStrategy)
            .failIfException(e -> (e instanceof SdkClientException) && !((SdkClientException) e).isRetryable())
            .build();
    }

    public void putObject(String streamName, String partitionKey, Object content) throws SdkClientException {
        byte[] bytes = new byte[0];
        try {
            bytes = OBJECT_MAPPER.writeValueAsBytes(content);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e.getCause());
        }

        putObject(streamName, partitionKey, bytes);
    }


    public void putObject(String streamName, String partitionKey, byte[] content) throws ServiceException {
        try {
            retrier.run(() -> putObjectInternal(streamName, partitionKey, content));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    private void putObjectInternal(String streamName, String partitionKey, byte[] content) throws SdkClientException {
        PutRecordRequest request = new PutRecordRequest();
        request.setStreamName(streamName);
        request.setPartitionKey(partitionKey);
        request.setData(ByteBuffer.wrap(content));

        kinesis.putRecord(request);
    }
}
