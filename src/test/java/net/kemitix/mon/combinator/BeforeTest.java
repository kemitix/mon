package net.kemitix.mon.combinator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class BeforeTest {

    @Test
    public void canCreateBeforeCombinator() {
        //given
        final List<String> events = new ArrayList<>();
        final Function<Integer, Integer> squareDecorated =
                Before.decorate(
                        argument -> before(argument, events),
                        argument -> function(argument, events)
                               );
        //when
        final Integer result = squareDecorated.apply(2);
        //then
        assertThat(result).isEqualTo(4);
        assertThat(events).containsExactly("before 2", "function");
    }

    private static void before(
            final Integer argument,
            final List<String> events
                              ) {
        events.add("before " + argument);
    }

    private static Integer function(
            final Integer argument,
            final List<String> events
                                   ) {
        events.add("function");
        return argument * argument;
    }
}
