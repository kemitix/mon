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

import net.kemitix.mon.Functor;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lazy evaluation of 'expensive' expressions.
 *
 * @param <T> the type of the value
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface Lazy<T> extends Functor<T, Lazy<?>> {

    /**
     * Create a new Lazy value from the supplier.
     *
     * @param supplier the source of the value
     * @param <R> the type of the value
     * @return a Lazy wrapper of the Supplier
     */
    public static <R> Lazy<R> of(final Supplier<R> supplier) {
        return new LazySupplier<>(supplier);
    }

    /**
     * Checks if the value has been evaluated.
     *
     * @return true if the value has been evaluated.
     */
    public abstract boolean isEvaluated();

    /**
     * The value, evaluating it if necessary.
     *
     * <p>Does not evaluate the value more than once.</p>
     *
     * @return the evaluated value
     */
    public abstract T value();

    @Override
    public abstract <R> Lazy<R> map(Function<T, R> f);
}
