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

import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A value that may or may not be present.
 *
 * @param <T> the type of the content of the Just
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface Maybe<T> extends Functor<T, Maybe<?>> {

    /**
     * Create a Maybe for the value that is present.
     *
     * @param value the value, not null
     * @param <T>   the type of the value
     *
     * @return a Maybe of the value
     */
    static <T> Maybe<T> just(@NonNull final T value) {
        return new Just<>(value);
    }

    /**
     * Create a Maybe for a lack of a value.
     *
     * @param <T> the type of the missing value
     *
     * @return an empty Maybe
     */
    @SuppressWarnings("unchecked")
    static <T> Maybe<T> nothing() {
        return (Maybe<T>) Nothing.INSTANCE;
    }

    /**
     * Create a Maybe for the value that may or may not be present.
     *
     * @param value the value, may be null
     * @param <T>   the type of the value
     *
     * @return a Maybe, either a Just, or Nothing if value is null
     */
    static <T> Maybe<T> maybe(final T value) {
        if (value == null) {
            return nothing();
        }
        return just(value);
    }

    /**
     * Create a Maybe from an {@link Optional}.
     *
     * @param optional the Optional
     * @param <T>      the type of the Optional
     *
     * @return a Maybe
     */
    static <T> Maybe<T> fromOptional(final Optional<T> optional) {
        return optional.map(Maybe::maybe)
                       .orElse(nothing());
    }

    /**
     * Provide a value to use when Maybe is Nothing.
     *
     * @param supplier supplier for an alternate value
     *
     * @return a Maybe
     */
    T orElseGet(Supplier<T> supplier);

    /**
     * A value to use when Maybe is Nothing.
     *
     * @param otherValue an alternate value
     *
     * @return a Maybe
     */
    T orElse(T otherValue);

    /**
     * Filter a Maybe by the predicate, replacing with Nothing when it fails.
     *
     * @param predicate the test
     *
     * @return the Maybe, or Nothing if the test returns false
     */
    Maybe<T> filter(Predicate<T> predicate);

    /**
     * Convert the Maybe to an {@link Optional}.
     *
     * @return an Optional containing a value for a Just, or empty for a Nothing
     */
    Optional<T> toOptional();

    /**
     * Provide the value within the Maybe, if it exists, to the Supplier, and returns the Maybe.
     *
     * @param consumer the Consumer to the value if present
     *
     * @return the Maybe
     */
    Maybe<T> peek(Consumer<T> consumer);

    /**
     * Throw the exception if the Maybe is a Nothing.
     *
     * @param e the exception to throw
     *
     * @throws Exception if the Maybe is a Nothing
     */
    @SuppressWarnings("illegalthrows")
    void orElseThrow(Supplier<Exception> e) throws Exception;

}