package net.kemitix.mon;

import org.assertj.core.util.Strings;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeAliasTest {

    @Test
    public void shouldCreateATypeAliasAndGetTheValue() {
        //given
        final String value = "value";
        //when
        final TypeAlias<String> typeAlias = givenTypeAlias(value);
        //then
        assertThat(typeAlias.getValue()).isSameAs(value);
    }

    private TypeAlias<String> givenTypeAlias(final String value) {
        return new TypeAlias<String>(value) {
        };
    }

    @Test
    public void shouldCreateATypeAliasWithNestedGenericTypes() {
        //given
        final Iterable<String> iterable = Collections.emptyList();
        //when
        final TypeAlias<Iterable<String>> typeAlias =
                new TypeAlias<Iterable<String>>(iterable) {
        };
        //then
        assertThat(typeAlias.getValue()).isSameAs(iterable);
    }

    @Test
    public void shouldCreateATypeAliasSubclassAndGetTheValue() {
        //given
        final String value = "value";
        //when
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.getValue()).isSameAs(value);
    }

    @Test
    public void shouldNotBeEqualWhenValueTypesAreDifferent() {
        //given
        final TypeAlias<String> stringTypeAlias = givenTypeAlias("1");
        final TypeAlias<Integer> integerTypeAlias = new TypeAlias<Integer>(1) {
        };
        //then
        assertThat(stringTypeAlias).isNotEqualTo(integerTypeAlias);
    }

    @Test
    public void shouldBeEqualWhenValuesAreTheSame() {
        //given
        final String value = "value";
        final AnAlias anAlias1 = AnAlias.of(value);
        final AnAlias anAlias2 = AnAlias.of(value);
        //then
        assertThat(anAlias1).isEqualTo(anAlias2);
    }

    @Test
    public void shouldNotBeEqualWhenValuesAreNotTheSame() {
        //given
        final AnAlias valueA = AnAlias.of("value a");
        final AnAlias valueB = AnAlias.of("value b");
        //then
        assertThat(valueA).isNotEqualTo(valueB);
    }

    @Test
    public void shouldBeEqualToRawValue() {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias).isEqualTo(value);
    }

    @Test
    public void shouldHaveHashCodeOfValue() {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.hashCode()).isEqualTo(value.hashCode());
    }

    @Test
    public void shouldHaveSameToStringAsAliasedType() {
        //given
        final List<Integer> value = Arrays.asList(1, 2, 3);
        //when
        final TypeAlias<List<Integer>> anAlias = new TypeAlias<List<Integer>>(value) {
        };
        //then
        assertThat(anAlias.toString()).isEqualTo(value.toString());
    }

    @Test
    public void shouldMapTypeAlias() {
        //given
        final AnAlias anAlias = AnAlias.of("text");
        //when
        final String value = anAlias.map(Strings::quote);
        //then
        assertThat(value).isEqualTo("'text'");
    }

    private static class AnAlias extends TypeAlias<String> {

        /**
         * Constructor.
         *
         * @param value the value
         */
        protected AnAlias(final String value) {
            super(value);
        }

        protected static AnAlias of(final String value) {
            return new AnAlias(value);
        }
    }
}
