package com.jorgenota.utils.function;

import java.util.function.BiFunction;

import static com.jorgenota.utils.function.FunctionUtils.throwAsUnchecked;

/**
 * @author Jorge Alonso
 */
@FunctionalInterface
public interface BiFunctionWithExceptions<T, U, R, E extends Exception> extends BiFunction<T, U, R> {
    default R apply(T t, U u) {
        try {
            return applyWithExceptions(t, u);
        } catch (Exception e) {
            throwAsUnchecked(e);
        }
        return null;
    }

    R applyWithExceptions(T t, U u) throws E;
}
