package net.kemitix.mon;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MonTest {

    @Test
    void canCreateAndMapCanGetValue() {
        //when
        final Mon<String> wrap = Mon.of("test");
        //then
        assertMonContains(wrap, "test");
    }

    private <T> void assertMonContains(
            final Mon<T> wrap,
            final T expected
                                      ) {
        wrap.map(value -> assertThat(value).isEqualTo(expected));
    }

    @Test
    void canMap() {
        //given
        final Mon<String> wrap = Mon.of("test");
        //when
        final Mon<String> updated = wrap.map(a -> a + " more");
        //then
        assertMonContains(updated, "test more");
    }

    @Test
    void canMapInstance() {
        //given
        final Mon<String> wrap = Mon.of("test");
        //when
        final Mon<Integer> result = wrap.map(String::length);
        //then
        assertMonContains(result, 4);
    }

    @Test
    void createWithValidatorAndContinuations() {
        //given
        final Function<String, Optional<Mon<String>>> factory =
                Mon.factory(
                        v -> v.length() <= 10,
                        Optional::of,
                        Optional::empty
                           );
        //when
        final Optional<Mon<String>> shortAndValid = factory.apply("value okay");
        final Optional<Mon<String>> longAndInvalid = factory.apply("value is too long");
        //then
        assertThat(shortAndValid).isNotEmpty();
        shortAndValid.ifPresent(valid -> assertMonContains(valid, "value okay"));
        assertThat(longAndInvalid).isEmpty();
    }

    @Test

    void canCompare() {
        //given
        final Mon<String> one = Mon.of("test");
        final Mon<String> same = Mon.of("test");
        final Mon<String> other = Mon.of("other");
        //then
        assertThat(one).isEqualTo(same);
        assertThat(one).isNotEqualTo(other);
    }

    @Test
    void canFlatMap() {
        //given
        final Mon<String> wrap = Mon.of("test");
        //when
        final Mon<Mon<String>> nonFlatMapped = wrap.map(Mon::of);
        final Mon<String> result = wrap.flatMap(Mon::of);
        //then
        assertMonContains(result, "test");
        nonFlatMapped.map(inner -> assertThat(result).isEqualTo(inner));
    }

    @Test
    void ofRequiresNonNull() {
        assertThatNullPointerException().isThrownBy(() -> Mon.of(null));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void factoryRequiresValidator() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(null, Optional::of, Optional::empty))
                .withMessageContaining("validator");
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void factoryRequiresOnValid() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(v -> true, null, Optional::empty))
                .withMessageContaining("onValid");
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void factoryRequiresOnInvalid() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(v -> true, Optional::of, null))
                .withMessageContaining("onInvalid");
    }

    @Test
    void factory() {
        //given
        final Function<Integer, Optional<?>> evenMonFactory =
                Mon.factory((Integer v) -> v % 2 == 0, Optional::of, Optional::empty);
        //when
        final Optional<?> oddResult = evenMonFactory.apply(1);
        final Optional<?> evenResult = evenMonFactory.apply(2);
        //then
        assertThat(oddResult).isEmpty();// because 1 % 2 != 0
        assertThat(evenResult).isNotEmpty(); // because 2 % 2 == 0
        evenResult.ifPresent(value -> assertThat(value).isEqualTo(Mon.of(2)));
    }

    @Test
    void shouldGetInvalidResultWhenFactoryApplyWithNull() {
        //given
        final Function<Object, Optional<?>> factory = Mon.factory(v -> true, Optional::of, Optional::empty);
        //when
        final Optional<?> result = factory.apply(null);
        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHaveHashCodeBasedOnContent() {
        //given
        final int hashOfOne = Mon.of("one")
                .hashCode();
        final int hashOfTwo = Mon.of("two")
                .hashCode();
        final int otherHashOfAOne = Mon.of("one")
                .hashCode();
        //then
        assertThat(hashOfOne).isNotEqualTo(hashOfTwo);
        assertThat(hashOfOne).isEqualTo(otherHashOfAOne);
    }

    @Test
    void shouldHaveEquals() {
        //given
        final Mon<String> one = Mon.of("one");
        final Mon<String> two = Mon.of("two");
        final Mon<String> otherOne = Mon.of("one");
        final Integer notAMon = 1;
        //then
        assertThat(one).isEqualTo(one);
        assertThat(one).isNotEqualTo(two);
        assertThat(one).isEqualTo(otherOne);
        assertThat(one).isNotEqualTo(notAMon);
        assertThat(one).isNotEqualTo(null);
    }
}
