package net.kemitix.mon;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WrapperTest {

    interface WrappedString extends Wrapper<String> {}

    interface WrappedInteger extends Wrapper<Integer> {}

    interface WrappedIterableString extends Wrapper<Iterable<String>> { }

    interface AWrapper extends Wrapper<String> {}

    @Test
    void shouldCreateAWrapperAndGetTheValue() {
        //given
        final String value = "value";
        //when
        // - anonymous class syntax
        final WrappedString wrappedString = new WrappedString() {
            @Override
            public String value() {
                return value;
            }
        };
        //then
        assertThat(wrappedString.value()).isSameAs(value);
    }

    @Test
    void shouldCreateAWrapperWithNestedGenericTypes() {
        //given
        final Iterable<String> iterable = Collections.emptyList();
        //when
        // - functional interface / lambda syntax
        final WrappedIterableString wrappedIterableString = () -> iterable;
        //then
        assertThat(wrappedIterableString.value()).isSameAs(iterable);
    }

    @Test
    void shouldCreateAWrapperSubclassAndGetTheValue() {
        //given
        final String value = "value";
        //when
        final AWrapper aWrapper = () -> value;
        //then
        assertThat(aWrapper.value()).isSameAs(value);
    }

    @Test
    void shouldNotBeEqualWhenValueTypesAreDifferent() {
        //given
        final WrappedString wrappedString = () -> "1";
        final WrappedInteger wrappedInteger = () -> 1;
        //then
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(wrappedString).isNotEqualTo(wrappedInteger);
    }

    @Test
    void shouldNotBeEqualEvenWhenValuesAreTheSame() {
        //given
        final String value = "value";
        final AWrapper aWrapper1 = () -> value;
        final AWrapper aWrapper2 = () -> value;
        //then
        assertThat(aWrapper1).isNotEqualTo(aWrapper2);
        // instead compare `.value()`s
        assertThat(aWrapper1.value()).isEqualTo(aWrapper2.value());
    }

}
