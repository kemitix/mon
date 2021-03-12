package net.kemitix.mon;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class AliasTest {

    interface TestString extends Alias<String> {}

    interface TestInteger extends Alias<Integer> {}

    interface TestIterableString extends Alias<Iterable<String>> { }

    interface TestListInteger extends Alias<List<Integer>> {}

    interface AnAlias extends Alias<String> {}

    @Test
    void shouldCreateATypeAliasAndGetTheValue() {
        //given
        final String value = "value";
        //when
        final TestString typeAlias = new TestString() {
            @Override
            public String value() {
                return value;
            }
        };
        //then
        assertThat(typeAlias.value()).isSameAs(value);
    }

    @Test
    void shouldCreateATypeAliasWithNestedGenericTypes() {
        //given
        final Iterable<String> iterable = Collections.emptyList();
        //when
        final TestIterableString typeAlias = () -> iterable;
        //then
        assertThat(typeAlias.value()).isSameAs(iterable);
    }

    @Test
    void shouldCreateATypeAliasSubclassAndGetTheValue() {
        //given
        final String value = "value";
        //when
        final AnAlias anAlias = () -> value;
        //then
        assertThat(anAlias.value()).isSameAs(value);
    }

    @Test
    void shouldNotBeEqualWhenValueTypesAreDifferent() {
        //given
        final TestString stringTypeAlias = () -> "1";
        final TestInteger integerTypeAlias = () -> 1;
        //then
        assertThat(stringTypeAlias).isNotEqualTo(integerTypeAlias);
    }

    @Test
    void shouldNotBeEqualEvenWhenValuesAreTheSame() {
        //given
        final String value = "value";
        final AnAlias anAlias1 = () -> value;
        final AnAlias anAlias2 = () -> value;
        //then
        assertThat(anAlias1).isNotEqualTo(anAlias2);
        // instead compare `.value()`s
        assertThat(anAlias1.value()).isEqualTo(anAlias2.value());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreNotTheSame() {
        //given
        final AnAlias valueA = () -> "value a";
        final AnAlias valueB = () -> "value b";
        //then
        assertThat(valueA).isNotEqualTo(valueB);
    }

    @Test
    void shouldNotBeEqualToRawValue() {
        //given
        final String value = "value";
        final AnAlias anAlias = () -> value;
        //then
        assertThat(anAlias).isNotEqualTo(value);
    }

    @Test
    void shouldNotHaveSameHashCodeOfValue() {
        //given
        final String value = "value";
        final AnAlias anAlias = () -> value;
        //then
        assertThat(anAlias).doesNotHaveSameHashCodeAs(value);
    }

    @Test
    @Disabled("no toString aliasing")
    void shouldHaveSameToStringAsAliasedType() {
        //given
        final List<Integer> value = Arrays.asList(1, 2, 3);
        //when
        final TestListInteger anAlias = () -> value;
        //then
        assertThat(anAlias.toString()).isEqualTo(value.toString());
    }

    @Test
    void shouldMapTypeAlias() {
        //given
        final AnAlias anAlias = () -> "text";
        //when
        final String value = anAlias.map(Strings::quote);
        //then
        assertThat(value).isEqualTo("'text'");
    }

    @Test
    void shouldFlatMapTypeAlias() {
        //given
        final AnAlias anAlias = () -> "text";
        //when
        final TestInteger result = anAlias.map(s -> s::length);
        //then
        assertThat(result.value()).isEqualTo(4);
    }

    @Test
    @DisplayName("equals other is null then not equals")
    void whenOtherNullEqualsIsFalse() {
        //given
        final AnAlias anAlias = () -> "text";
        //then
        boolean result = anAlias.equals(null);
        //then
        assertThat(result).isFalse();
    }
}
