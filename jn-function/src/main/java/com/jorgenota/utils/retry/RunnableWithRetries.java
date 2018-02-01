package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.RunnableWithExceptions;

import java.util.concurrent.ExecutionException;

/**
 * @author Jorge Alonso
 */
public abstract class RunnableWithRetries<E extends Exception> extends DoerWithRetries implements Runnable {
    @Override
    public final void run() {
        try {
            getRetrier().run((RunnableWithExceptions) this::runWithRetries);
        } catch (ExecutionException e) {
            getRetryExceptionHandler().handleExecutionException(e);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
    }

    abstract void runWithRetries() throws E;
}
