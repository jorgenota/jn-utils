package com.jorgenota.utils.retry;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jorge Alonso
 */
@Slf4j
public abstract class DoerWithRetries<T, U, E extends Exception> {

    private static final RetryExceptionHandler defaultRetryExceptionHandler = new RetryExceptionHandler() {
        @Override
        public void handleRetryException(RetryException e) {
            if (e instanceof FailException) {
                log.error("Execution with retries failed", e.getCause());
            } else if (e instanceof SleepInterruptedException) {
                log.error("Execution interrupted while sleeping", e.getCause());
            } else {
                log.error("Execution with retries finished without success", e.getCause());
            }
        }
    };

    protected Retrier getRetrier() {
        return RetrierBuilder.getDefaultRetrier();
    }

    protected RetryExceptionHandler getRetryExceptionHandler() {
        return defaultRetryExceptionHandler;
    }

}
