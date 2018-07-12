package net.kemitix.mon;

import net.kemitix.mon.maybe.Maybe;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.kemitix.mon.maybe.Maybe.*;

public class MaybeTest implements WithAssertions {

    private static <T> Predicate<T> eq(final T value) {
        return v -> Objects.equals(value, v);
    }

    @Test
    public void justMustBeNonNull() {
        assertThatNullPointerException().isThrownBy(() -> just(null))
                .withMessageContaining("value");
    }

    @Test
    public void nothingReusesTheSameInstance() {
        assertThat(nothing()).isSameAs(nothing());
    }

    @Test
    public void equality() {
        assertThat(just(1)).isEqualTo(just(1));
        assertThat(just(1)).isNotEqualTo(just(2));
        assertThat(just(1)).isNotEqualTo(nothing());
        assertThat(nothing()).isEqualTo(nothing());
        assertThat(just(1).equals("1")).isFalse();
    }

    @Test
    public void maybeAllowsNull() {
        assertThat(just(1)).isEqualTo(maybe(1));
        assertThat(nothing()).isEqualTo(maybe(null));
    }

    @Test
    public void map() {
        assertThat(just(1).map(v -> v + 1)).isEqualTo(just(2));
        assertThat(nothing().map(v -> v)).isEqualTo(nothing());
    }

    @Test
    public void justHashCode() {
        assertThat(just(1).hashCode()).isNotEqualTo(just(2).hashCode());
    }

    @Test
    public void nothingHashCode() {
        assertThat(nothing().hashCode()).isEqualTo(maybe(null).hashCode());
    }

    @Test
    public void orElseGet() {
        assertThat(just(1).orElseGet(() -> -1)).isEqualTo(1);
        assertThat(nothing().orElseGet(() -> -1)).isEqualTo(-1);
    }

    @Test
    public void orElse() {
        assertThat(just(1).orElse(-1)).isEqualTo(1);
        assertThat(nothing().orElse(-1)).isEqualTo(-1);
    }

    @Test
    public void filter() {
        assertThat(just(1).filter(eq(1))).isEqualTo(just(1));
        assertThat(just(1).filter(eq(0))).isEqualTo(nothing());
        assertThat(nothing().filter(eq(1))).isEqualTo(nothing());
    }

    @Test
    public void toOptional() {
        assertThat(just(1).toOptional()).isEqualTo(Optional.of(1));
        assertThat(nothing()
                .toOptional()).isEqualTo(Optional.empty());
    }

    @Test
    public void fromOptional() {
        assertThat(Optional.of(1).map(Maybe::just).orElseGet(Maybe::nothing)).isEqualTo(just(1));
        assertThat(Optional.empty().map(Maybe::just).orElseGet(Maybe::nothing)).isEqualTo(nothing());
    }

    @Test
    public void peek() {
        final AtomicInteger ref = new AtomicInteger(0);
        assertThat(just(1).peek(x -> ref.incrementAndGet())).isEqualTo(just(1));
        assertThat(ref.get()).isEqualTo(1);

        assertThat(nothing().peek(x -> ref.incrementAndGet())).isEqualTo(nothing());
        assertThat(ref.get()).isEqualTo(1);
    }

    @Test
    public void justOrThrow() {
        assertThatCode(() -> just(1).orElseThrow(IllegalStateException::new)).doesNotThrowAnyException();
    }

    @Test
    public void nothingOrThrow() {
        assertThatThrownBy(() -> nothing().orElseThrow(IllegalStateException::new)).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    public void justToStream() {
        //when
        final Stream<Integer> stream = just(1).stream();
        //then
        assertThat(stream).containsExactly(1);
    }

    @Test
    public void nothingToStream() {
        //when
        final Stream<Object> stream = nothing().stream();
        //then
        assertThat(stream).isEmpty();
    }

    @Test
    public void justFlatMap() {
        //given
        final Maybe<Integer> just1 = Maybe.just(1);
        final Maybe<Integer> just2 = Maybe.just(2);
        //when
        final Maybe<Integer> result = just1.flatMap(v1 ->
                just2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).contains(3);
    }

    @Test
    public void nothingFlatMap() {
        //given
        final Maybe<Integer> nothing1 = Maybe.nothing();
        final Maybe<Integer> nothing2 = Maybe.nothing();
        //when
        final Maybe<Integer> result = nothing1.flatMap(v1 ->
                nothing2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).isEmpty();
    }

    @Test
    public void justNothingFlatMap() {
        //given
        final Maybe<Integer> just1 = Maybe.just(1);
        final Maybe<Integer> nothing2 = Maybe.nothing();
        //when
        final Maybe<Integer> result = just1.flatMap(v1 ->
                nothing2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).isEmpty();
    }

    @Test
    public void just_ifNothing_isIgnored() {
        //given
        final Maybe<Integer> just = Maybe.just(1);
        final AtomicBoolean capture = new AtomicBoolean(false);
        //when
        just.ifNothing(() -> capture.set(true));
        //then
        assertThat(capture).isFalse();
    }

    @Test
    public void nothing_ifNothing_isCalled() {
        //given
        final Maybe<Integer> nothing = Maybe.nothing();
        final AtomicBoolean capture = new AtomicBoolean(false);
        //when
        nothing.ifNothing(() -> capture.set(true));
        //then
        assertThat(capture).isTrue();
    }

    @Test
    public void just_whenMatch_thenJustTriggers() {
        //given
        final Maybe<Integer> maybe = Maybe.just(1);
        //then
        maybe.match(
                just -> assertThat(just).isEqualTo(1),
                () -> fail("Not nothing")
        );
    }

    @Test
    public void nothing_whenMatch_thenNothingTriggers() {
        //given
        final Maybe<Integer> maybe = Maybe.nothing();
        final AtomicBoolean flag = new AtomicBoolean(false);
        //when
        maybe.match(
                just -> fail("Not a just"),
                () -> flag.set(true)
        );
        //then
        assertThat(flag).isTrue();
    }

    @Test
    public void just_isJust_isTrue() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final boolean isJust = maybe.isJust();
        //then
        assertThat(isJust).isTrue();
    }

    @Test
    public void just_isNothing_isFalse() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final boolean isNothing = maybe.isNothing();
        //then
        assertThat(isNothing).isFalse();
    }

    @Test
    public void nothing_isJust_isFalse() {
        //given
        final Maybe<Object> maybe = nothing();
        //when
        final boolean isJust = maybe.isJust();
        //then
        assertThat(isJust).isFalse();
    }

    @Test
    public void nothing_isNothing_isTrue() {
        //given
        final Maybe<Object> maybe = nothing();
        //when
        final boolean isNothing = maybe.isNothing();
        //then
        assertThat(isNothing).isTrue();
    }

}
