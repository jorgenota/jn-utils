package com.jorgenota.utils.retry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Jorge Alonso
 */
class DoerWithRetriesTests {

    private static final RetryExceptionHandler rethrowingRetryExceptionHandler = e -> {
        throw new RuntimeException(e);
    };

    private Retrier retrier = RetrierBuilder.newBuilder().failIfExceptionOfType(IOException.class).build();

    @Nested
    @DisplayName("Testing BiConsumerWithRetries")
    class testBiConsumerWithRetries {

        @Test
        void testSucceedingBiConsumerWithRetries() {
            BiConsumerWithRetries<Object, Object, Exception> biConsumerWithRetries = new BiConsumerWithRetries<Object, Object, Exception>() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    // Do nothing
                }
            };
            biConsumerWithRetries.accept("2", "3");
        }

        @Test
        void testFailingBiConsumerWithRetriesUsingDefaultExceptionHandler() {
            BiConsumerWithRetries<Object, Object, Exception> biConsumerWithRetries = new BiConsumerWithRetries<Object, Object, Exception>() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    throw new NullPointerException();
                }
            };
            biConsumerWithRetries.accept("2", "3");
        }

        @Test
        void testFailingBiConsumerWithRetriesUsingRetryExceptionHandler() {
            BiConsumerWithRetries<Object, Object, Exception> biConsumerWithRetries = new BiConsumerWithRetries<Object, Object, Exception>() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                biConsumerWithRetries.accept("2", "3");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingBiConsumerWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            BiConsumerWithRetries<Object, Object, IOException> biConsumerWithRetries = new BiConsumerWithRetries<Object, Object, IOException>() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                biConsumerWithRetries.accept("2", "3");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing BiFunctionWithRetries")
    class testBiFunctionWithRetries {

        @Test
        void testSucceedingBiFunctionWithRetries() {
            BiFunctionWithRetries<Integer, Integer, Integer, NullPointerException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, NullPointerException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws NullPointerException {
                    return o + o2;
                }
            };
            Integer result = biFunctionWithRetries.apply(2, 3);
            assertThat(result).isEqualTo(5);
        }

        @Test
        void testFailingBiFunctionWithRetriesUsingDefaultExceptionHandler() {
            BiFunctionWithRetries<Integer, Integer, Integer, NullPointerException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, NullPointerException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            Integer result = biFunctionWithRetries.apply(2, 3);
            assertThat(result).isNull();
        }

        @Test
        void testFailingBiFunctionWithRetriesUsingRetryExceptionHandler() {
            BiFunctionWithRetries<Integer, Integer, Integer, IOException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                biFunctionWithRetries.apply(2, 3);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingBiFunctionWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            BiFunctionWithRetries<Integer, Integer, Integer, IOException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                biFunctionWithRetries.apply(2, 3);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing ConsumerWithRetries")
    class testConsumerWithRetries {

        @Test
        void testSucceedingConsumerWithRetries() {
            ConsumerWithRetries<Object, Exception> consumerWithRetries = new ConsumerWithRetries<Object, Exception>() {
                @Override
                void acceptWithRetries(Object o) throws Exception {
                    // Do nothing
                }
            };
            consumerWithRetries.accept("2");
        }

        @Test
        void testFailingConsumerWithRetriesUsingDefaultExceptionHandler() {
            ConsumerWithRetries<Object, NullPointerException> consumerWithRetries = new ConsumerWithRetries<Object, NullPointerException>() {
                @Override
                void acceptWithRetries(Object o) throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            consumerWithRetries.accept("2");
        }

        @Test
        void testFailingConsumerWithRetriesUsingRetryExceptionHandler() {
            ConsumerWithRetries<Object, IOException> consumerWithRetries = new ConsumerWithRetries<Object, IOException>() {
                @Override
                void acceptWithRetries(Object o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                consumerWithRetries.accept("2");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingConsumerWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            ConsumerWithRetries<Object, IOException> consumerWithRetries = new ConsumerWithRetries<Object, IOException>() {
                @Override
                void acceptWithRetries(Object o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                consumerWithRetries.accept("2");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing FunctionWithRetries")
    class testFunctionWithRetries {

        @Test
        void testSucceedingFunctionWithRetries() {
            FunctionWithRetries<Integer, Integer, NullPointerException> functionWithRetries = new FunctionWithRetries<Integer, Integer, NullPointerException>() {
                @Override
                Integer applyWithRetries(Integer o) throws NullPointerException {
                    return o + 3;
                }
            };
            Integer result = functionWithRetries.apply(2);
            assertThat(result).isEqualTo(5);
        }

        @Test
        void testFailingFunctionWithRetriesUsingDefaultExceptionHandler() {
            FunctionWithRetries<Integer, Integer, NullPointerException> functionWithRetries = new FunctionWithRetries<Integer, Integer, NullPointerException>() {
                @Override
                Integer applyWithRetries(Integer o) throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            Integer result = functionWithRetries.apply(2);
            assertThat(result).isNull();
        }

        @Test
        void testFailingFunctionWithRetriesUsingRetryExceptionHandler() {
            FunctionWithRetries<Integer, Integer, IOException> functionWithRetries = new FunctionWithRetries<Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                functionWithRetries.apply(2);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingFunctionWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            FunctionWithRetries<Integer, Integer, IOException> functionWithRetries = new FunctionWithRetries<Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                functionWithRetries.apply(2);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing RunnableWithRetries")
    class testRunnableWithRetries {

        @Test
        void testSucceedingRunnableWithRetries() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries() {
                @Override
                void runWithRetries() throws Exception {
                    // Do nothing
                }
            };
            runnableWithRetries.run();
        }

        @Test
        void testFailingRunnableWithRetriesUsingDefaultExceptionHandler() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<NullPointerException>() {
                @Override
                void runWithRetries() throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            runnableWithRetries.run();
        }

        @Test
        void testFailingRunnableWithRetriesUsingRetryExceptionHandler() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<IOException>() {
                @Override
                void runWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                runnableWithRetries.run();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingRunnableWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<IOException>() {
                @Override
                void runWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                runnableWithRetries.run();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing CallableWithRetries")
    class testCallableWithRetries {

        @Test
        void testSucceedingCallableWithRetries() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws NullPointerException {
                    return 5;
                }
            };
            Integer result = callableWithRetries.call();
            assertThat(result).isEqualTo(5);
        }

        @Test
        void testFailingCallableWithRetriesUsingDefaultExceptionHandler() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            Integer result = callableWithRetries.call();
            assertThat(result).isNull();
        }

        @Test
        void testFailingCallableWithRetriesUsingRetryExceptionHandler() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                callableWithRetries.call();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingCallableWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                callableWithRetries.call();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing SupplierWithRetries")
    class testSupplierWithRetries {

        @Test
        void testSucceedingSupplierWithRetries() {
            SupplierWithRetries<Integer, NullPointerException> supplierWithRetries = new SupplierWithRetries<Integer, NullPointerException>() {
                @Override
                Integer getWithRetries() throws NullPointerException {
                    return 5;
                }
            };
            Integer result = supplierWithRetries.get();
            assertThat(result).isEqualTo(5);
        }

        @Test
        void testFailingSupplierWithRetriesUsingDefaultExceptionHandler() {
            SupplierWithRetries<Integer, NullPointerException> supplierWithRetries = new SupplierWithRetries<Integer, NullPointerException>() {
                @Override
                Integer getWithRetries() throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            Integer result = supplierWithRetries.get();
            assertThat(result).isNull();
        }

        @Test
        void testFailingSupplierWithRetriesUsingRetryExceptionHandler() {
            SupplierWithRetries<Integer, IOException> supplierWithRetries = new SupplierWithRetries<Integer, IOException>() {
                @Override
                Integer getWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }
            };

            try {
                supplierWithRetries.get();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExhaustedRetryException.class);
            }
        }

        @Test
        void testFailingSupplierWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            SupplierWithRetries<Integer, IOException> supplierWithRetries = new SupplierWithRetries<Integer, IOException>() {
                @Override
                Integer getWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                supplierWithRetries.get();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(FailException.class);
            }
        }
    }

}
