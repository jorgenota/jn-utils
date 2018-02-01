package com.jorgenota.utils.retry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Jorge Alonso
 */
public class DoerWithRetriesTests {

    private static final RetryExceptionHandler rethrowingRetryExceptionHandler = new RetryExceptionHandler() {
        @Override
        public void handleRetryException(RetryException e) {
            throw new RuntimeException(e);
        }

        @Override
        public void handleExecutionException(ExecutionException e) {
            throw new RuntimeException(e);
        }
    };
    Retrier retrier = RetrierBuilder.newBuilder().failIfExceptionOfType(IOException.class).build();

    @Nested
    @DisplayName("Testing BiConsumerWithRetries")
    class testBiConsumerWithRetries {

        @Test
        public void testSucceedingBiConsumerWithRetries() {
            BiConsumerWithRetries biConsumerWithRetries = new BiConsumerWithRetries() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    // Do nothing
                }
            };
            biConsumerWithRetries.accept("2", "3");
        }

        @Test
        public void testFailingBiConsumerWithRetriesUsingDefaultExceptionHandler() {
            BiConsumerWithRetries biConsumerWithRetries = new BiConsumerWithRetries() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    throw new NullPointerException();
                }
            };
            biConsumerWithRetries.accept("2", "3");
        }

        @Test
        public void testFailingBiConsumerWithRetriesUsingRetryExceptionHandler() {
            BiConsumerWithRetries biConsumerWithRetries = new BiConsumerWithRetries() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                biConsumerWithRetries.accept("2", "3");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingBiConsumerWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            BiConsumerWithRetries biConsumerWithRetries = new BiConsumerWithRetries() {
                @Override
                void acceptWithRetries(Object o, Object o2) throws Exception {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                biConsumerWithRetries.accept("2", "3");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing BiFunctionWithRetries")
    class testBiFunctionWithRetries {

        @Test
        public void testSucceedingBiFunctionWithRetries() {
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
        public void testFailingBiFunctionWithRetriesUsingDefaultExceptionHandler() {
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
        public void testFailingBiFunctionWithRetriesUsingRetryExceptionHandler() {
            BiFunctionWithRetries<Integer, Integer, Integer, IOException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                biFunctionWithRetries.apply(2, 3);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingBiFunctionWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            BiFunctionWithRetries<Integer, Integer, Integer, IOException> biFunctionWithRetries = new BiFunctionWithRetries<Integer, Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o, Integer o2) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                biFunctionWithRetries.apply(2, 3);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing ConsumerWithRetries")
    class testConsumerWithRetries {

        @Test
        public void testSucceedingConsumerWithRetries() {
            ConsumerWithRetries consumerWithRetries = new ConsumerWithRetries() {
                @Override
                void acceptWithRetries(Object o) throws Exception {
                    // Do nothing
                }
            };
            consumerWithRetries.accept("2");
        }

        @Test
        public void testFailingConsumerWithRetriesUsingDefaultExceptionHandler() {
            ConsumerWithRetries consumerWithRetries = new ConsumerWithRetries<Object, NullPointerException>() {
                @Override
                void acceptWithRetries(Object o) throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            consumerWithRetries.accept("2");
        }

        @Test
        public void testFailingConsumerWithRetriesUsingRetryExceptionHandler() {
            ConsumerWithRetries consumerWithRetries = new ConsumerWithRetries<Object, IOException>() {
                @Override
                void acceptWithRetries(Object o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                consumerWithRetries.accept("2");
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingConsumerWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            ConsumerWithRetries consumerWithRetries = new ConsumerWithRetries<Object, IOException>() {
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
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing FunctionWithRetries")
    class testFunctionWithRetries {

        @Test
        public void testSucceedingFunctionWithRetries() {
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
        public void testFailingFunctionWithRetriesUsingDefaultExceptionHandler() {
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
        public void testFailingFunctionWithRetriesUsingRetryExceptionHandler() {
            FunctionWithRetries<Integer, Integer, IOException> functionWithRetries = new FunctionWithRetries<Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                functionWithRetries.apply(2);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingFunctionWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            FunctionWithRetries<Integer, Integer, IOException> functionWithRetries = new FunctionWithRetries<Integer, Integer, IOException>() {
                @Override
                Integer applyWithRetries(Integer o) throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                functionWithRetries.apply(2);
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing RunnableWithRetries")
    class testRunnableWithRetries {

        @Test
        public void testSucceedingRunnableWithRetries() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries() {
                @Override
                void runWithRetries() throws Exception {
                    // Do nothing
                }
            };
            runnableWithRetries.run();
        }

        @Test
        public void testFailingRunnableWithRetriesUsingDefaultExceptionHandler() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<NullPointerException>() {
                @Override
                void runWithRetries() throws NullPointerException {
                    throw new NullPointerException();
                }
            };
            runnableWithRetries.run();
        }

        @Test
        public void testFailingRunnableWithRetriesUsingRetryExceptionHandler() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<IOException>() {
                @Override
                void runWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                runnableWithRetries.run();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingRunnableWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            RunnableWithRetries runnableWithRetries = new RunnableWithRetries<IOException>() {
                @Override
                void runWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                runnableWithRetries.run();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing CallableWithRetries")
    class testCallableWithRetries {

        @Test
        public void testSucceedingCallableWithRetries() {
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
        public void testFailingCallableWithRetriesUsingDefaultExceptionHandler() {
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
        public void testFailingCallableWithRetriesUsingRetryExceptionHandler() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                callableWithRetries.call();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingCallableWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            CallableWithRetries<Integer> callableWithRetries = new CallableWithRetries<Integer>() {
                @Override
                Integer callWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                callableWithRetries.call();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

    @Nested
    @DisplayName("Testing SupplierWithRetries")
    class testSupplierWithRetries {

        @Test
        public void testSucceedingSupplierWithRetries() {
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
        public void testFailingSupplierWithRetriesUsingDefaultExceptionHandler() {
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
        public void testFailingSupplierWithRetriesUsingRetryExceptionHandler() {
            SupplierWithRetries<Integer, IOException> supplierWithRetries = new SupplierWithRetries<Integer, IOException>() {
                @Override
                Integer getWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;
            };

            try {
                supplierWithRetries.get();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(RetryException.class);
            }
        }

        @Test
        public void testFailingSupplierWithRetriesUsingRetryExceptionHandlerAndFailingRetrier() {
            SupplierWithRetries<Integer, IOException> supplierWithRetries = new SupplierWithRetries<Integer, IOException>() {
                @Override
                Integer getWithRetries() throws IOException {
                    throw new IOException();
                }

                @Override
                protected RetryExceptionHandler getRetryExceptionHandler() {
                    return rethrowingRetryExceptionHandler;
                }

                ;

                @Override
                protected Retrier getRetrier() {
                    return retrier;
                }
            };

            try {
                supplierWithRetries.get();
                failBecauseExceptionWasNotThrown(RuntimeException.class);
            } catch (RuntimeException e) {
                assertThat(e).hasCauseInstanceOf(ExecutionException.class);
            }
        }
    }

}
