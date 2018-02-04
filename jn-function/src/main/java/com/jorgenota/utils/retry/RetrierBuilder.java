package com.jorgenota.utils.retry;

import java.util.function.Predicate;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * @author Jorge Alonso
 */
public class RetrierBuilder {
    private static final int DEFAULT_ATTEMPT_NUMBER = 10;

    private StopStrategy stopStrategy;
    private WaitStrategy waitStrategy;
    private Predicate<FailedAttempt> failPredicate = (x -> false);

    private static final Retrier DEFAULT_RETRIER = newBuilder().build();

    private RetrierBuilder() {
    }

    /**
     * Constructs a new builder
     *
     * @return the new builder
     */
    public static RetrierBuilder newBuilder() {
        return new RetrierBuilder();
    }

    /**
     * Constructs a new builder
     *
     * @return the new builder
     */
    public static Retrier getDefaultRetrier() {
        return DEFAULT_RETRIER;
    }

    /**
     * Sets the wait strategy used to decide how long to sleep between failed attempts.
     * The default strategy is to retry immediately after a failed attempt.
     *
     * @param waitStrategy the strategy used to sleep between failed attempts
     * @return <code>this</code>
     * @throws IllegalStateException if a wait strategy has already been set.
     */
    public RetrierBuilder withWaitStrategy(WaitStrategy waitStrategy) throws IllegalStateException {
        this.waitStrategy = notNull(waitStrategy, "waitStrategy may not be null");
        return this;
    }

    /**
     * Sets the stop strategy used to decide when to stop retrying. The default strategy is to stop after
     * {@value #DEFAULT_ATTEMPT_NUMBER} attempts.
     *
     * @param stopStrategy the strategy used to decide when to stop retrying
     * @return <code>this</code>
     * @throws IllegalStateException if a stop strategy has already been set.
     */
    public RetrierBuilder withStopStrategy(StopStrategy stopStrategy) throws IllegalStateException {
        this.stopStrategy = notNull(stopStrategy, "stopStrategy may not be null");
        return this;
    }

    /**
     * Configures the retrier to fail if an exception (i.e. any <code>Exception</code> or subclass
     * of <code>Exception</code>) is thrown by the call.
     *
     * @return <code>this</code>
     */
    public RetrierBuilder failIfException() {
        failPredicate = failPredicate.or(new ExceptionClassPredicate(Exception.class));
        return this;
    }

    /**
     * Configures the retrier to fail if a runtime exception (i.e. any <code>RuntimeException</code> or subclass
     * of <code>RuntimeException</code>) is thrown by the call.
     *
     * @return <code>this</code>
     */
    public RetrierBuilder failIfRuntimeException() {
        failPredicate = failPredicate.or(new ExceptionClassPredicate(RuntimeException.class));
        return this;
    }

    /**
     * Configures the retrier to fail if an exception of the given class (or subclass of the given class) is
     * thrown by the call.
     *
     * @param exceptionClass the type of the exception which should cause the retrier to retry
     * @return <code>this</code>
     */
    public RetrierBuilder failIfExceptionOfType(Class<? extends Throwable> exceptionClass) {
        notNull(exceptionClass, "exceptionClass may not be null");
        failPredicate = failPredicate.or(new ExceptionClassPredicate(exceptionClass));
        return this;
    }

    /**
     * Configures the retrier to fail if an exception satisfying the given predicate is
     * thrown by the call.
     *
     * @param exceptionPredicate the predicate which causes a retry if satisfied
     * @return <code>this</code>
     */
    public RetrierBuilder failIfException(Predicate<Throwable> exceptionPredicate) {
        notNull(exceptionPredicate, "exceptionPredicate may not be null");
        failPredicate = failPredicate.or(new ExceptionPredicate(exceptionPredicate));
        return this;
    }

    /**
     * Builds the retrier.
     *
     * @return the built retrier.
     */
    public Retrier build() {
        StopStrategy theStopStrategy = stopStrategy == null ? StopStrategies.stopAfterAttempt(DEFAULT_ATTEMPT_NUMBER) : stopStrategy;
        WaitStrategy theWaitStrategy = waitStrategy == null ? WaitStrategies.noWait() : waitStrategy;

        return new Retrier(theStopStrategy, theWaitStrategy, failPredicate);
    }

    private static final class ExceptionClassPredicate implements Predicate<FailedAttempt> {

        private Class<? extends Throwable> exceptionClass;

        ExceptionClassPredicate(Class<? extends Throwable> exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        @Override
        public boolean test(FailedAttempt attempt) {
            return exceptionClass.isAssignableFrom(attempt.getExceptionCause().getClass());
        }
    }

    private static final class ExceptionPredicate implements Predicate<FailedAttempt> {

        private Predicate<Throwable> delegate;

        ExceptionPredicate(Predicate<Throwable> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean test(FailedAttempt attempt) {
            return delegate.test(attempt.getExceptionCause());
        }
    }
}
