package com.jorgenota.utils.retry;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jorge Alonso
 */
class WaitStrategiesTests {


    @Test
    void testNoWait() {
        WaitStrategy noWait = WaitStrategies.noWait();
        assertThat(noWait.computeSleepTime(failedAttempt(18, 9879L))).isEqualTo(0);
    }

    @Test
    void testFixedWait() {
        WaitStrategy fixedWait = WaitStrategies.fixedWait(1000L);
        assertThat(fixedWait.computeSleepTime(failedAttempt(12, 6546L))).isEqualTo(1000L);
    }

    @Test
    void testIncrementingWait() {
        WaitStrategy incrementingWait = WaitStrategies.incrementingWait(500L, 100L);
        assertThat(incrementingWait.computeSleepTime(failedAttempt(1, 6546L))).isEqualTo(500L);
        assertThat(incrementingWait.computeSleepTime(failedAttempt(3, 6546L))).isEqualTo(700L);
    }

    @Test
    void testRandomWait() {
        WaitStrategy randomWait = WaitStrategies.randomWait(1900L, 2000L);
        Set<Long> times = new HashSet<>();
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        assertThat(times.size()).isGreaterThan(1); // if not, the random is not random
        for (long time : times) {
            assertThat(time).isGreaterThanOrEqualTo(1900L);
            assertThat(time).isLessThanOrEqualTo(2000L);
        }
    }

    @Test
    void testRandomWaitWithoutMinimum() {
        WaitStrategy randomWait = WaitStrategies.randomWait(200L);
        Set<Long> times = new HashSet<>();
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        times.add(randomWait.computeSleepTime(failedAttempt(1, 6546L)));
        assertThat(times.size()).isGreaterThan(1); // if not, the random is not random
        for (long time : times) {
            assertThat(time).isGreaterThanOrEqualTo(0L);
            assertThat(time).isLessThanOrEqualTo(200L);
        }
    }

    @Test
    void testExponential() {
        WaitStrategy exponentialWait = WaitStrategies.exponentialWait();
        assertThat(exponentialWait.computeSleepTime(failedAttempt(1, 6546L))).isEqualTo(2);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(2, 0))).isEqualTo(4);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(3, 6546L))).isEqualTo(8);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(6, 0))).isEqualTo(64);
    }

    @Test
    void testExponentialWithMaximumWait() {
        WaitStrategy exponentialWait = WaitStrategies.exponentialWait(40);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(1, 6546L))).isEqualTo(2);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(2, 0))).isEqualTo(4);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(3, 6546L))).isEqualTo(8);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(6, 0))).isEqualTo(40);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(Integer.MAX_VALUE, 0))).isEqualTo(40);
    }

    @Test
    void testExponentialWithMultiplierAndMaximumWait() {
        WaitStrategy exponentialWait = WaitStrategies.exponentialWait(1000, 50000);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(1, 6546L))).isEqualTo(2000);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(2, 0))).isEqualTo(4000);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(3, 6546L))).isEqualTo(8000);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(6, 0))).isEqualTo(50000);
        assertThat(exponentialWait.computeSleepTime(failedAttempt(Integer.MAX_VALUE, 0))).isEqualTo(50000);
    }

    private FailedAttempt failedAttempt(long attemptNumber, long delaySinceFirstAttempt) {
        return new FailedAttempt(new RuntimeException(), attemptNumber, delaySinceFirstAttempt);
    }
}