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
                Before.decorate(v -> before(v, events), i -> function(i, events));
        //when
        final Integer result = squareDecorated.apply(2);
        //then
        assertThat(result).isEqualTo(4);
        assertThat(events).containsExactly("before", "function");
    }

    private static void before(
            final Integer v,
            final List<String> events
                              ) {
        events.add("before");
    }

    private static Integer function(
            final Integer i,
            final List<String> events
                                   ) {
        events.add("function");
        return i * i;
    }
}
