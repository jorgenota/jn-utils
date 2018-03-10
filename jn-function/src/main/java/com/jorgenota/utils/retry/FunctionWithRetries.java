package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.FunctionWithExceptions;
import org.springframework.lang.Nullable;

import java.util.function.Function;

/**
 * @author Jorge Alonso
 */
public abstract class FunctionWithRetries<T, R, E extends Exception> extends DoerWithRetries implements Function<T, R> {
    @Override
    @Nullable
    public final R apply(T t) {
        try {
            return getRetrier().apply((FunctionWithExceptions<T, R, E>) this::applyWithRetries, t);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    @Nullable
    abstract R applyWithRetries(T t) throws E;
}
