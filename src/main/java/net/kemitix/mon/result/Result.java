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

package net.kemitix.mon.result;

import net.kemitix.mon.maybe.Maybe;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An Either type for holding a result or an error (exception).
 *
 * @param <T> the type of the result when a success
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface Result<T> {

    /**
     * Create a Result for a success.
     *
     * @param value the value
     * @param <T> the type of the value
     * @return a successful Result
     */
    static <T> Result<T> ok(final T value) {
        return new Success<>(value);
    }

    /**
     * Create a Result for an error.
     *
     * @param error the error (exception)
     * @param <T> the type had the result been a success
     * @return an error Result
     */
    static <T> Result<T> error(final Throwable error) {
        return new Err<>(error);
    }

    /**
     * Checks of the Result is an error.
     *
     * @return true if the Result is an error.
     */
    boolean isError();

    /**
     * Checks of the Result is a success.
     *
     * @return true if the Result is a success.
     */
    boolean isOkay();

    /**
     * Returns a new Result consisting of the result of applying the function to the contents of the Result.
     *
     * @param f   the mapping function the produces a Result
     * @param <R> the type of the value withing the Result of the mapping function
     *
     * @return a Result
     */
    <R> Result<R> flatMap(Function<T, Result<R>> f);

    /**
     * Applies the functions to the value of a successful result, while doing nothing with an error.
     *
     * @param f the mapping function to produce the new value
     * @param <R> the type of the result of the mapping function
     *
     * @return a Result
     */
    <R> Result<R> map(Function<T, R> f);

    /**
     * Matches the Result, either success or error, and supplies the appropriate Consumer with the value or error.
     *
     * @param onSuccess the Consumer to pass the value of a successful Result to
     * @param onError the Consumer to pass the error from an error Result to
     */
    void match(Consumer<T> onSuccess, Consumer<Throwable> onError);

    /**
     * Wraps the value within the Result in a Maybe, either a Just if the predicate is true, or Nothing.
     *
     * @param predicate the test to decide
     * @return a Result containing a Maybe that may or may not contain a value
     */
    Result<Maybe<T>> maybe(Predicate<T> predicate);
}
