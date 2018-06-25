package net.kemitix.mon;

import net.kemitix.mon.maybe.Maybe;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.function.Function;

public class MaybeMonadTest implements WithAssertions {

    @Test
    public void leftIdentity() {
        //given
        final int value = 1;
        final Maybe<Integer> maybe = Maybe.maybe(value);
        final Function<Integer, Maybe<Integer>> f = i -> Maybe.maybe(i * 2);
        //then
        assertThat(maybe.flatMap(f)).isEqualTo(f.apply(value));
    }

    @Test
    public void rightIdentity() {
        //given
        final Maybe<Integer> maybe = Maybe.maybe(1);
        //then
        assertThat(maybe.flatMap(Maybe::maybe)).isEqualTo(maybe);
    }

    @Test
    public void associativity() {
        //given
        final Maybe<Integer> maybe = Maybe.maybe(1);
        final Function<Integer, Maybe<Integer>> f = i -> Maybe.maybe(i * 2);
        final Function<Integer, Maybe<Integer>> g = i -> Maybe.maybe(i + 6);
        //then
        assertThat(maybe.flatMap(f).flatMap(g)).isEqualTo(maybe.flatMap(x -> f.apply(x).flatMap(g)));
    }

}
