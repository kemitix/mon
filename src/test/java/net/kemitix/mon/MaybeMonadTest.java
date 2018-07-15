package net.kemitix.mon;

import net.kemitix.mon.maybe.Maybe;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.function.Function;

public class MaybeMonadTest implements WithAssertions {

    private final int v = 1;
    private final Function<Integer, Maybe<Integer>> f = i -> m(i * 2);
    private final Function<Integer, Maybe<Integer>> g = i -> m(i + 6);

    private static Maybe<Integer> m(int value) {
        return Maybe.maybe(value);
    }

    @Test
    public void leftIdentity() {
        assertThat(
                m(v).flatMap(f)
        ).isEqualTo(
                f.apply(v)
        );
    }

    @Test
    public void rightIdentity() {
        assertThat(
                m(v).flatMap(x -> m(x))
        ).isEqualTo(
                m(v)
        );
    }

    @Test
    public void associativity() {
        assertThat(
                m(v).flatMap(f).flatMap(g)
        ).isEqualTo(
                m(v).flatMap(x -> f.apply(x).flatMap(g))
        );
    }

}
