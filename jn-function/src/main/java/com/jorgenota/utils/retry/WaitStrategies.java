package com.jorgenota.utils.retry;

import com.jorgenota.utils.base.Preconditions;

import java.util.Random;

/**
 * Factory class for instances of {@link WaitStrategy}.
 *
 * @author JB
 */
public final class WaitStrategies {

    private static final WaitStrategy NO_WAIT_STRATEGY = new FixedWaitStrategy(0L);

    private WaitStrategies() {
    }

    /**
     * Returns a wait strategy that doesn't sleep at all before retrying. Use this at your own risk.
     *
     * @return a wait strategy that doesn't wait between retries
     */
    public static WaitStrategy noWait() {
        return NO_WAIT_STRATEGY;
    }

    /**
     * Returns a wait strategy that sleeps a fixed amount of time before retrying.
     *
     * @param sleepTimeMilis the time to sleep
     * @return a wait strategy that sleeps a fixed amount of time
     * @throws IllegalStateException if the sleep time is &lt; 0
     */
    public static WaitStrategy fixedWait(long sleepTimeMilis) throws IllegalStateException {
        return new FixedWaitStrategy(sleepTimeMilis);
    }

    /**
     * Returns a strategy that sleeps a random amount of time before retrying.
     *
     * @param maximumTimeMilis the maximum time to sleep
     * @return a wait strategy with a random wait time
     * @throws IllegalStateException if the maximum sleep time is &lt;= 0.
     */
    public static WaitStrategy randomWait(long maximumTimeMilis) {
        return new RandomWaitStrategy(0L, maximumTimeMilis);
    }

    /**
     * Returns a strategy that sleeps a random amount of time before retrying.
     *
     * @param minimumTime the minimum time to sleep
     * @param maximumTime the maximum time to sleep
     * @return a wait strategy with a random wait time
     * @throws IllegalStateException if the minimum sleep time is &lt; 0, or if the
     *                               maximum sleep time is less than (or equals to) the minimum.
     */
    public static WaitStrategy randomWait(long minimumTime,
                                          long maximumTime) {
        return new RandomWaitStrategy(minimumTime, maximumTime);
    }

    /**
     * Returns a strategy that sleeps a fixed amount of time after the first
     * failed attempt and in incrementing amounts of time after each additional
     * failed attempt.
     *
     * @param initialSleepTime the time to sleep before retrying the first time
     * @param increment        the increment added to the previous sleep time after each failed attempt
     * @return a wait strategy that incrementally sleeps an additional fixed time after each failed attempt
     */
    public static WaitStrategy incrementingWait(long initialSleepTime,
                                                long increment) {
        return new IncrementingWaitStrategy(initialSleepTime, increment);
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to Long.MAX_VALUE.
     *
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait() {
        return new ExponentialWaitStrategy(1, Long.MAX_VALUE);
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to the maximumTime.
     *
     * @param maximumTime the maximum time to sleep
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait(long maximumTime) {
        return new ExponentialWaitStrategy(1, maximumTime);
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to the maximumTime.
     * The wait time between the retries can be controlled by the multiplier.
     * nextWaitTime = exponentialIncrement * {@code multiplier}.
     *
     * @param multiplier  multiply the wait time calculated by this
     * @param maximumTime the maximum time to sleep
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait(long multiplier,
                                               long maximumTime) {
        return new ExponentialWaitStrategy(multiplier, maximumTime);
    }

    private static final class FixedWaitStrategy implements WaitStrategy {
        private final long sleepTime;

        private FixedWaitStrategy(long sleepTime) {
            Preconditions.isTrue(sleepTime >= 0L,
                "sleepTime must be >= 0 but is %d", sleepTime);
            this.sleepTime = sleepTime;
        }

        @Override
        public long computeSleepTime(FailedAttempt failedAttempt) {
            return sleepTime;
        }
    }

    private static final class RandomWaitStrategy implements WaitStrategy {
        private static final Random RANDOM = new Random();
        private final long minimum;
        private final long maximum;

        private RandomWaitStrategy(long minimum, long maximum) {
            Preconditions.isTrue(minimum >= 0,
                "minimum must be >= 0 but is %d", minimum);
            Preconditions.isTrue(maximum > minimum,
                "maximum must be > minimum but maximum is %d and minimum is", maximum, minimum);

            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        public long computeSleepTime(FailedAttempt failedAttempt) {
            long t = Math.abs(RANDOM.nextLong()) % (maximum - minimum);
            return t + minimum;
        }
    }

    private static final class IncrementingWaitStrategy implements WaitStrategy {
        private final long initialSleepTime;
        private final long increment;

        private IncrementingWaitStrategy(long initialSleepTime,
                                         long increment) {
            Preconditions.isTrue(initialSleepTime >= 0L,
                "initialSleepTime must be >= 0 but is %d", initialSleepTime);
            this.initialSleepTime = initialSleepTime;
            this.increment = increment;
        }

        @Override
        public long computeSleepTime(FailedAttempt failedAttempt) {
            long result = initialSleepTime + (increment * (failedAttempt.getAttemptNumber() - 1));
            return result >= 0L ? result : 0L;
        }
    }

    private static final class ExponentialWaitStrategy implements WaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        private ExponentialWaitStrategy(long multiplier,
                                        long maximumWait) {
            Preconditions.isTrue(multiplier > 0L,
                "multiplier must be > 0 but is %d", multiplier);
            Preconditions.isTrue(maximumWait >= 0L,
                "maximumWait must be >= 0 but is %d", maximumWait);
            Preconditions.isTrue(multiplier < maximumWait,
                "multiplier must be < maximumWait but is %d", multiplier);
            this.multiplier = multiplier;
            this.maximumWait = maximumWait;
        }

        @Override
        public long computeSleepTime(FailedAttempt failedAttempt) {
            double exp = Math.pow(2, failedAttempt.getAttemptNumber());
            long result = Math.round(multiplier * exp);
            if (result > maximumWait) {
                result = maximumWait;
            }
            return result >= 0L ? result : 0L;
        }
    }
}