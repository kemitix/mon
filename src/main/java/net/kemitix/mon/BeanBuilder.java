package net.kemitix.mon;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class BeanBuilder<T> {

    private final Supplier<T> supplier;

    public static <T> BeanBuilder<T> define(final Supplier<T> supplier) {
        return new BeanBuilder<>(supplier);
    }

    public T with(final Consumer<T> consumer) {
        final T result = supplier.get();
        consumer.accept(result);
        return result;
    }
}
