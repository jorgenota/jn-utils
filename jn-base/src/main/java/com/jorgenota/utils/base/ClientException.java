package com.jorgenota.utils.base;

/**
 * @author Jorge Alonso
 */
public class ClientException extends RuntimeException {
    public ClientException(Throwable t) {
        super(t);
    }

    public ClientException(String message, Throwable t) {
        super(message, t);
    }
}
