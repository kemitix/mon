package net.kemitix.mon;

import static net.kemitix.mon.BeanBuilder.define;
import static org.assertj.core.api.Assertions.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BeanBuilderTest {

    @Test
    public void canCreateAndSetupObject() {
        //given
        final Supplier<DataObject> templateSupplier = () -> new DataObject("name");
        final Consumer<DataObject> propertySetter = data -> data.setValue("value");

        //when
        final DataObject result = define(templateSupplier)
                .with(propertySetter);

        //then
        assertThat(result)
                .returns("name", DataObject::getName)
                .returns("value", DataObject::getValue);
    }

    @Getter
    @RequiredArgsConstructor
    private class DataObject {

        private final String name;

        @Setter
        private String value;

    }
}
