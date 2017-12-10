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
        assertIdentityContains(idInt, 3);
    }

    private <T> void assertIdentityContains(final Identity<T> identity, final T expected) {
        identity.map(id -> assertThat(id).isEqualTo(expected));
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
        assertIdentityContains(idBytes, "par".getBytes());
    }

}
