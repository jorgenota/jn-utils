package com.jorgenota.utils.retry;

import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * @author Jorge Alonso
 */
public abstract class CallableWithRetries<T> extends DoerWithRetries implements Callable<T> {
    @Override
    @Nullable
    public final T call() {
        try {
            return getRetrier().call((Callable<T>) this::callWithRetries);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    @Nullable
    abstract T callWithRetries() throws Exception;
}
