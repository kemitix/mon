package net.kemitix.mon;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

/**
 * Tests for {@link Identity}.
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public class IdentityTest implements WithAssertions {

    @Test
    public void canMapIdentityFromStringToInteger() {
        //given
        final Identity<String> idString = new Identity<>("abc");
        //when
        final Identity<Integer> idInt = idString.map(String::length);
        //then
        idInt.map(id -> assertThat(id).isEqualTo(3));
    }

    @Test
    public void canFluentlyComposeFunctions() {
        //given
        final Customer customer = new Customer(new Address("Park Place"));
        //when
        final Identity<byte[]> idBytes = new Identity<>(customer).map(Customer::getAddress)
                                                                 .map(Address::getStreet)
                                                                 .map((String s) -> s.substring(0, 3))
                                                                 .map(String::toLowerCase)
                                                                 .map(String::getBytes);
        //then
        idBytes.map(bytes -> assertThat(bytes).isEqualTo("par".getBytes()));
    }

}
