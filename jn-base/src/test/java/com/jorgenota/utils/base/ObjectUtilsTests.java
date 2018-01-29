package com.jorgenota.utils.base;

import org.junit.jupiter.api.Test;

import java.util.*;

import static com.jorgenota.utils.base.ObjectUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/test/java/org/springframework/util/ObjectUtilsTests.java">ObjectUtilsTests.java from Spring Framework</a>
 */
public class ObjectUtilsTests {

    @Test
    public void isEmptyNull() {
        assertThat(ObjectUtils.isEmpty(null)).isTrue();
    }

    @Test
    public void isEmptyArray() {
        assertThat(isEmpty(new char[0])).isTrue();
        assertThat(isEmpty(new Object[0])).isTrue();
        assertThat(isEmpty(new Integer[0])).isTrue();

        assertThat(isEmpty(new int[]{42})).isFalse();
        assertThat(isEmpty(new Integer[]{new Integer(42)})).isFalse();
    }

    @Test
    public void isEmptyCollection() {
        assertThat(isEmpty(Collections.emptyList())).isTrue();
        assertThat(isEmpty(Collections.emptySet())).isTrue();

        Set<String> set = new HashSet<>();
        set.add("foo");
        assertThat(isEmpty(set)).isFalse();
        assertThat(isEmpty(Arrays.asList("foo"))).isFalse();
    }

    @Test
    public void isEmptyMap() {
        assertThat(isEmpty(Collections.emptyMap())).isTrue();

        HashMap<String, Object> map = new HashMap<>();
        map.put("foo", 42L);
        assertThat(isEmpty(map)).isFalse();
    }

    @Test
    public void isEmptyCharSequence() {
        assertThat(isEmpty(new StringBuilder())).isTrue();
        assertThat(isEmpty("")).isTrue();

        assertThat(isEmpty(new StringBuilder("foo"))).isFalse();
        assertThat(isEmpty("   ")).isFalse();
        assertThat(isEmpty("\t")).isFalse();
        assertThat(isEmpty("foo")).isFalse();
    }

    @Test
    public void isEmptyUnsupportedObjectType() {
        assertThat(isEmpty(42L)).isFalse();
        assertThat(isEmpty(new Object())).isFalse();
    }
}
