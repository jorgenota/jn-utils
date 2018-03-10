package com.jorgenota.utils.function;

import org.springframework.lang.Nullable;

import java.util.function.Function;

import static com.jorgenota.utils.function.FunctionUtils.throwAsUnchecked;

/**
 * @author Jorge Alonso
 */
@FunctionalInterface
public interface FunctionWithExceptions<T, R, E extends Exception> extends Function<T, R> {
    @Nullable
    default R apply(T t) {
        try {
            return applyWithExceptions(t);
        } catch (Exception e) {
            throwAsUnchecked(e);
        }
        return null;
    }

    @Nullable
    R applyWithExceptions(T t) throws E;
}
