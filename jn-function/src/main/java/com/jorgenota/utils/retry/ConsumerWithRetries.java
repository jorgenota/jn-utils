package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.ConsumerWithExceptions;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author Jorge Alonso
 */
public abstract class ConsumerWithRetries<T, E extends Exception> extends DoerWithRetries implements Consumer<T> {
    @Override
    public final void accept(T t) {
        try {
            getRetrier().accept((ConsumerWithExceptions<T,E>) this::acceptWithRetries, t);
        } catch (ExecutionException e) {
            getRetryExceptionHandler().handleExecutionException(e);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
    }

    abstract void acceptWithRetries(T t) throws E;
}
