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

package net.kemitix.mon.combinator;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Around pattern combinator.
 *
 * <p>Original from http://boundsofjava.com/newsletter/003-introducing-combinators-part1</p>
 *
 * @param <T> the argument type
 * @param <R> the result type
 *
 * @author Federico Peralta Schaffner (fps@boundsofjava.com)
 */
@FunctionalInterface
public interface Around<T, R> extends
        Function<
                Function<T, R>,
                Function<
                        BiConsumer<Around.Executable<R>, T>,
                        Function<T, R>>> {

    /**
     * Decorates a function with an BiConsumer that will be supplier with an executable to perform the function, and the
     * argument that will be applied when executed.
     *
     * @param function the function to apply the argument to and return the result value of
     * @param around   the bi-consumer that will supplied with the executable and the argument
     * @param <T>      the argument type
     * @param <R>      the result type
     *
     * @return a partially applied Function that will take an argument, and the result of applying it to function
     */
    public static <T, R> Function<T, R> decorate(
            final Function<T, R> function,
            final BiConsumer<Executable<R>, T> around
                                         ) {
        return Around.<T, R>create().apply(function)
                .apply(around);
    }

    /**
     * Create an Around curried function.
     *
     * @param <T> the argument type
     * @param <R> the result type
     *
     * @return a curried function that will execute the around function, passing an executable and the invocations
     * argument. The around function must {@code execute()} the executable and may capture the result.
     */
    public static <T, R> Around<T, R> create() {
        return function -> around -> argument -> {
            final AtomicReference<R> result = new AtomicReference<>();
            final Executable<R> callback = () -> {
                result.set(function.apply(argument));
                return result.get();
            };
            around.accept(callback, argument);
            return result.get();
        };
    }

    /**
     * The executable that will be supplied to the around function to trigger the surrounded function.
     *
     * @param <R> the return type of the function
     */
    @FunctionalInterface
    public static interface Executable<R> {

        /**
         * Executes the function.
         *
         * @return the result of applying the function
         */
        public abstract R execute();
    }
}
