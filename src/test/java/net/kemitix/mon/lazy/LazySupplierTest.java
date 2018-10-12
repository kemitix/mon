package net.kemitix.mon.lazy;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

class LazySupplierTest implements WithAssertions {

    @Test
    void whenCreateLazyThenSupplierIsNotCalled() {
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
    void whenCreateLazyThenIsEvaluatedIsFalse() {
        //given
        final Supplier<UUID> supplier = UUID::randomUUID;
        //when
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //then
        assertThat(lazy.isEvaluated()).isFalse();
    }

    @Test
    void whenValueThenSupplierIsCalled() {
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
    void whenValueThenValueIsSameAsSupplier() {
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
    void whenValueThenIsEvaluatedIsTrue() {
        //given
        final Supplier<UUID> supplier = () -> UUID.randomUUID();
        final Lazy<UUID> lazy = Lazy.of(supplier);
        //when
        lazy.value();
        //then
        assertThat(lazy.isEvaluated()).isTrue();
    }

    @Test
    void whenValueCalledTwiceThenSupplierIsNotCalledAgain() {
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
    void whenMapLazyThenSupplierNotCalled() {
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
    void whenMapLazyValueThenSupplierIsCalled() {
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
    void whenMapLazyValueThenValueIsCorrect() {
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

    @Test
    void whenLazyValueCalledOnTwoThreadsThenSupplierIsOnlyCalledOnce() throws ExecutionException, InterruptedException {
        //given
        final AtomicInteger supplierCalledCounter = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(1);
        final UUID uuid = UUID.randomUUID();
        final Supplier<UUID> supplier = () -> {
            supplierCalledCounter.incrementAndGet();
            for (int i = 0; i < 10000; i++) {
                // hum
            };
            return uuid;
        };
        final Lazy<UUID> lazy = Lazy.of(supplier);
        final Callable<UUID> callable = () -> {
            latch.await();
            return lazy.value();
        };
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        //when
        final Future<UUID> call1 = executorService.submit(callable);
        final Future<UUID> call2 = executorService.submit(callable);
        latch.countDown();
        //then
        assertThat(call1.get()).isEqualTo(uuid);
        assertThat(call2.get()).isEqualTo(uuid);
        assertThat(supplierCalledCounter).hasValue(1);
    }

}
