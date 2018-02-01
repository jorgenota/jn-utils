package com.jorgenota.utils.retry;

/**
 * @author Jorge Alonso
 */
public interface WaitStrategy {

    /**
     * Returns the time, in milliseconds, to sleep before retrying.
     *
     * @param failedAttempt the previous failed {@code Attempt}
     * @return the sleep time before next attempt
     */
    long computeSleepTime(FailedAttempt failedAttempt);
}
