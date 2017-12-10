package net.kemitix.mon;

import org.junit.Test;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static net.kemitix.mon.Maybe.just;
import static net.kemitix.mon.Maybe.maybe;
import static net.kemitix.mon.Maybe.nothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MaybeTest {

    private static <T> Predicate<T> eq(final T value) {
        return v -> Objects.equals(value, v);
    }

    @Test
    public void documentation() {
        assertThat(Maybe.maybe(null)).isEqualTo(Maybe.nothing());
        assertThat(Maybe.maybe(1)).isEqualTo(Maybe.just(1));
        assertThat(Maybe.nothing()
                        .orElseGet(() -> 1)).isEqualTo(1);
        assertThat(Maybe.just(1)
                        .orElseGet(() -> 2)).isEqualTo(1);
        assertThat(Maybe.nothing()
                        .orElse(1)).isEqualTo(1);
        assertThat(Maybe.just(1)
                        .orElse(2)).isEqualTo(1);
        assertThat(Maybe.just(1)
                        .filter(v -> v > 2)).isEqualTo(Maybe.nothing());
        assertThat(Maybe.just(3)
                        .filter(v -> v > 2)).isEqualTo(Maybe.just(3));
        assertThat(Maybe.just(1)
                        .toOptional()).isEqualTo(Optional.of(1));
        assertThat(Maybe.nothing()
                        .toOptional()).isEqualTo(Optional.empty());
        assertThat(Maybe.fromOptional(Optional.of(1))).isEqualTo(Maybe.just(1));
        assertThat(Maybe.fromOptional(Optional.empty())).isEqualTo(Maybe.nothing());
        final AtomicInteger reference = new AtomicInteger(0);
        assertThat(Maybe.just(1)
                        .peek(reference::set)).isEqualTo(Maybe.just(1));
        assertThat(reference).hasValue(1);
        assertThat(Maybe.nothing()
                        .peek(v -> reference.incrementAndGet())).isEqualTo(Maybe.nothing());
        assertThat(reference).hasValue(1);
        assertThatCode(() -> Maybe.just(1)
                                  .orElseThrow(IllegalStateException::new)).doesNotThrowAnyException();
        assertThatThrownBy(() -> Maybe.nothing()
                                      .orElseThrow(IllegalStateException::new)).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    public void justMustBeNonNull() {
        assertThatNullPointerException().isThrownBy(() -> just(null))
                                        .withMessage("value");
    }

    @Test
    public void nothingReusesTheSameInstance() {
        assertThat(nothing()).isSameAs(nothing());
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
    public void testHashCode() {
        assertThat(just(1).hashCode()).isEqualTo(Objects.hashCode(1));
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
        assertThat(Maybe.nothing()
                        .toOptional()).isEqualTo(Optional.empty());
    }

    @Test
    public void fromOptional() {
        assertThat(Maybe.fromOptional(Optional.of(1))).isEqualTo(just(1));
        assertThat(Maybe.fromOptional(Optional.empty())).isEqualTo(Maybe.nothing());
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
}
