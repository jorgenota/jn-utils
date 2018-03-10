package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.SupplierWithExceptions;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * @author Jorge Alonso
 */
public abstract class SupplierWithRetries<T, E extends Exception> extends DoerWithRetries implements Supplier<T> {
    @Override
    @Nullable
    public final T get() {
        try {
            return getRetrier().get((SupplierWithExceptions<T, E>) this::getWithRetries);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
        return null;
    }

    @Nullable
    abstract T getWithRetries() throws E;
}
