package net.kemitix.mon.combinator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class AfterTest {

    @Test
    void canCreateAfterCombinator() {
        //given
        final List<String> events = new ArrayList<>();
        final Function<Integer, Integer> squareDecorated =
                After.decorate(
                        argument -> function(argument, events),
                        (argument, result) -> after(argument, result, events)
                              );
        //when
        final Integer result = squareDecorated.apply(2);
        //then
        assertThat(result).isEqualTo(4);
        assertThat(events).containsExactly("function", "after 2 -> 4");
    }

    private static void after(
            final Integer argument,
            final Integer result,
            final List<String> events
                             ) {
        events.add(String.format("after %d -> %d", argument, result));
    }

    private static Integer function(
            final Integer argument,
            final List<String> events
                                   ) {
        events.add("function");
        return argument * argument;
    }
}
