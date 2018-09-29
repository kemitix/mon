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

package net.kemitix.mon.maybe;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A Maybe where no value is present.
 *
 * @param <T> the type of the missing content
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("methodcount")
final class Nothing<T> implements Maybe<T> {

    static final Maybe<?> INSTANCE = new Nothing<>();

    @Override
    public boolean isJust() {
        return false;
    }

    @Override
    public boolean isNothing() {
        return true;
    }

    @Override
    public <R> Maybe<R> flatMap(final Function<T, Maybe<R>> f) {
        return Maybe.nothing();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Maybe<R> map(final Function<T, R> f) {
        return (Maybe<R>) INSTANCE;
    }

    @Override
    public T orElseGet(final Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public T orElse(final T otherValue) {
        return otherValue;
    }

    @Override
    public Maybe<T> filter(final Predicate<T> predicate) {
        return this;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> e) throws X {
        throw e.get();
    }

    @Override
    public Maybe<T> peek(final Consumer<T> consumer) {
        return this;
    }

    @Override
    public void ifNothing(final Runnable runnable) {
        runnable.run();
    }

    @Override
    public void match(final Consumer<T> justMatcher, final Runnable nothingMatcher) {
        nothingMatcher.run();
    }

    @Override
    public Maybe<T> or(final Supplier<Maybe<T>> alternative) {
        return alternative.get();
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }
}
