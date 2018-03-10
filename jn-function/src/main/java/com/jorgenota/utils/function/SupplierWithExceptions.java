package com.jorgenota.utils.function;

import org.springframework.lang.Nullable;

import java.util.function.Supplier;

import static com.jorgenota.utils.function.FunctionUtils.throwAsUnchecked;

/**
 * @author Jorge Alonso
 */
public interface SupplierWithExceptions<T, E extends Exception> extends Supplier<T> {

    @Nullable
    default T get() {
        try {
            return getWithExceptions();
        } catch (Exception e) {
            throwAsUnchecked(e);
        }
        return null;
    }

    @Nullable
    T getWithExceptions() throws E;
}
