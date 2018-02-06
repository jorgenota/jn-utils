package com.jorgenota.utils.base;

/**
 * Assertion utility class that assists in validating arguments.
 * <p>
 * <p>Useful for identifying programmer errors early and clearly at runtime.
 * <p>
 * <p>For example, if the contract of a public method states it does not
 * allow {@code null} arguments, {@code Preconditions} can be used to validate that
 * contract. Doing this clearly indicates a contract violation when it
 * occurs and protects the class's invariants.
 *
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/Assert.java">Assert.java from Spring Framework</a>
 * @see <a href="https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Preconditions.java">Preconditions.java from Guava libraries</a>
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void isTrue(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by formatting the message template with the provided arguments.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to strings using {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void isTrue(
        boolean expression,
        String errorMessageTemplate,
        Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression   a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void state(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling instance, but not
     * involving any parameters to the calling method.
     *
     * @param expression           a boolean expression
     * @param errorMessageTemplate a template for the exception message should the check fail. The
     *                             message is formed by replacing each {@code %s} placeholder in the template with an
     *                             argument. These are matched by position - the first {@code %s} gets {@code
     *                             errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the formatted message in
     *                             square braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs     the arguments to be substituted into the message template. Arguments
     *                             are converted to strings using {@link String#valueOf(Object)}.
     * @throws IllegalStateException if {@code expression} is false
     */
    public static void state(
        boolean expression,
        String errorMessageTemplate,
        Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T notNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Assert that the given CharSequence is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">Preconditions.hasLength(name, "Name must not be empty");</pre>
     *
     * @param text         the CharSequence to check
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if the text is empty
     */
    public static void hasLength(CharSequence text, Object errorMessage) {
        if (!StringUtils.hasLength(text)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">Preconditions.hasText(name, "Name must not be empty");</pre>
     *
     * @param text         the CharSequence to check
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if the text is empty
     */
    public static void hasText(CharSequence text, Object errorMessage) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(array, "The array must contain elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not empty; that is, it must not be
     * {@code null} and must has content.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     * @see ObjectUtils#isEmpty(Object)
     */
    public static void notEmpty(Object object, String message) {
        if (ObjectUtils.isEmpty(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array is
     * {@code null} or does not contain any element.
     * <pre class="code">Assert.empty(array, "The array must not contain elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array contains elements
     */
    public static void empty(Object[] array, String message) {
        if (!ObjectUtils.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is empty; that is, it must be
     * {@code null} or must have no content.
     * <pre class="code">Assert.empty(collection, "Collection must not contain elements");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection
     *                                  contains elements
     * @see ObjectUtils#isEmpty(Object)
     */
    public static void empty(Object object, String message) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains no {@code null} elements.
     * <p>Note: Does not complain if the array is empty!
     * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }
}
