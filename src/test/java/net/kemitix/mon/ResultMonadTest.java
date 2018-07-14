package net.kemitix.mon;

import net.kemitix.mon.result.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.function.Function;

public class ResultMonadTest implements WithAssertions {

    private final int v = 1;
    private final Function<Integer, Result<Integer>> f = i -> r(i * 2);
    private final Function<Integer, Result<Integer>> g = i -> r(i + 6);

    private static Result<Integer> r(int v) {
        return Result.ok(v);
    }

    @Test
    public void leftIdentity() {
        assertThat(
                r(v).flatMap(f)
        ).isEqualTo(
                f.apply(v)
        );
    }

    @Test
    public void rightIdentity() {
        assertThat(
                r(v).flatMap(x -> r(x))
        ).isEqualTo(
                r(v)
        );
    }

    @Test
    public void associativity() {
        assertThat(
                r(v).flatMap(f).flatMap(g)
        ).isEqualTo(
                r(v).flatMap(x -> f.apply(x).flatMap(g))
        );
    }

}
