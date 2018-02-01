package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Jorge Alonso
 */
@DisplayName("Testing RetrierBuilder and Retrier")
public class RetrierBuilderTests {

    // Operations that always fail
    Callable<Integer> callableAlwaysFail = () -> {
        throw new RuntimeException();
    };
    // Operations that work after N attempts
    Callable<Integer> callableWorksAfter5Attempts = worksAfterNFailedAttempts(5);
    Callable<Integer> callableWorksAfter11Attempts = worksAfterNFailedAttempts(11);
    // Operations that always succeed
    private Callable<Integer> callableReturns5 = () -> 5;
    private Function<Integer, String> integerToString = x -> String.valueOf(x);
    private BiFunction<Integer, Integer, String> sumToString = (x, y) -> String.valueOf(x + y);
    private Consumer<Integer> printInteger = (x) -> System.out.println(x);
    private BiConsumer<Integer, Integer> printSum = (x, y) -> System.out.println(x + y);
    private Runnable runnable = () -> System.out.println("I'm a runnable");
    private Supplier<Integer> supplierOf5 = () -> 5;
    private Function<Integer, String> functionAlwaysFail = x -> {
        throw new RuntimeException();
    };
    private BiFunction<Integer, Integer, String> biFunctionAlwaysFail = (x, y) -> {
        throw new RuntimeException();
    };
    private Consumer<Integer> consumerAlwaysFail = (x) -> {
        throw new RuntimeException();
    };
    private BiConsumer<Integer, Integer> biConsumerAlwaysFail = (x, y) -> {
        throw new RuntimeException();
    };
    private Runnable runnableAlwaysFail = () -> {
        throw new RuntimeException();
    };
    private Supplier<Integer> supplierAlwaysFail = () -> {
        throw new RuntimeException();
    };
    private FunctionWithExceptions<Integer, String, IOException> functionWithExceptionsAlwaysFail = x -> {
        throw new IOException();
    };
    private BiFunctionWithExceptions<Integer, Integer, String, IOException> biFunctionWithExceptionsAlwaysFail = (x, y) -> {
        throw new IOException();
    };
    private ConsumerWithExceptions<Integer, IOException> consumerWithExceptionsAlwaysFail = (x) -> {
        throw new IOException();
    };
    private BiConsumerWithExceptions<Integer, Integer, IOException> biConsumerWithExceptionsAlwaysFail = (x, y) -> {
        throw new IOException();
    };
    private RunnableWithExceptions<IOException> runnableWithExceptionsAlwaysFail = () -> {
        throw new IOException();
    };
    private SupplierWithExceptions<Integer, IOException> supplierWithExceptionsAlwaysFail = () -> {
        throw new IOException();
    };

    private Callable<Integer> worksAfterNFailedAttempts(int attemptsToFail) {
        return new Callable<Integer>() {
            int counter = 0;

            @Override
            public Integer call() throws Exception {
                if (counter < attemptsToFail) {
                    counter++;
                    throw new IOException();
                }
                return counter;
            }
        };
    }

