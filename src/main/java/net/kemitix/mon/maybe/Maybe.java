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

package net.kemitix.mon.maybe;

import lombok.NonNull;
import net.kemitix.mon.Functor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A value that may or may not be present.
 *
 * @param <T> the type of the content of the Just
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("methodcount")
public interface Maybe<T> extends Functor<T, Maybe<?>> {

    /**
     * Create a Maybe for the value that is present.
     *
     * <p>The {@literal value} must not be {@literal null} or a {@literal NullPointerException} will be thrown.
     * If you can't prove that the value won't be {@literal null} you should use {@link #maybe(Object)} instead.</p>
     *
     * @param value the value, not null
     * @param <T>   the type of the value
     * @return a Maybe of the value
     */
    static <T> Maybe<T> just(@NonNull final T value) {
        return new Just<>(value);
    }

    /**
     * Create a Maybe for a lack of a value.
     *
     * @param <T> the type of the missing value
     * @return an empty Maybe
     */
    @SuppressWarnings("unchecked")
    static <T> Maybe<T> nothing() {
        return (Maybe<T>) Nothing.INSTANCE;
    }

    /**
     * Create a Maybe for the value that may or may not be present.
     *
     * <p>Where the value is {@literal null}, that is taken as not being present.</p>
     *
     * @param value the value, may be null
     * @param <T>   the type of the value
     * @return a Maybe, either a Just, or Nothing if value is null
     */
    static <T> Maybe<T> maybe(final T value) {
        if (value == null) {
            return nothing();
        }
        return just(value);
    }

    /**
     * Creates a Maybe from the first item in the stream, or nothing if the stream is empty.
     *
     * @param stream the Stream
     * @param <T> the type of the stream
     * @return a Maybe containing the first item in the stream
     */
    static <T> Maybe<T> findFirst(Stream<T> stream) {
        return stream.findFirst()
                .map(Maybe::just)
                .orElseGet(Maybe::nothing);
    }

    /**
     * Creates a Maybe from an Optional.
     *
     * @param optional the Optional
     * @param <T> the type of the value
     * @return a Just if the Optional contains a value, otherwise a Nothing
     */
    static <T> Maybe<T> fromOptional(Optional<T> optional) {
        return optional.map(Maybe::maybe)
                .orElseGet(Maybe::nothing);
    }

    /**
     * Checks if the Maybe is a Just.
     *
     * @return true if the Maybe is a Just
     */
    boolean isJust();

    /**
     * Checks if the Maybe is Nothing.
     *
     * @return true if the Maybe is Nothing
     */
    boolean isNothing();

    /**
     * Monad binder maps the Maybe into another Maybe using the binder method f.
     *
     * @param f   the mapper function
     * @param <R> the type of the value in the final maybe
     * @return a Maybe with the mapped value
     */
    <R> Maybe<R> flatMap(Function<T, Maybe<R>> f);

    @Override
    <R> Maybe<R> map(Function<T, R> f);

    /**
     * Provide a value to use when Maybe is Nothing.
     *
     * @param supplier supplier for an alternate value
     * @return a Maybe
     */
    T orElseGet(Supplier<T> supplier);

    /**
     * A value to use when Maybe is Nothing.
     *
     * @param otherValue an alternate value
     * @return the value of the Maybe if a Just, otherwise the otherValue
     */
    T orElse(T otherValue);

    /**
     * Convert the Maybe to an {@link Optional}.
     *
     * @return an Optional containing a value for a Just, or empty for a Nothing
     */
    Optional<T> toOptional();

    /**
     * Throw the exception if the Maybe is a Nothing.
     *
     * @param e   the exception to throw
     * @param <X> the type of the exception to throw
     * @return the value of the Maybe if a Just
     * @throws X if the Maybe is nothing
     */
    <X extends Throwable> T orElseThrow(Supplier<? extends X> e) throws X;

    /**
     * Converts the Maybe into either a single value stream or an empty stream.
     *
     * @return a Stream containing the value or nothing.
     */
    Stream<T> stream();

    /**
     * Filter a Maybe by the predicate, replacing with Nothing when it fails.
     *
     * @param predicate the test
     * @return the Maybe, or Nothing if the test returns false
     */
    Maybe<T> filter(Predicate<T> predicate);

    /**
     * Provide the value within the Maybe, if it exists, to the Consumer, and returns this Maybe.
     *
     * @param consumer the Consumer to the value if present
     * @return this Maybe
     */
    Maybe<T> peek(Consumer<T> consumer);

    /**
     * Run the runnable if the Maybe is a Nothing, otherwise do nothing.
     *
     * @param runnable the runnable to call if this is a Nothing
     */
    void ifNothing(Runnable runnable);

    /**
     * Matches the Maybe, either just or nothing, and performs either the Consumer, for Just, or Runnable for nothing.
     *
     * <p>Unlike {@link #matchValue(Function, Supplier)}, this method does not return a value.</p>
     *
     * @param justMatcher    the Consumer to pass the value of a Just to
     * @param nothingMatcher the Runnable to call if the Maybe is a Nothing
     */
    void match(Consumer<T> justMatcher, Runnable nothingMatcher);

    /**
     * Matches the Maybe, either just or nothing, and performs either the Function, for Just, or Supplier for nothing.
     *
     * <p>Unlike {@link #match(Consumer, Runnable)}, this method returns a value.</p>
     *
     * @param justMatcher    the Function to pass the value of a Just to
     * @param nothingMatcher the Supplier to call if the Maybe is a Nothing
     * @param <R> the type of the matched result
     *
     * @return the matched result, from either the justMatcher or the nothingMatcher
     */
    <R> R matchValue(Function<T, R> justMatcher, Supplier<R> nothingMatcher);

    /**
     * Maps the Maybe into another Maybe only when it is nothing.
     *
     * @param alternative the maybe to map the nothing into
     *
     * @return the original Maybe if not nothing, or the alternative
     */
    Maybe<T> or(Supplier<Maybe<T>> alternative);
}
