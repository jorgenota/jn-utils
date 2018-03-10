package com.jorgenota.utils.retry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jorge Alonso
 */
class StopStrategiesTests {

    @Test
    void testNeverStop() {
        assertThat(StopStrategies.neverStop().shouldStop(failedAttempt(3, 6546L))).isFalse();
    }

    @Test
    void testStopAfterAttempt() {
        assertThat(StopStrategies.stopAfterAttempt(3).shouldStop(failedAttempt(2, 6546L))).isFalse();
        assertThat(StopStrategies.stopAfterAttempt(3).shouldStop(failedAttempt(3, 6546L))).isTrue();
        assertThat(StopStrategies.stopAfterAttempt(3).shouldStop(failedAttempt(4, 6546L))).isTrue();
    }

    @Test
    void testStopAfterDelay() {
        assertThat(StopStrategies.stopAfterDelay(1000L).shouldStop(failedAttempt(2, 999L))).isFalse();
        assertThat(StopStrategies.stopAfterDelay(1000L).shouldStop(failedAttempt(2, 1000L))).isTrue();
        assertThat(StopStrategies.stopAfterDelay(1000L).shouldStop(failedAttempt(2, 1001L))).isTrue();
    }

    private FailedAttempt failedAttempt(long attemptNumber, long delaySinceFirstAttempt) {
        return new FailedAttempt(new RuntimeException(), attemptNumber, delaySinceFirstAttempt);
    }
}