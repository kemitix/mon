/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Paul Campbell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.kemitix.mon.lazy;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of Lazy using a Supplier.
 *
 * @param <T> the type of the value
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
class LazySupplier<T> implements Lazy<T> {

    private final Supplier<T> supplier;
    private final AtomicBoolean evaluated = new AtomicBoolean(false);
    private final AtomicReference<T> value = new AtomicReference<>();

    /**
     * Creates a new Lazy wrapper for the Supplier.
     *
     * @param supplier the source of the value
     */
    LazySupplier(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean isEvaluated() {
        return evaluated.get();
    }

    @Override
    public T value() {
        if (evaluated.get()) {
            return value.get();
        }
        synchronized (value) {
            if (!evaluated.get()) {
                value.set(supplier.get());
                evaluated.set(true);
            }
        }
        return value.get();
    }

    @Override
    public <R> Lazy<R> map(final Function<T, R> f) {
        return Lazy.of(() -> f.apply(value()));
    }

}
