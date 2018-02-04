package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public class FailException extends RetryException {

    FailedAttempt failedAttempt;

    public FailException(FailedAttempt failedAttempt) {
        this("Retrying failed and was aborted after " + failedAttempt.getAttemptNumber() + " attempts.", failedAttempt);
    }

    public FailException(String message, FailedAttempt failedAttempt) {
        super(message, failedAttempt);
    }

}
