package com.jorgenota.utils.base.service;

/**
 * @author Jorge Alonso
 */
public class ServiceException extends RuntimeException {
    public ServiceException(Throwable t) {
        super(t);
    }

    public ServiceException(String message, Throwable t) {
        super(message, t);
    }
}
