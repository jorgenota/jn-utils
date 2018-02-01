package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.SupplierWithExceptions;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * @author Jorge Alonso
 */
public abstract class SupplierWithRetries<T, E extends Exception> extends DoerWithRetries implements Supplier<T> {
    @Override
    public final T get() {
        try {
            return getRetrier().get((SupplierWithExceptions<T,E>) this::getWithRetries);
        } catch (ExecutionException e) {
            getRetryExceptionHandler().handleExecutionException(e);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    abstract T getWithRetries() throws E;
}
