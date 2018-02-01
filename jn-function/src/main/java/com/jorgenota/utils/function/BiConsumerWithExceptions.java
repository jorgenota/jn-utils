package com.jorgenota.utils.function;

import java.util.function.BiConsumer;

/**
 * @author Jorge Alonso
 */
@FunctionalInterface
public interface BiConsumerWithExceptions<T, U, E extends Exception> extends BiConsumer<T, U> {
    default void accept(T t, U u) {
        try {
            acceptWithExceptions(t, u);
        } catch (Exception e) {
            FunctionUtils.throwAsUnchecked(e);
        }
    }

    void acceptWithExceptions(T t, U u) throws E;
}
