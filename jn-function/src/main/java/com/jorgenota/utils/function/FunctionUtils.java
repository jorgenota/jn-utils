package com.jorgenota.utils.function;

public final class FunctionUtils {

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
