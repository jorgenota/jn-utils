package com.jorgenota.utils.base.service;

import java.util.Collection;

/**
 * @author Jorge Alonso
 */
public interface ObjectStore {

    /**
     * Get object from the store.
     *
     * @param storageName name of the storage containing the object to retrieve
     * @param key         key of the object to retrieve
     * @return byte[] representing the object stored in the bucket
     */
    byte[] getObjectAsBytes(String storageName, String key) throws ServiceException;

    /**
     * Get object from the store.
     *
     * @param storageName name of the storage containing the object to retrieve
     * @param key         key of the object to retrieve
     * @return string representing the object stored in the bucket
     */
    String getObjectAsString(String storageName, String key) throws ServiceException;

    void putObject(String storageName, String key, byte[] content, String contentType) throws ServiceException;

    void putObject(String storageName, String key, String content, String contentType) throws ServiceException;

    void deleteObject(String storageName, String key) throws ServiceException;

    void deleteObjects(String storageName, Collection<String> keys) throws ServiceException;

    void deleteBucket(String storageName) throws ServiceException;
}
