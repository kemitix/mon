package net.kemitix.mon;

import net.kemitix.mon.maybe.Maybe;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.kemitix.mon.maybe.Maybe.*;

class MaybeTest implements WithAssertions {

    private static <T> Predicate<T> eq(final T value) {
        return v -> Objects.equals(value, v);
    }

    @Test
    void justMustBeNonNull() {
        assertThatNullPointerException().isThrownBy(() -> just(null))
                .withMessageContaining("value");
    }

    @Test
    void nothingReusesTheSameInstance() {
        assertThat(nothing()).isSameAs(nothing());
    }

    @Test
    void equality() {
        assertThat(just(1)).isEqualTo(just(1));
        assertThat(just(1)).isNotEqualTo(just(2));
        assertThat(just(1)).isNotEqualTo(nothing());
        assertThat(nothing()).isEqualTo(nothing());
        assertThat(just(1).equals("1")).isFalse();
    }

    @Test
    void maybeAllowsNull() {
        assertThat(just(1)).isEqualTo(maybe(1));
        assertThat(nothing()).isEqualTo(maybe(null));
    }

    @Test
    void map() {
        assertThat(just(1).map(v -> v + 1)).isEqualTo(just(2));
        assertThat(nothing().map(v -> v)).isEqualTo(nothing());
    }

