package com.jorgenota.utils.retry;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author Jorge Alonso
 */
@Slf4j
public abstract class DoerWithRetries<T, U, E extends Exception> {

    private static final RetryExceptionHandler defaultRetryExceptionHandler = new RetryExceptionHandler() {
        @Override
        public void handleRetryException(RetryException e) {
            log.error("Execution with retries finished without success", e);
        }

        @Override
        public void handleExecutionException(ExecutionException e) {
            log.error("Execution with retries failed", e);
        }
    };

    protected Retrier getRetrier() {
        return RetrierBuilder.getDefaultRetrier();
    }

    protected RetryExceptionHandler getRetryExceptionHandler() {
        return defaultRetryExceptionHandler;
    }

}
