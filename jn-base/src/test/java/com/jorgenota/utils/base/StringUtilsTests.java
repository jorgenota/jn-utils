package com.jorgenota.utils.base;

import org.junit.jupiter.api.Test;

import static com.jorgenota.utils.base.StringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/test/java/org/springframework/util/StringUtilsTests.java">StringUtilsTests.java from Spring Framework</a>
 */
public class StringUtilsTests {

    @Test
    public void testIsEmptyNullEmpty() {
        assertThat(isEmpty(null)).isTrue();
        assertThat(isEmpty("")).isTrue();
    }

    @Test
    public void testIsEmptyWhitespaceOrText() {
        assertThat(isEmpty(" ")).isFalse();
        assertThat(isEmpty("\t")).isFalse();
        assertThat(isEmpty("a")).isFalse();
    }

    @Test
    public void testHasTextNullEmpty() {
        assertThat(hasText(null)).isFalse();
        assertThat(hasText("")).isFalse();
    }

    @Test
    public void testHasTextValid() {
        assertThat(hasText("t")).isTrue();
    }

    @Test
    public void testTrimWhitespace() {
        assertThat(StringUtils.trimWhitespace(null)).isNull();
        assertThat("").isEqualTo(trimWhitespace(""));
        assertThat("").isEqualTo(trimWhitespace(" "));
        assertThat("").isEqualTo(trimWhitespace("\t"));
        assertThat("a").isEqualTo(trimWhitespace(" a"));
        assertThat("a").isEqualTo(trimWhitespace("a "));
        assertThat("a").isEqualTo(trimWhitespace(" a "));
        assertThat("a b").isEqualTo(trimWhitespace(" a b "));
        assertThat("a b  c").isEqualTo(trimWhitespace(" a b  c "));
    }

}
