package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.BiConsumerWithExceptions;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author Jorge Alonso
 */
public abstract class BiConsumerWithRetries<T, U, E extends Exception> extends DoerWithRetries implements BiConsumer<T, U> {
    @Override
    public final void accept(T t, U u) {
        try {
            getRetrier().accept((BiConsumerWithExceptions<T, U, E>) this::acceptWithRetries, t, u);
        } catch (ExecutionException e) {
            getRetryExceptionHandler().handleExecutionException(e);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
    }

    abstract void acceptWithRetries(T t, U u) throws E;
}
