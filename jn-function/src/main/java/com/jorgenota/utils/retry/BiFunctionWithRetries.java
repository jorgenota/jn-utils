package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.BiFunctionWithExceptions;

import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

/**
 * @author Jorge Alonso
 */
public abstract class BiFunctionWithRetries<T, U, R, E extends Exception> extends DoerWithRetries implements BiFunction<T, U, R> {
    @Override
    public final R apply(T t, U u) {
        try {
            return getRetrier().apply((BiFunctionWithExceptions<T, U, R, E>) this::applyWithRetries, t, u);
        } catch (ExecutionException e) {
            getRetryExceptionHandler().handleExecutionException(e);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    abstract R applyWithRetries(T t, U u) throws E;
}
