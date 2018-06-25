package net.kemitix.mon;

import net.kemitix.mon.result.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.function.Function;

public class ResultMonadTest implements WithAssertions {

    @Test
    public void leftIdentity() {
        //given
        final int value = 1;
        final Result<Integer> result = Result.ok(value);
        final Function<Integer, Result<Integer>> f = i -> Result.ok(i * 2);
        //then
        assertThat(result.flatMap(f)).isEqualTo(f.apply(value));
    }

    @Test
    public void rightIdentity() {
        //given
        final Result<Integer> result = Result.ok(1);
        //then
        assertThat(result.flatMap(Result::ok)).isEqualTo(result);
    }

    @Test
    public void associativity() {
        //given
        final Result<Integer> result = Result.ok(1);
        final Function<Integer, Result<Integer>> f = i -> Result.ok(i * 2);
        final Function<Integer, Result<Integer>> g = i -> Result.ok(i + 6);
        //then
        assertThat(result.flatMap(f).flatMap(g)).isEqualTo(result.flatMap(x -> f.apply(x).flatMap(g)));
    }

}
