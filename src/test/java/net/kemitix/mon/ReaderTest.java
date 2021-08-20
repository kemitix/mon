package net.kemitix.mon;

import net.kemitix.mon.reader.Reader;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ReaderTest
        implements WithAssertions {

    @Test
    @DisplayName("read a value and return it")
    void readAndReturn() {
        //given
        Integer value = 123;
        Environment env = new Environment() {
            @Override
            public Integer intValue() {
                return value;
            }
        };
        Reader<Environment, Integer> program = (Environment e) -> {
            return e.intValue();
        };

        //when
        Integer result = program.run(env);

        //then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("map")
    void map() {
        //given
        AtomicInteger value = new AtomicInteger(123);
        Environment env = new Environment() {
            @Override
            public Integer intValue() {
                return value.incrementAndGet();
            }
        };
        Reader<Environment, String> program =
                ((Reader<Environment, Integer>) e -> e.intValue())
                        .map(i -> i * 2)
                        .map(i -> Integer.toString(i));
        //when
        String result = program.run(env);
        //then
        assertThat(result).isEqualTo("248");// (123 + 1) * 2
    }

    @Test
    @DisplayName("andThen")
    void andThen() {
        //given
        AtomicInteger value = new AtomicInteger(123);
        Environment env = new Environment() {
            @Override
            public Integer intValue() {
                return value.incrementAndGet();
            }
        };
        Reader<Environment, String> program =
                ((Reader<Environment, Integer>) e -> e.intValue())
                        .flatMap((e, i) -> e.intValue() + (i * 2))
                        .flatMap((e, i) -> String.format("%.2f",
                                i.floatValue() / e.intValue()));
        //when
        String result = program.run(env);
        //then
        assertThat(result).isEqualTo("2.96");// ((123 * 2) + (123 + 1)) / (123 + 2)
    }

    @Test
    @DisplayName("flatMap")
    void flatMap() {
        //given
        AtomicInteger value = new AtomicInteger(123);
        Environment env = new Environment() {
            @Override
            public Integer intValue() {
                return value.incrementAndGet();
            }
        };
        Function<Integer, Reader<Environment, Integer>> addNextValue =
                integer -> (Reader<Environment, Integer>) e -> e.intValue() + integer;
        Function<Integer, Reader<Environment, Float>> divideByNextValue =
                integer -> (Reader<Environment, Float>) e -> integer.floatValue() / e.intValue();
        Reader<Environment, String> program =
                ((Reader<Environment, Integer>) e -> e.intValue())
                        .flatMap(addNextValue)
                        .flatMap(divideByNextValue)
                        .map(f -> String.format("%.3f", f));
        //when
        String result = program.run(env);
        //then
        assertThat(result).isEqualTo("1.976");// (123 + 124) / 125
    }

    private interface Environment {

        Integer intValue();

    }
}
