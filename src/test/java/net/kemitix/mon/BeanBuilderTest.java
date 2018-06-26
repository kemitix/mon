package net.kemitix.mon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kemitix.mon.experimental.BeanBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BeanBuilderTest implements WithAssertions {

    @Test
    public void canCreateAndSetupObject() {
        //given
        final Supplier<DataObject> template = () -> new DataObject("name");
        final Consumer<DataObject> value = data -> data.setValue("value");
        final Consumer<DataObject> age = data -> data.setAge(42);
        //when
        final DataObject result = BeanBuilder
                .define(template)
                .with(value)
                .with(age)
                .build();
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
        private int age;

        @Setter
        private String value;

    }
}
