package com.jorgenota.utils.function;

import java.util.function.Supplier;

import static com.jorgenota.utils.function.FunctionUtils.throwAsUnchecked;

/**
 * @author Jorge Alonso
 */
public interface SupplierWithExceptions<T, E extends Exception> extends Supplier<T> {

    default T get() {
        try {
            return getWithExceptions();
        } catch (Exception e) {
            throwAsUnchecked(e);
        }
        return null;
    }

    T getWithExceptions() throws E;
}
