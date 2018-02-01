package com.jorgenota.utils.retry;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * @author Jorge Alonso
 */
public final class Retrier {
    private final StopStrategy stopStrategy;
    private final WaitStrategy waitStrategy;
    private final Predicate<FailedAttempt> failPredicate;

    public Retrier(StopStrategy stopStrategy,
                   WaitStrategy waitStrategy,
                   Predicate<FailedAttempt> failPredicate
    ) {

        this.stopStrategy = notNull(stopStrategy, "stopStrategy may not be null");
        this.waitStrategy = notNull(waitStrategy, "waitStrategy may not be null");
        this.failPredicate = notNull(failPredicate, "failPredicate may not be null");
    }

    public <T> T call(Callable<T> callable) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                return callable.call();
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public <T, R> R apply(Function<T, R> function, T t) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                return function.apply(t);
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public <T, U, R> R apply(BiFunction<T, U, R> function, T t, U u) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                return function.apply(t, u);
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public <T> void accept(Consumer<T> consumer, T t) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                consumer.accept(t);
                return;
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public <T, U> void accept(BiConsumer<T, U> consumer, T t, U u) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                consumer.accept(t, u);
                return;
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public void run(Runnable runnable) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                runnable.run();
                return;
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    public <T> T get(Supplier<T> supplier) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        for (int attemptNumber = 1; ; attemptNumber++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                handleFailedAttempt(e, attemptNumber, startTime);
            }
        }
    }

    private void handleFailedAttempt(Exception e, int attemptNumber, long startTime) throws ExecutionException, RetryException {
        long delaySinceFirstAttempt = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        FailedAttempt failedAttempt = new FailedAttempt(e, attemptNumber, delaySinceFirstAttempt);

        if (failPredicate.test(failedAttempt)) {
            failedAttempt.throwExecutionException();
        }
        if (stopStrategy.shouldStop(failedAttempt)) {
            throw new RetryException(failedAttempt);
        } else {
            long sleepTime = waitStrategy.computeSleepTime(failedAttempt);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RetryException(failedAttempt);
                }
            }
        }
    }
}
