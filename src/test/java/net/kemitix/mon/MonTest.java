package net.kemitix.mon;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

public class MonTest {

    @Test
    public void canCreateAndMapCanGetValue() {
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
    public void canMap() {
        //given
        final Mon<String> wrap = Mon.of("test");
        //when
        final Mon<String> updated = wrap.map(a -> a + " more");
        //then
        assertMonContains(updated, "test more");
    }

    @Test
    public void canMapInstance() {
        //given
        final Mon<String> wrap = Mon.of("test");
        //when
        final Mon<Integer> result = wrap.map(String::length);
        //then
        assertMonContains(result, 4);
    }

    @Test
    public void createWithValidatorAndContinuations() {
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

    public void canCompare() {
        //given
        final Mon<String> one = Mon.of("test");
        final Mon<String> same = Mon.of("test");
        final Mon<String> other = Mon.of("other");
        //then
        assertThat(one).isEqualTo(same);
        assertThat(one).isNotEqualTo(other);
    }

    @Test
    public void canFlatMap() {
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
    public void ofRequiresNonNull() {
        assertThatNullPointerException().isThrownBy(() -> Mon.of(null));
    }

    @Test
    public void factoryRequiresValidator() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(null, Optional::of, Optional::empty))
                .withMessage("validator");
    }

    @Test
    public void factoryRequiresOnValid() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(v -> true, null, Optional::empty))
                .withMessage("onValid");
    }

    @Test
    public void factoryRequiresOnInvalid() {
        assertThatNullPointerException().isThrownBy(
                () -> Mon.factory(v -> true, Optional::of, null))
                .withMessage("onInvalid");
    }

    @Test
    public void shouldGetInvalidResultWhenFactoryApplyWithNull() {
        //given
        final Function<Object, Optional<?>> factory = Mon.factory(v -> true, Optional::of, Optional::empty);
        //when
        final Optional<?> result = factory.apply(null);
        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldHaveHashCodeBasedOnContent() {
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
    public void shouldHaveEquals() {
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
