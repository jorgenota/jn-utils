package com.jorgenota.utils.retry;

public interface RetryExceptionHandler {
    void handleRetryException(RetryException e);
}
