package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.FunctionWithExceptions;

import java.util.function.Function;

/**
 * @author Jorge Alonso
 */
public abstract class FunctionWithRetries<T, R, E extends Exception> extends DoerWithRetries implements Function<T, R> {
    @Override
    public final R apply(T t) {
        try {
            return getRetrier().apply((FunctionWithExceptions<T, R, E>) this::applyWithRetries, t);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    abstract R applyWithRetries(T t) throws E;
}
