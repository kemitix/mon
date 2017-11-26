package net.kemitix.mon;

import org.junit.Test;

import java.util.Collections;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeAliasTest {

    @Test
    public void shouldCreateATypeAliasAndGetTheValue() throws Exception {
        //given
        final String value = "value";
        //when
        final TypeAlias<String> typeAlias = givenTypeAlias(value);
        //then
        assertThat(typeAlias.<Boolean>map(value::equals)).isTrue();
    }

    @Test
    public void shouldCreateATypeAliasWithNestedGenericTypes() {
        //given
        final Iterable<String> iterable = Collections.emptyList();
        //when
        final TypeAlias<Iterable<String>> typeAlias =
                new TypeAlias<Iterable<String>>(iterable, Iterable.class) {
        };
        //then
        assertThat(typeAlias.<Boolean>map(iterable::equals)).isTrue();
    }

    private TypeAlias<String> givenTypeAlias(final String value) {
        return new TypeAlias<String>(value, String.class) {
        };
    }

    @Test
    public void shouldCreateAnAliasedTypeAndGetTheValue() throws Exception {
        //given
        final String value = "value";
        //when
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.<Boolean>map(value::equals)).isTrue();
    }

    @Test
    public void shouldNotBeEqualWhenValueTypesAreDifferent() {
        //given
        final TypeAlias<String> stringTypeAlias = givenTypeAlias("1");
        final TypeAlias<Integer> integerTypeAlias = new TypeAlias<Integer>(1, Integer.class){};
        //then
        assertThat(stringTypeAlias).isNotEqualTo(integerTypeAlias);
    }

    @Test
    public void shouldBeEqualWhenValuesAreTheSame() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias1 = AnAlias.of(value);
        final AnAlias anAlias2 = AnAlias.of(value);
        //then
        assertThat(anAlias1).isEqualTo(anAlias2);
    }

    @Test
    public void shouldBeEqualToUnAliasedValue() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias).isEqualTo(value);
    }

    @Test
    public void shouldHaveHashCodeOfValue() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.hashCode()).isEqualTo(value.hashCode());
    }

    @Test
    public void shouldHaveSameToStringAsAliasedType() throws Exception {
        //given
        final String value = "value";
        //when
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.toString()).isEqualTo(value);
    }

    @Test
    public void shouldMapTypeAlias() {
        //given
        final AnAlias anAlias = AnAlias.of("text");
        final Function<String, String> function = v -> v;
        //when
        final String value = anAlias.map(function);
        //then
        assertThat(value).isEqualTo("text");
    }

    private static class AnAlias extends TypeAlias<String> {

        /**
         * Constructor.
         *
         * @param value the value
         */
        protected AnAlias(final String value) {
            super(value, String.class);
        }

        protected static AnAlias of(final String value) {
            return new AnAlias(value);
        }
    }
}
