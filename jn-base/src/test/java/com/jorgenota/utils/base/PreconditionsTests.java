package com.jorgenota.utils.base;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 *
 */
class PreconditionsTests {

    @Test
    void testIsTrue() {
        Preconditions.isTrue(true, "enigma");
    }

    @Test
    void testIsTrueWithFalse() {
        try {
            Preconditions.isTrue(false, "enigma");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    void testIsTrueWithFalseAndNullMessage() {
        try {
            //noinspection ConstantConditions
            Preconditions.isTrue(false, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("null");
        }
    }

    @Test
    void testIsTrueWithMessageWithArgs() {
        Preconditions.isTrue(true, "bla %s %d", "A", 4);
    }

    @Test
    void testIsTrueWithFalseAndMessageWithArgs() {
        try {
            Preconditions.isTrue(false, "bla %s %d", "A", 4);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("bla A 4");
        }
    }

    @Test
    void testState() {
        Preconditions.state(true, "enigma");
    }

    @Test
    void testStateWithFalse() {
        try {
            Preconditions.state(false, "enigma");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    void testStateWithFalseAndNullMessage() {
        try {
            //noinspection ConstantConditions
            Preconditions.state(false, null);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("null");
        }
    }

    @Test
    void testStateWithMessageWithArgs() {
        Preconditions.state(true, "bla %s %d", "A", 4);
    }

    @Test
    void testStateWithFalseAndMessageWithArgs() {
        try {
            Preconditions.state(false, "bla %s %d", "A", 4);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("bla A 4");
        }
    }

    @Test
    void testNotNull() {
        Integer i = 5;
        Integer result = Preconditions.notNull(i, "enigma");
        assertThat(result).isSameAs(i);
    }

    @Test
    void testNotNullWithNull() {
        try {
            Preconditions.notNull(null, "enigma");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    void testHasLength() {
        Preconditions.hasLength("I Heart ...", "enigma");
        Preconditions.hasLength("\t  ", "enigma");
    }

    @Test
    void testHasLengthWithEmptyString() {
        try {
            Preconditions.hasLength("", "enigma hasLength");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasLength");
        }
    }

    @Test
    void testHasLengthWithNull() {
        try {
            Preconditions.hasLength(null, "enigma");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    void testHasText() {
        Preconditions.hasText("I Heart ...", "enigma");
    }

    @Test
    void testHasTextWithEmptyString() {
        try {
            Preconditions.hasText("", "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    void testHasTextWithNull() {
        try {
            Preconditions.hasText(null, "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    void testHasTextWithWhitespace() {
        try {
            Preconditions.hasText("    \t \n  ", "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    void testEmptyArray() {
        Preconditions.empty(new String[]{}, "enigma");
        Preconditions.empty(null, "enigma");
    }

    @Test
    void testEmptyArrayWithNotEmptyArray() {
        try {
            Preconditions.empty(new String[]{null}, "enigma empty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma empty");
        }

        try {
            Preconditions.empty(new String[]{"a"}, "enigma empty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma empty");
        }
    }

    @Test
    void testEmptyCollections() {
        Preconditions.empty("", "enigma");
        Preconditions.empty((Collection<?>) null, "enigma");
        Preconditions.empty((Map<?, ?>) null, "enigma");
        Preconditions.empty(emptyList(), "enigma");
        Preconditions.empty(emptyMap(), "enigma");
        Preconditions.empty(Optional.empty(), "enigma");
    }

    @Test
    void testNotEmptyArray() {
        Preconditions.notEmpty(new String[]{"1234"}, "enigma");
        Preconditions.notEmpty(new String[]{null}, "enigma");
    }

    @Test
    void testNotEmptyArrayWithEmptyArray() {
        try {
            Preconditions.notEmpty(new String[]{}, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyArrayWithNullArray() {
        try {
            Preconditions.notEmpty(null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }


    @Test
    void testNotEmptyObject() {
        Preconditions.notEmpty("I Heart ...", "enigma");
        Preconditions.notEmpty(5, "enigma");
        Preconditions.notEmpty(singletonMap("foo", "bar"), "enigma");
        Preconditions.notEmpty(singletonList("foo"), "enigma");
        Preconditions.notEmpty(Optional.of("foo"), "enigma");
    }

    @Test
    void testNotEmptyWithEmptyString() {
        try {
            Preconditions.notEmpty("", "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyWithNullCollection() {
        try {
            Preconditions.notEmpty((Collection<?>) null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyWithEmptyCollection() {
        try {
            Preconditions.notEmpty(emptyList(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyWithNullMap() {
        try {
            Preconditions.notEmpty((Map<?, ?>) null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyWithEmptyMap() {
        try {
            Preconditions.notEmpty(emptyMap(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNotEmptyWithEmptyOptional() {
        try {
            Preconditions.notEmpty(Optional.empty(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    void testNoNullElements() {
        Preconditions.noNullElements(new String[]{"1234"}, "enigma");
        Preconditions.noNullElements(new String[]{}, "enigma");
    }

    @Test
    void testNoNullElementsWithNullElements() {
        try {
            Preconditions.noNullElements(new String[]{"foo", null, "bar"}, "enigma notNull");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notNull");
        }
    }

}
