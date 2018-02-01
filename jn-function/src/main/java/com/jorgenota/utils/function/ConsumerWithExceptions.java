package com.jorgenota.utils.function;

import java.util.function.Consumer;

/**
 * @author Jorge Alonso
 */
@FunctionalInterface
public interface ConsumerWithExceptions<T, E extends Exception> extends Consumer<T> {
    default void accept(T t) {
        try {
            acceptWithExceptions(t);
        } catch (Exception e) {
            FunctionUtils.throwAsUnchecked(e);
        }
    }

    void acceptWithExceptions(T t) throws E;
}
