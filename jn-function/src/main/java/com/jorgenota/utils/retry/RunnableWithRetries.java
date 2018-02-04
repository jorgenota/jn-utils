package com.jorgenota.utils.retry;

import com.jorgenota.utils.function.RunnableWithExceptions;

/**
 * @author Jorge Alonso
 */
public abstract class RunnableWithRetries<E extends Exception> extends DoerWithRetries implements Runnable {
    @Override
    public final void run() {
        try {
            getRetrier().run((RunnableWithExceptions) this::runWithRetries);
        } catch (RetryException e) {
            getRetryExceptionHandler().handleRetryException(e);
        }
    }

    abstract void runWithRetries() throws E;
}
