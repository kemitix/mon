/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Paul Campbell
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

package net.kemitix.mon.combinator;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * After pattern combinator.
 *
 * <p>Original from http://boundsofjava.com/newsletter/003-introducing-combinators-part1</p>
 *
 * @param <T> the argument type
 * @param <R> the result type
 *
 * @author Federico Peralta Schaffner (fps@boundsofjava.com)
 */
@FunctionalInterface
public interface After<T, R> extends
        Function<Function<T, R>,
                Function<
                        BiConsumer<T, R>,
                        Function<T, R>>> {

    /**
     * Decorates a function with a Consumer that will be supplier with the argument before applying it to the function.
     *
     * @param function the function to apply the argument to and return the result value of
     * @param after    the bi-consumer that will receive the argument and the result of the function
     * @param <T>      the argument type
     * @param <R>      the result type
     *
     * @return a partially applied Function that will take an argument and return the result of applying it to the
     * function parameter
     */
    static <T, R> Function<T, R> decorate(
            final Function<T, R> function,
            final BiConsumer<T, R> after
    ) {
        return After.<T, R>create().apply(function)
                .apply(after);
    }

    /**
     * Create an After curried function.
     *
     * @param <T> the argument type
     * @param <R> the result type
     *
     * @return a curried function that will pass the argument and the result of the function to the supplied bi-consumer
     */
    static <T, R> After<T, R> create() {
        return function -> after -> argument -> {
            final R result = function.apply(argument);
            after.accept(argument, result);
            return result;
        };
    }
}
