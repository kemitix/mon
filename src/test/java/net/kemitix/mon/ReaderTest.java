package net.kemitix.mon;

import net.kemitix.mon.reader.Reader;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

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
                        .andThen((e, i) -> e.intValue() + (i * 2))
                        .andThen((e, i) -> String.format("%.2f",
                                i.floatValue() / e.intValue()));
        //when
        String result = program.run(env);
        //then
        assertThat(result).isEqualTo("2.96");// ((123 * 2) + (123 + 1)) / (123 + 2)
    }

    private interface Environment {

        Integer intValue();

    }
}