    @Test
    public void testInterruption() throws InterruptedException, ExecutionException {
        final AtomicBoolean result = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Retrier retrier = RetrierBuilder.newBuilder()
                    .withWaitStrategy(WaitStrategies.fixedWait(1000L))
                    .build();
                try {
                    retrier.call(alwaysNull(latch));
                    failBecauseExceptionWasNotThrown(RetryException.class);
                } catch (RetryException e) {
                    assertThat(e).hasCauseInstanceOf(RuntimeException.class);
                    assertThat(Thread.currentThread().isInterrupted()).isTrue();
                    result.set(true);
                } catch (ExecutionException e) {
                    failBecauseExceptionWasNotThrown(RetryException.class);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        latch.countDown();
        t.interrupt();
        t.join();
        assertThat(result.get()).isTrue();
    }

    private Callable<Boolean> alwaysNull(final CountDownLatch latch) {
        return () -> {
            latch.countDown();
            throw new RuntimeException();
        };
    }

    @Test
    public void testWhetherBuilderFailsForNullStopStrategy() {
        try {
            RetrierBuilder.<Void>newBuilder()
                .withStopStrategy(null)
                .build();
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException exception) {
            assertThat(exception.getMessage()).contains("stopStrategy may not be null");
        }
    }

    @Test
    public void testWhetherBuilderFailsForNullWaitStrategy() {
        try {
            RetrierBuilder.<Void>newBuilder()
                .withWaitStrategy(null)
                .build();
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException exception) {
            assertThat(exception.getMessage()).contains("waitStrategy may not be null");
        }
    }

    @DisplayName("Testing Retrier built with default strategies")
    @Nested
    class testRetrierWithDefaultStrategies {

        Retrier defaultRetrier = RetrierBuilder.newBuilder().build();

        @Test
        public void retrierReturnsResultWhenOperationsSucceed() throws ExecutionException, RetryException {
            assertThat(defaultRetrier.call(callableReturns5)).isEqualTo(5);
            assertThat(defaultRetrier.apply(integerToString, 4)).isEqualTo("4");
            assertThat(defaultRetrier.apply(sumToString, 4, 8)).isEqualTo("12");
            defaultRetrier.accept(printInteger, 6);
            defaultRetrier.accept(printSum, 6, 8);
            defaultRetrier.run(runnable);
            assertThat(defaultRetrier.get(supplierOf5)).isEqualTo(5);
        }

        @Test
        public void retrierThrowsRetryExceptionWhenOperationsAlwaysFail() throws ExecutionException {
            try {
                defaultRetrier.call(callableAlwaysFail);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.apply(functionAlwaysFail, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.apply(biFunctionAlwaysFail, 8, 6);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.accept(consumerAlwaysFail, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.accept(biConsumerAlwaysFail, 8, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.run(runnableAlwaysFail);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.get(supplierAlwaysFail);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.apply(functionWithExceptionsAlwaysFail, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.apply(biFunctionWithExceptionsAlwaysFail, 8, 6);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.accept(consumerWithExceptionsAlwaysFail, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.accept(biConsumerWithExceptionsAlwaysFail, 8, 8);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.run(runnableWithExceptionsAlwaysFail);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }

            try {
                defaultRetrier.get(supplierWithExceptionsAlwaysFail);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
            }
        }


        @Test
        public void retrierWithDefaultWaitStrategyDoesNotWaitBetweenAttempts() throws ExecutionException, RetryException {
            long start = System.currentTimeMillis();
            int result = defaultRetrier.call(callableWorksAfter5Attempts);
            // The default wait strategy is noWait
            assertThat(System.currentTimeMillis() - start).isLessThan(10L);
            assertThat(result).isEqualTo(5);
        }

        @Test
        public void retrierWithDefaultStopStrategyFailsAfter10Attempts() throws ExecutionException, RetryException {
            long start = System.currentTimeMillis();
            try {
                defaultRetrier.call(callableWorksAfter11Attempts);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
                // The default stop strategy is stop after 10 attempts
                assertThat(e.getNumberOfFailedAttempts()).isEqualTo(10);
                assertThat(e).hasCauseInstanceOf(IOException.class);
            }
        }
    }

    @DisplayName("Testing Retriers built with different strategies")
    @Nested
    class testRetrierWithDifferentStrategies {

        @Test
        public void testWithFixedWaitStrategy() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.fixedWait(50L))
                .build();
            long start = System.currentTimeMillis();
            int result = retrier.call(callableWorksAfter5Attempts);
            // Waits 50 * 5 millis
            assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(250L).isLessThan(260L);
            assertThat(result).isEqualTo(5);
        }

        @Test
        public void testWithIncrementingWaitStrategy() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.incrementingWait(10L, 10L))
                .build();
            long start = System.currentTimeMillis();
            int result = retrier.call(callableWorksAfter5Attempts);
            // Waits 10 + 20 + 30 + 40 + 50
            assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(150L).isLessThan(160L);
            assertThat(result).isEqualTo(5);
        }

        @Test
        public void testWithStopAfterAttemptStrategy() throws ExecutionException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
            try {
                retrier.call(callableWorksAfter5Attempts);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
                // Stops after 3 attempts
                assertThat(e.getNumberOfFailedAttempts()).isEqualTo(3);
            }
        }

        @Test
        public void testWithStopAfterDelayStrategy() throws ExecutionException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.fixedWait(50L))
                .withStopStrategy(StopStrategies.stopAfterDelay(300L))
                .build();
            long start = System.currentTimeMillis();
            try {
                retrier.call(callableWorksAfter11Attempts);
                failBecauseExceptionWasNotThrown(RetryException.class);
            } catch (RetryException e) {
                assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(300L).isLessThan(360L);
            }
        }

        @Test
        public void testWithFailIfRuntimeException() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfRuntimeException()
                .build();
            // callableWorksAfter5Attempts fails with IOException, so the retrier will keep retrying
            int result = retrier.call(callableWorksAfter5Attempts);
            assertThat(result).isEqualTo(5);

        }

        @Test
        public void testWithFailIfNullPointerException() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfExceptionOfType(NullPointerException.class)
                .build();
            // callableWorksAfter5Attempts fails with IOException, so the retrier will keep retrying
            int result = retrier.call(callableWorksAfter5Attempts);
            assertThat(result).isEqualTo(5);

        }

        @Test
        public void testWithFailIfFalseExceptionPredicate() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfException(x -> false)
                .build();
            // exception predicate is false, so the retrier will keep retrying
            int result = retrier.call(callableWorksAfter5Attempts);
            assertThat(result).isEqualTo(5);

        }

        @Test
        public void testWithFailIfTrueExceptionPredicate() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfException(x -> x instanceof IOException)
                .build();
            try {
                // exception predicate is false, so the retrier will fail
                int result = retrier.call(callableWorksAfter5Attempts);
                failBecauseExceptionWasNotThrown(ExecutionException.class);
            } catch (ExecutionException e) {
                assertThat(e).hasCauseInstanceOf(IOException.class);
            }
        }

        @Test
        public void testWithFailIfException() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfException()
                .build();
            try {
                // callableWorksAfter5Attempts fails with IOException, so the retrier will fail
                int result = retrier.call(callableWorksAfter5Attempts);
                failBecauseExceptionWasNotThrown(ExecutionException.class);
            } catch (ExecutionException e) {
                assertThat(e).hasCauseInstanceOf(IOException.class);
            }
        }

        @Test
        public void testWithFailIfIOException() throws ExecutionException, RetryException {
            Retrier retrier = RetrierBuilder.newBuilder()
                .withWaitStrategy(WaitStrategies.noWait())
                .withStopStrategy(StopStrategies.neverStop())
                .failIfExceptionOfType(IOException.class)
                .build();
            try {
                // callableWorksAfter5Attempts fails with IOException, so the retrier will fail
                int result = retrier.call(callableWorksAfter5Attempts);
                failBecauseExceptionWasNotThrown(ExecutionException.class);
            } catch (ExecutionException e) {
                assertThat(e).hasCauseInstanceOf(IOException.class);
            }
        }
    }
}
