package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.BiFunctionWithExceptions;
import org.springframework.lang.Nullable;

import java.util.function.BiFunction;

/**
 * @author Jorge Alonso
 */
public abstract class BiFunctionWithRetries<T, U, R, E extends Exception> extends DoerWithRetries implements BiFunction<T, U, R> {
    @Override
    @Nullable
    public final R apply(T t, U u) {
        try {
            return getRetrier().apply((BiFunctionWithExceptions<T, U, R, E>) this::applyWithRetries, t, u);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    @Nullable
    abstract R applyWithRetries(T t, U u) throws E;
}
