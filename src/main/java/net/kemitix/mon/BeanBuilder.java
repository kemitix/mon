/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Paul Campbell
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

package net.kemitix.mon;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Helper to create bean-style objects.
 *
 * @param <T> The type of the bean being built
 */
@RequiredArgsConstructor
public class BeanBuilder<T> {

    private final Supplier<T> supplier;

    /**
     * Create a BeanBuilder from the Supplier.
     *
     * @param constructor supplies a new instance of the bean
     * @param <T> the type of the bean being built
     * @return a BeanBuilder instance
     */
    public static <T> BeanBuilder<T> define(final Supplier<T> constructor) {
        return new BeanBuilder<>(constructor);
    }

    /**
     * Creates a new bean and passes it to the customiser.
     *
     * @param customiser customises the template bean
     * @return the final customised bean
     */
    public T with(final Consumer<T> customiser) {
        final T result = supplier.get();
        customiser.accept(result);
        return result;
    }
}
