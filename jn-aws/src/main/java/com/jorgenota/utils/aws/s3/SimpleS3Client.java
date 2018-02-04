package com.jorgenota.utils.aws.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.jorgenota.utils.base.ClientException;
import com.jorgenota.utils.retry.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.jorgenota.utils.base.Preconditions.notNull;


/**
 * @author Jorge Alonso
 */
public class SimpleS3Client {

    private final Retrier retrier;
    private final AmazonS3 s3;

    public SimpleS3Client(AmazonS3 s3) {
        this(s3, WaitStrategies.noWait(), StopStrategies.stopAfterAttempt(1));
    }

    public SimpleS3Client(AmazonS3 s3, int attemptNumber, long sleepTime) {
        this(s3, WaitStrategies.fixedWait(sleepTime), StopStrategies.stopAfterAttempt(attemptNumber));
    }

    public SimpleS3Client(AmazonS3 s3, WaitStrategy waitStrategy, StopStrategy stopStrategy) {
        this.s3 = notNull(s3, "S3 client mustn't be null");
        this.retrier = RetrierBuilder.newBuilder()
            .withWaitStrategy(waitStrategy)
            .withStopStrategy(stopStrategy)
            .failIfException(e -> (e instanceof SdkClientException) && !((SdkClientException) e).isRetryable())
            .build();
    }

    public byte[] getObjectAsBytes(String bucketName, String key) throws ClientException {
        try {
            return retrier.call(() -> getBytesInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    public String getObjectAsString(String bucketName, String key) throws ClientException {
        try {
            return retrier.call(() -> getStringInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    /**
     * Get object from S3 with the name storageName.
     *
     * @param bucketName
     * @param key
     * @return ByteArrayDataSource
     */
    private byte[] getBytesInternal(String bucketName, String key) throws IOException, SdkClientException {
        try (S3Object object = s3.getObject(new GetObjectRequest(bucketName, key))) {
            return IOUtils.toByteArray(object.getObjectContent());
        }
    }

    /**
     * Get object from S3 with the name storageName.
     *
     * @param bucketName
     * @param key
     * @return ByteArrayDataSource
     */
    private String getStringInternal(String bucketName, String key) throws IOException, SdkClientException {
        return s3.getObjectAsString(bucketName, key);
    }

    public void putObject(String bucketName, String key, byte[] content, String contentType) throws SdkClientException {
        try {
            retrier.run(() -> putObjectInternal(bucketName, key, content, contentType));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    public void putObject(String bucketName, String key, String content, String contentType) throws SdkClientException {
        try {
            retrier.run(() -> putObjectInternal(bucketName, key, content.getBytes(StandardCharsets.UTF_8), contentType));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    private void putObjectInternal(String bucketName, String key, byte[] content, String contentType) throws SdkClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType(contentType);

        s3.putObject(bucketName, key, new ByteArrayInputStream(content), metadata);
    }

    public void deleteObject(String bucketName, String key) throws SdkClientException {
        try {
            retrier.run(() -> deleteObjectInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    public void deleteObjects(String bucketName, String key) throws SdkClientException {
        try {
            retrier.run(() -> deleteObjectInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ClientException(e.getCause());
        }
    }

    private void deleteObjectInternal(String bucketName, String key) throws SdkClientException {
        s3.deleteObject(bucketName, key);
    }

    private void deleteObjectsInternal(String bucketName, Collection<String> keys) throws SdkClientException {
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName);
        multiObjectDeleteRequest.setKeys(keys.stream()
            .map(x -> new DeleteObjectsRequest.KeyVersion(x)).collect(Collectors.toList()));

        s3.deleteObjects(multiObjectDeleteRequest);
    }
}
