package com.jorgenota.utils.retry;

import java.util.concurrent.ExecutionException;

public interface RetryExceptionHandler {
    void handleRetryException(RetryException e);

    void handleExecutionException(ExecutionException e);
}
