package net.kemitix.mon;

import net.kemitix.mon.reader.Reader;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    private interface Environment {

        Integer intValue();

    }
}
