package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public class SleepInterruptedException extends RetryException {

    public SleepInterruptedException(FailedAttempt failedAttempt) {
        this("Thread interrupted while sleeping after " + failedAttempt.getAttemptNumber() + " attempts.", failedAttempt);
    }

    public SleepInterruptedException(String message, FailedAttempt failedAttempt) {
        super(message, failedAttempt);
    }

}
