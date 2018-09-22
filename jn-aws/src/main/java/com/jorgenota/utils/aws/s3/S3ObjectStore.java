package com.jorgenota.utils.aws.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.jorgenota.utils.base.service.ObjectStore;
import com.jorgenota.utils.base.service.ServiceException;
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
public class S3ObjectStore implements ObjectStore {

    private final Retrier retrier;
    private final AmazonS3 s3;

    public S3ObjectStore(AmazonS3 s3) {
        this(s3, WaitStrategies.noWait(), StopStrategies.stopAfterAttempt(1));
    }

    public S3ObjectStore(AmazonS3 s3, int attemptNumber, long sleepTime) {
        this(s3, WaitStrategies.fixedWait(sleepTime), StopStrategies.stopAfterAttempt(attemptNumber));
    }

    public S3ObjectStore(AmazonS3 s3, WaitStrategy waitStrategy, StopStrategy stopStrategy) {
        this.s3 = notNull(s3, "S3 client mustn't be null");
        this.retrier = RetrierBuilder.newBuilder()
            .withWaitStrategy(waitStrategy)
            .withStopStrategy(stopStrategy)
            .failIfException(e -> (e instanceof SdkClientException) && !((SdkClientException) e).isRetryable())
            .build();
    }

    @Override
    public byte[] getObjectAsBytes(String bucketName, String key) throws ServiceException {
        try {
            return retrier.call(() -> getBytesInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    @Override
    public String getObjectAsString(String bucketName, String key) throws ServiceException {
        try {
            return retrier.call(() -> getStringInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    /**
     * Get object from S3 with the name storageName.
     *
     * @param bucketName name of the bucket containing the object to retrieve
     * @param key key of the object to retrieve
     * @return byte[] representing the object stored in the bucket
     */
    private byte[] getBytesInternal(String bucketName, String key) throws IOException, SdkClientException {
        try (S3Object object = s3.getObject(new GetObjectRequest(bucketName, key))) {
            return IOUtils.toByteArray(object.getObjectContent());
        }
    }

    /**
     * Get object from S3 with the name storageName.
     *
     * @param bucketName name of the bucket containing the object to retrieve
     * @param key key of the object to retrieve
     * @return string representing the object stored in the bucket
     */
    private String getStringInternal(String bucketName, String key) throws SdkClientException {
        return s3.getObjectAsString(bucketName, key);
    }

    @Override
    public void putObject(String bucketName, String key, byte[] content, String contentType) throws ServiceException {
        try {
            retrier.run(() -> putObjectInternal(bucketName, key, content, contentType));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    @Override
    public void putObject(String bucketName, String key, String content, String contentType) throws ServiceException {
        try {
            retrier.run(() -> putObjectInternal(bucketName, key, content.getBytes(StandardCharsets.UTF_8), contentType));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    private void putObjectInternal(String bucketName, String key, byte[] content, String contentType) throws SdkClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType(contentType);

        s3.putObject(bucketName, key, new ByteArrayInputStream(content), metadata);
    }

    @Override
    public void deleteObject(String bucketName, String key) throws ServiceException {
        try {
            retrier.run(() -> deleteObjectInternal(bucketName, key));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    @Override
    public void deleteObjects(String bucketName, Collection<String> keys) throws ServiceException {
        try {
            retrier.run(() -> deleteObjectsInternal(bucketName, keys));
        } catch (RetryException e) {
            throw new ServiceException(e.getCause());
        }
    }

    private void deleteObjectInternal(String bucketName, String key) throws SdkClientException {
        s3.deleteObject(bucketName, key);
    }

    private void deleteObjectsInternal(String bucketName, Collection<String> keys) throws SdkClientException {
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName);
        multiObjectDeleteRequest.setKeys(keys.stream()
                .map(DeleteObjectsRequest.KeyVersion::new).collect(Collectors.toList()));


        s3.deleteObjects(multiObjectDeleteRequest);
    }

    @Override
    public void deleteBucket(String bucketName) throws ServiceException {
        try {

            ObjectListing objectListing = s3.listObjects(bucketName);

            while (true) {
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    s3.deleteObject(bucketName, objectSummary.getKey());
                }

                if (objectListing.isTruncated()) {
                    objectListing = s3.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }

            VersionListing list = s3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
            for (S3VersionSummary s : list.getVersionSummaries()) {
                s3.deleteVersion(bucketName, s.getKey(), s.getVersionId());
            }

            s3.deleteBucket(bucketName);
        } catch (Exception e) {
            throw new ServiceException(e.getCause());
        }
    }
}
