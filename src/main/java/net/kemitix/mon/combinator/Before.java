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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Before pattern combinator.
 *
 * <p>Original from http://boundsofjava.com/newsletter/003-introducing-combinators-part1</p>
 *
 * @param <T> the argument type
 * @param <R> the result type
 *
 * @author Federico Peralta Schaffner (fps@boundsofjava.com)
 */
@FunctionalInterface
public interface Before<T, R> extends
        Function<
                Consumer<T>,
                Function<
                        Function<T, R>,
                        Function<T, R>>> {

    /**
     * Decorates a function with a Consumer that will be supplied with the argument before applying it to the function.
     *
     * @param before   the consumer that will receive the argument before the function
     * @param function the function to apply the argument to and return the result value of
     * @param <T>      the argument type
     * @param <R>      the result type
     *
     * @return a partially applied Function that will take an argument and return the result of applying it to the
     * function parameter
     */
    static <T, R> Function<T, R> decorate(
            final Consumer<T> before,
            final Function<T, R> function
    ) {
        return Before.<T, R>create().apply(before)
                .apply(function);
    }

    /**
     * Create a Before curried function.
     *
     * @param <T> the argument type
     * @param <R> the result type
     *
     * @return a curried function that will pass the argument to before applying the supplied function
     */
    static <T, R> Before<T, R> create() {
        return before -> function -> argument -> {
            before.accept(argument);
            return function.apply(argument);
        };
    }
}
