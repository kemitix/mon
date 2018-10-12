package net.kemitix.mon;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class TypeAliasMonadTest implements WithAssertions {

    private final int v = 1;
    private final Function<Integer, AnAlias<Integer>> f = i -> a(i * 2);
    private final Function<Integer, AnAlias<Integer>> g = i -> a(i + 6);

    private static AnAlias<Integer> a(Integer v) {
        return AnAlias.of(v);
    }

    @Test
    void leftIdentity() {
        assertThat(
                a(v).flatMap(f)
        ).isEqualTo(
                f.apply(v)
        );
    }

    @Test
    void rightIdentity() {
        final AnAlias<Integer> integerAnAlias = a(v).flatMap(x -> a(x));
        assertThat(
                integerAnAlias
        ).isEqualTo(
                a(v)
        );
    }

    @Test
    void associativity() {
        assertThat(
                a(v).flatMap(f).flatMap(g)
        ).isEqualTo(
                a(v).flatMap(x -> f.apply(x).flatMap(g))
        );
    }

    static class AnAlias<T> extends TypeAlias<T> {
        private AnAlias(T value) {
            super(value);
        }

        static <T> AnAlias<T> of(T value) {
            return new AnAlias<>(value);
        }
    }
}
