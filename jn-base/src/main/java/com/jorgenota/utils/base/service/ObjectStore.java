package com.jorgenota.utils.base.service;

import java.util.Collection;

/**
 * @author Jorge Alonso
 */
public interface ObjectStore {

    byte[] getObjectAsBytes(String bucketName, String key) throws ServiceException;

    String getObjectAsString(String bucketName, String key) throws ServiceException;

    void putObject(String bucketName, String key, byte[] content, String contentType) throws ServiceException;

    void putObject(String bucketName, String key, String content, String contentType) throws ServiceException;

    void deleteObject(String bucketName, String key) throws ServiceException;

    void deleteObjects(String bucketName, Collection<String> keys) throws ServiceException;

    void deleteBucket(String bucketName) throws ServiceException;
}
