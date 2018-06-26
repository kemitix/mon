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

package net.kemitix.mon.maybe;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A Maybe where a value is present.
 *
 * @param <T> the type of the content
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("methodcount")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
final class Just<T> implements Maybe<T> {

    private final T value;

    @Override
    public <R> Maybe<R> flatMap(final Function<T, Maybe<R>> f) {
        return f.apply(value);
    }

    @Override
    public <R> Maybe<R> map(final Function<T, R> f) {
        return new Just<>(f.apply(value));
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Just && Objects.equals(value, ((Just) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public T orElseGet(final Supplier<T> supplier) {
        return value;
    }

    @Override
    public T orElse(final T otherValue) {
        return value;
    }

    @Override
    public Maybe<T> filter(final Predicate<T> predicate) {
        if (predicate.test(value)) {
            return this;
        }
        return Maybe.nothing();
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.of(value);
    }

    @Override
    public Maybe<T> peek(final Consumer<T> consumer) {
        consumer.accept(value);
        return this;
    }

    @Override
    public void orElseThrow(final Supplier<Exception> e) {
        // do not throw
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(value);
    }
}
