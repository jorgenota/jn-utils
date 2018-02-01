package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public class RetryException extends Exception {

    FailedAttempt failedAttempt;

    public RetryException(FailedAttempt failedAttempt) {
        this("Retrying failed to complete successfully after " + failedAttempt.getAttemptNumber() + " attempts.", failedAttempt);
    }

    public RetryException(String message, FailedAttempt failedAttempt) {
        super(message, failedAttempt.getExceptionCause());
        this.failedAttempt = failedAttempt;
    }

    /**
     * Returns the number of failed attempts
     *
     * @return the number of failed attempts
     */
    public long getNumberOfFailedAttempts() {
        return failedAttempt.getAttemptNumber();
    }

    /**
     * Returns the last failed attempt
     *
     * @return the last failed attempt
     */
    public FailedAttempt getFailedAttempt() {
        return failedAttempt;
    }
}