    @Test
    void mapToNull_thenJustNull() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final Maybe<Object> result = maybe.map(x -> null);
        //then
        result.match(
                just -> assertThat(just).isNull(),
                () -> fail("mapped to a null, not a Nothing - use flatMap() to convert to Nothing in null")
        );
    }

    @Test
    void optional_mapToNull_thenJustNull() {
        //given
        final Optional<Integer> optional = Optional.ofNullable(1);
        //when
        final Optional<Object> result = optional.map(x -> null);
        //then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void justHashCode() {
        assertThat(just(1).hashCode()).isNotEqualTo(just(2).hashCode());
    }

    @Test
    void nothingHashCode() {
        assertThat(nothing().hashCode()).isEqualTo(maybe(null).hashCode());
    }

    @Test
    void orElseGet() {
        assertThat(just(1).orElseGet(() -> -1)).isEqualTo(1);
        assertThat(nothing().orElseGet(() -> -1)).isEqualTo(-1);
    }

    @Test
    void orElse() {
        assertThat(just(1).orElse(-1)).isEqualTo(1);
        assertThat(nothing().orElse(-1)).isEqualTo(-1);
    }

    @Test
    void filter() {
        assertThat(just(1).filter(eq(1))).isEqualTo(just(1));
        assertThat(just(1).filter(eq(0))).isEqualTo(nothing());
        assertThat(nothing().filter(eq(1))).isEqualTo(nothing());
    }

    @Test
    void toOptional() {
        assertThat(just(1).toOptional()).isEqualTo(Optional.of(1));
        assertThat(nothing()
                .toOptional()).isEqualTo(Optional.empty());
    }

    @Test
    void mapFromOptional() {
        assertThat(Optional.of(1).map(Maybe::just).orElseGet(Maybe::nothing)).isEqualTo(just(1));
        assertThat(Optional.empty().map(Maybe::just).orElseGet(Maybe::nothing)).isEqualTo(nothing());
    }

    @Test
    void fromOptional() {
        assertThat(Maybe.fromOptional(Optional.of(1))).isEqualTo(just(1));
        assertThat(Maybe.fromOptional(Optional.empty())).isEqualTo(nothing());
    }

    @Test
    void peek() {
        final AtomicInteger ref = new AtomicInteger(0);
        assertThat(just(1).peek(x -> ref.incrementAndGet())).isEqualTo(just(1));
        assertThat(ref.get()).isEqualTo(1);

        assertThat(nothing().peek(x -> ref.incrementAndGet())).isEqualTo(nothing());
        assertThat(ref.get()).isEqualTo(1);
    }

    @Test
    void justOrThrowDoesNotThrow() {
        assertThatCode(() -> just(1).orElseThrow(IllegalStateException::new)).doesNotThrowAnyException();
    }

    @Test
    void justOrThrowReturnsValue() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final Integer result = maybe.orElseThrow(() -> new RuntimeException());
        //then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void nothingOrThrow() {
        assertThatThrownBy(() -> nothing().orElseThrow(IllegalStateException::new)).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    void justToStream() {
        //when
        final Stream<Integer> stream = just(1).stream();
        //then
        assertThat(stream).containsExactly(1);
    }

    @Test
    void nothingToStream() {
        //when
        final Stream<Object> stream = nothing().stream();
        //then
        assertThat(stream).isEmpty();
    }

    @Test
    void justFlatMap() {
        //given
        final Maybe<Integer> just1 = just(1);
        final Maybe<Integer> just2 = just(2);
        //when
        final Maybe<Integer> result = just1.flatMap(v1 ->
                just2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).contains(3);
    }

    @Test
    void nothingFlatMap() {
        //given
        final Maybe<Integer> nothing1 = nothing();
        final Maybe<Integer> nothing2 = nothing();
        //when
        final Maybe<Integer> result = nothing1.flatMap(v1 ->
                nothing2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).isEmpty();
    }

    @Test
    void justNothingFlatMap() {
        //given
        final Maybe<Integer> just1 = just(1);
        final Maybe<Integer> nothing2 = nothing();
        //when
        final Maybe<Integer> result = just1.flatMap(v1 ->
                nothing2.flatMap(v2 ->
                        Maybe.maybe(v1 + v2)
                ));
        //then
        assertThat(result.toOptional()).isEmpty();
    }

    @Test
    void just_ifNothing_isIgnored() {
        //given
        final Maybe<Integer> just = just(1);
        final AtomicBoolean capture = new AtomicBoolean(false);
        //when
        just.ifNothing(() -> capture.set(true));
        //then
        assertThat(capture).isFalse();
    }

    @Test
    void nothing_ifNothing_isCalled() {
        //given
        final Maybe<Integer> nothing = nothing();
        final AtomicBoolean capture = new AtomicBoolean(false);
        //when
        nothing.ifNothing(() -> capture.set(true));
        //then
        assertThat(capture).isTrue();
    }

    @Test
    void just_whenMatch_thenJustTriggers() {
        //given
        final Maybe<Integer> maybe = just(1);
        //then
        maybe.match(
                just -> assertThat(just).isEqualTo(1),
                () -> fail("Not nothing")
        );
    }

    @Test
    void nothing_whenMatch_thenNothingTriggers() {
        //given
        final Maybe<Integer> maybe = nothing();
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
    void just_whenMatchValue_thenJustTriggers() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final String result = maybe.matchValue(integer -> "just", () -> "nothing");
        //then
        assertThat(result).isEqualTo("just");
    }

    @Test
    void nothing_whenMatchValue_thenNothingTriggers() {
        //given
        final Maybe<Integer> maybe = nothing();
        //when
        final String result = maybe.matchValue(integer -> "just", () -> "nothing");
        //then
        assertThat(result).isEqualTo("nothing");
    }

    @Test
    void just_isJust_isTrue() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final boolean isJust = maybe.isJust();
        //then
        assertThat(isJust).isTrue();
    }

    @Test
    void just_isNothing_isFalse() {
        //given
        final Maybe<Integer> maybe = just(1);
        //when
        final boolean isNothing = maybe.isNothing();
        //then
        assertThat(isNothing).isFalse();
    }

    @Test
    void nothing_isJust_isFalse() {
        //given
        final Maybe<Object> maybe = nothing();
        //when
        final boolean isJust = maybe.isJust();
        //then
        assertThat(isJust).isFalse();
    }

    @Test
    void nothing_isNothing_isTrue() {
        //given
        final Maybe<Object> maybe = nothing();
        //when
        final boolean isNothing = maybe.isNothing();
        //then
        assertThat(isNothing).isTrue();
    }

    @Test
    void just_or_ignoreTheAlternative() {
        //given
        final Maybe<String> one = Maybe.just("one");
        //when
        final Maybe<String> result = one.or(() -> Maybe.just("two"));
        //then
        assertThat(result.toOptional()).contains("one");
    }

    @Test
    void nothing_or_isTheAlternative() {
        //given
        final Maybe<String> one = Maybe.nothing();
        //when
        final Maybe<String> result = one.or(() -> Maybe.just("two"));
        //then
        assertThat(result.toOptional()).contains("two");
    }

    @Test
    void emptyStream_findFirst_isNothing() {
        //given
        final Stream<Object> stream = Stream.empty();
        //when
        final Maybe<Object> result = Maybe.findFirst(stream);
        //then
        assertThat(result.isNothing()).isTrue();
    }

    @Test
    void singleItemStream_findFirst_isJustItem() {
        //given
        final String item = "item";
        final Stream<String> stream = Stream.of(item);
        //when
        final Maybe<String> result = Maybe.findFirst(stream);
        //then
        assertThat(result.toOptional()).contains(item);
    }

    @Test
    void multipleItemStream_findFirst_isJustFirst() {
        //given
        final String first = "first";
        final Stream<String> stream = Stream.of(first, "second");
        //when
        final Maybe<String> result = Maybe.findFirst(stream);
        //then
        assertThat(result.toOptional()).contains(first);
    }
}
