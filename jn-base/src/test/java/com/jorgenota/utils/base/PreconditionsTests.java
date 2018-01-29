package com.jorgenota.utils.base;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/test/java/org/springframework/util/StringUtilsTests.java">StringUtilsTests.java from Spring Framework</a>
 */
public class PreconditionsTests {

    @Test
    public void testIsTrue() {
        Preconditions.isTrue(true, "enigma");
    }

    @Test
    public void testIsTrueWithFalse() {
        try {
            Preconditions.isTrue(false, "enigma");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    public void testIsTrueWithFalseAndNullMessage() {
        try {
            Preconditions.isTrue(false, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("null");
        }
    }

    @Test
    public void testIsTrueWithMessageWithArgs() {
        Preconditions.isTrue(true, "bla %s %d", "A", 4);
    }

    @Test
    public void testIsTrueWithFalseAndMessageWithArgs() {
        try {
            Preconditions.isTrue(false, "bla %s %d", "A", 4);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("bla A 4");
        }
    }

    @Test
    public void testState() {
        Preconditions.state(true, "enigma");
    }

    @Test
    public void testStateWithFalse() {
        try {
            Preconditions.state(false, "enigma");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    public void testStateWithFalseAndNullMessage() {
        try {
            Preconditions.state(false, null);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("null");
        }
    }

    @Test
    public void testStateWithMessageWithArgs() {
        Preconditions.state(true, "bla %s %d", "A", 4);
    }

    @Test
    public void testStateWithFalseAndMessageWithArgs() {
        try {
            Preconditions.state(false, "bla %s %d", "A", 4);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("bla A 4");
        }
    }

    @Test
    public void testNotNull() {
        Integer i = new Integer(5);
        Integer result = Preconditions.notNull(i, "enigma");
        assertThat(result).isSameAs(i);
    }

    @Test
    public void testNotNullWithNull() {
        try {
            Preconditions.notNull(null, "enigma");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    public void testHasLength() {
        Preconditions.hasLength("I Heart ...", "enigma");
        Preconditions.hasLength("\t  ", "enigma");
    }

    @Test
    public void testHasLengthWithEmptyString() {
        try {
            Preconditions.hasLength("", "enigma hasLength");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasLength");
        }
    }

    @Test
    public void testHasLengthWithNull() {
        try {
            Preconditions.hasLength(null, "enigma");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma");
        }
    }

    @Test
    public void testHasText() {
        Preconditions.hasText("I Heart ...", "enigma");
    }

    @Test
    public void testHasTextWithEmptyString() {
        try {
            Preconditions.hasText("", "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    public void testHasTextWithNull() {
        try {
            Preconditions.hasText(null, "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    public void testHasTextWithWhitespace() {
        try {
            Preconditions.hasText("    \t \n  ", "enigma hasText");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma hasText");
        }
    }

    @Test
    public void testNotEmptyArray() {
        Preconditions.notEmpty(new String[]{"1234"}, "enigma");
        Preconditions.notEmpty(new String[]{null}, "enigma");
    }

    @Test
    public void testNotEmptyArrayWithEmptyArray() {
        try {
            Preconditions.notEmpty(new String[]{}, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyArrayWithNullArray() {
        try {
            Preconditions.notEmpty((Object[]) null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }


    @Test
    public void testNotEmptyObject() {
        Preconditions.notEmpty("I Heart ...", "enigma");
        Preconditions.notEmpty(5, "enigma");
        Preconditions.notEmpty(singletonMap("foo", "bar"), "enigma");
        Preconditions.notEmpty(singletonList("foo"), "enigma");
        Preconditions.notEmpty(Optional.of("foo"), "enigma");
    }

    @Test
    public void testNotEmptyWithEmptyString() {
        try {
            Preconditions.notEmpty("", "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyWithNullCollection() {
        try {
            Preconditions.notEmpty((Collection<?>) null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyWithEmptyCollection() {
        try {
            Preconditions.notEmpty(emptyList(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyWithNullMap() {
        try {
            Preconditions.notEmpty((Map<?, ?>) null, "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyWithEmptyMap() {
        try {
            Preconditions.notEmpty(emptyMap(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

    @Test
    public void testNotEmptyWithEmptyOptional() {
        try {
            Preconditions.notEmpty(Optional.empty(), "enigma notEmpty");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("enigma notEmpty");
        }
    }

}
