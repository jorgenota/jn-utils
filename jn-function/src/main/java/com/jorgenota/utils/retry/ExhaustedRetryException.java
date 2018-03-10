package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public class ExhaustedRetryException extends RetryException {

    public ExhaustedRetryException(FailedAttempt failedAttempt) {
        this("Retry exhausted after " + failedAttempt.getAttemptNumber() + " attempts.", failedAttempt);
    }

    public ExhaustedRetryException(String message, FailedAttempt failedAttempt) {
        super(message, failedAttempt);
    }

}
