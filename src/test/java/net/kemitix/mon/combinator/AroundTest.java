package net.kemitix.mon.combinator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class AroundTest {

    @Test
    void canCreateAnAroundCombinator() {
        //given
        final List<String> events = new ArrayList<>();
        final Function<Integer, Integer> squareDecorated =
                Around.decorate(
                        argument -> function(argument, events),
                        (executable, argument) -> around(executable, argument, events)
                               );
        //when
        final Integer result = squareDecorated.apply(2);
        //then
        assertThat(result).isEqualTo(4);
        assertThat(events).containsExactly("around before 2", "function", "around after 4");
    }

    private void around(
            final Around.Executable<Integer> executable,
            final Integer argument,
            final List<String> events
                       ) {
        events.add("around before " + argument);
        final Integer result = executable.execute();
        events.add("around after " + result);
    }

    private static Integer function(
            final Integer argument,
            final List<String> events
                                   ) {
        events.add("function");
        return argument * argument;
    }
}
