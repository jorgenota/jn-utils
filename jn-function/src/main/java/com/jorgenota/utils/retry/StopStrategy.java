package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public interface StopStrategy {

    /**
     * Returns <code>true</code> if the retryer should stop retrying.
     *
     * @param failedAttempt the previous failed {@code Attempt}
     * @return <code>true</code> if the retryer must stop, <code>false</code> otherwise
     */
    boolean shouldStop(FailedAttempt failedAttempt);
}
