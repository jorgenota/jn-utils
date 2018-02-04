package com.jorgenota.utils.retry;

import java.util.concurrent.ExecutionException;

/**
 * @author Jorge Alonso
 */
public final class FailedAttempt {
    private final ExecutionException e;
    private final long attemptNumber;
    private final long delaySinceFirstAttempt;

    public FailedAttempt(Exception cause, long attemptNumber, long delaySinceFirstAttempt) {
        this.e = new ExecutionException(cause);
        this.attemptNumber = attemptNumber;
        this.delaySinceFirstAttempt = delaySinceFirstAttempt;
    }

    public Throwable getExceptionCause() throws IllegalStateException {
        return e.getCause();
    }

    public long getAttemptNumber() {
        return attemptNumber;
    }

    public long getDelaySinceFirstAttempt() {
        return delaySinceFirstAttempt;
    }
}
