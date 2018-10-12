package net.kemitix.mon;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

class OptionalMonadTest implements WithAssertions {

    private final int v = 1;
    private final Function<Integer, Optional<Integer>> f = i -> o(i * 2);
    private final Function<Integer, Optional<Integer>> g = i -> o(i + 6);

    private static Optional<Integer> o(int value) {
        return Optional.ofNullable(value);
    }

    @Test
    void leftIdentity() {
        assertThat(
                o(v).flatMap(f)
        ).isEqualTo(
                f.apply(v)
        );
    }

    @Test
    void rightIdentity() {
        assertThat(
                o(v).flatMap(x -> o(x))
        ).isEqualTo(
                o(v)
        );
    }

    @Test
    void associativity() {
        assertThat(
                o(v).flatMap(f).flatMap(g)
        ).isEqualTo(
                o(v).flatMap(x -> f.apply(x).flatMap(g))
        );
    }

}
