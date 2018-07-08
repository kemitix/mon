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

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A Successful Result.
 *
 * @param <T> the type of the value in the Result
 */
@RequiredArgsConstructor
@SuppressWarnings("methodcount")
class Success<T> implements Result<T> {

    private final T value;

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isOkay() {
        return true;
    }

    @Override
    public <R> Result<R> flatMap(final Function<T, Result<R>> f) {
        return f.apply(value);
    }

    @Override
    public <R> Result<R> map(final Function<T, R> f) {
        return Result.ok(f.apply(value));
    }

    @Override
    public void match(final Consumer<T> onSuccess, final Consumer<Throwable> onError) {
        onSuccess.accept(value);
    }

    @Override
    public Result<Maybe<T>> maybe(final Predicate<T> predicate) {
        if (predicate.test(value)) {
            return Result.ok(Maybe.just(value));
        }
        return Result.ok(Maybe.nothing());
    }

    @Override
    public T orElseThrow() {
        return value;
    }

    @Override
    public Result<T> peek(final Consumer<T> consumer) {
        consumer.accept(value);
        return this;
    }

    @Override
    public Result<T> recover(final Function<Throwable, Result<T>> f) {
        return this;
    }

    @Override
    public void onError(final Consumer<Throwable> errorConsumer) {
        // do nothing - this is not an error
    }

    @Override
    public <R> Result<R> andThen(final Function<T, Callable<R>> f) {
        return Result.of(f.apply(value));
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Success && Objects.equals(value, ((Success) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("Result.Success{value=%s}", value);
    }
}
