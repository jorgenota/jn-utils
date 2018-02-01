package com.jorgenota.utils.function;

/**
 * @author Jorge Alonso
 */
public interface RunnableWithExceptions<E extends Exception> extends Runnable {

    default void run() {
        try {
            runWithExceptions();
        } catch (Exception e) {
            FunctionUtils.throwAsUnchecked(e);
        }
    }

    void runWithExceptions() throws E;
}
