package net.kemitix.mon;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeAliasTest {

    @Test
    public void shouldCreateATypeAliasAndGetTheValue() throws Exception {
        //given
        final String value = "value";
        //when
        final TypeAlias<String> typeAlias = new TypeAlias<String>(value) {
        };
        //then
        assertThat(typeAlias.getValue()).isEqualTo(value);
    }

    @Test
    public void shouldCreateAnAliasedTypeAndGetTheValue() throws Exception {
        //given
        final String value = "value";
        //when
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.getValue()).isEqualTo(value);
    }

    @Test
    public void shouldBeEqualWhenValuesAreTheSame() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias1 = AnAlias.of(value);
        final AnAlias anAlias2 = AnAlias.of(value);
        //then
        assertThat(anAlias1).isEqualTo(anAlias2);
    }

    @Test
    public void shouldBeEqualToUnAliasedValue() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias).isEqualTo(value);
    }

    @Test
    public void shouldHaveHashCodeOfValue() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        //then
        assertThat(anAlias.hashCode()).isEqualTo(value.hashCode());
    }

    @Test
    public void shouldHaveSameToStringAsAliasedType() throws Exception {
        //given
        final String value = "value";
        final AnAlias anAlias = AnAlias.of(value);
        assertThat(anAlias.toString()).isEqualTo(value);
    }

    private static class AnAlias extends TypeAlias<String> {

        /**
         * Constructor.
         *
         * @param value the value
         */
        protected AnAlias(final String value) {
            super(value);
        }

        protected static AnAlias of(final String value) {
            return new AnAlias(value);
        }
    }
}
