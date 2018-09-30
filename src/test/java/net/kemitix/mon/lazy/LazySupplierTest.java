package net.kemitix.mon.lazy;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LazySupplierTest implements WithAssertions {

    @Test
    public void whenCreateLazyThenSupplierIsNotCalled() {
        //given
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        final Supplier<UUID> supplier = () -> {
            supplierCalled.set(true);
            return UUID.randomUUID();
        };
        //when
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //then
        assertThat(supplierCalled).isFalse();
    }

    @Test
    public void whenCreateLazyThenIsEvaluatedIsFalse() {
        //given
        final Supplier<UUID> supplier = UUID::randomUUID;
        //when
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //then
        assertThat(lazy.isEvaluated()).isFalse();
    }

    @Test
    public void whenValueThenSupplierIsCalled() {
        //given
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        final Supplier<UUID> supplier = () -> {
            supplierCalled.set(true);
            return UUID.randomUUID();
        };
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //when
        lazy.value();
        //then
        assertThat(supplierCalled).isTrue();
    }

    @Test
    public void whenValueThenValueIsSameAsSupplier() {
        //given
        final UUID uuid = UUID.randomUUID();
        final Supplier<UUID> supplier = () -> uuid;
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //when
        final UUID value = lazy.value();
        //then
        assertThat(value).isSameAs(uuid);
    }

    @Test
    public void whenValueThenIsEvaluatedIsTrue() {
        //given
        final Supplier<UUID> supplier = () -> UUID.randomUUID();
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //when
        lazy.value();
        //then
        assertThat(lazy.isEvaluated()).isTrue();
    }

    @Test
    public void whenValueCalledTwiceThenSupplierIsNotCalledAgain() {
        //given
        final AtomicInteger supplierCalledCounter = new AtomicInteger(0);
        final Supplier<UUID> supplier = () -> {
            supplierCalledCounter.incrementAndGet();
            return UUID.randomUUID();
        };
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //when
        lazy.value();
        lazy.value();
        //then
        assertThat(supplierCalledCounter).hasValue(1);
    }

    @Test
    public void whenMapLazyThenSupplierNotCalled() {
        //given
        final UUID uuid = UUID.randomUUID();
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        final Supplier<UUID> supplier = () -> {
            supplierCalled.set(true);
            return uuid;
        };
        final Lazy<UUID> uuidLazy = Lazy.of(supplier);
        //when
        uuidLazy.map(UUID::toString);
        //then
        assertThat(supplierCalled).isFalse();
    }

    @Test
    public void whenMapLazyValueThenSupplierIsCalled() {
        //given
        final UUID uuid = UUID.randomUUID();
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        final Supplier<UUID> supplier = () -> {
            supplierCalled.set(true);
            return uuid;
        };
        final Lazy<UUID> uuidLazy = Lazy.of(supplier);
        final Lazy<String> stringLazy = uuidLazy.map(UUID::toString);
        //when
        stringLazy.value();
        //then
        assertThat(supplierCalled).isTrue();
    }

    @Test
    public void whenMapLazyValueThenValueIsCorrect() {
        //given
        final UUID uuid = UUID.randomUUID();
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        final Supplier<UUID> supplier = () -> {
            supplierCalled.set(true);
            return uuid;
        };
        final Lazy<UUID> uuidLazy = Lazy.of(supplier);
        final Lazy<String> stringLazy = uuidLazy.map(UUID::toString);
        //when
        final String value = stringLazy.value();
        //then
        assertThat(value).isEqualTo(uuid.toString());
    }

}