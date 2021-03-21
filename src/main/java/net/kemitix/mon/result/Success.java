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

package net.kemitix.mon.result;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A Successful Result.
 *
 * @param <T> the type of the value in the Result
 */
@RequiredArgsConstructor
@SuppressWarnings({"methodcount", "PMD.CyclomaticComplexity"})
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
    @SuppressWarnings({"illegalcatch", "PMD.AvoidCatchingThrowable"})
    public <R> Result<R> map(final Function<T, R> f) {
        try {
            return success(f.apply(value));
        } catch (Throwable e) {
            return err(e);
        }
    }

    @Override
    public void match(final Consumer<T> onSuccess, final Consumer<Throwable> onError) {
        onSuccess.accept(value);
    }

    @Override
    public Result<Maybe<T>> maybe(final Predicate<T> predicate) {
        if (predicate.test(value)) {
            return success(Maybe.just(value));
        }
        return success(Maybe.nothing());
    }

    @Override
    public T orElseThrow() {
        return value;
    }

    @Override
    public <E extends Exception> T orElseThrow(final Class<E> type) throws E {
        return value;
    }

    @Override
    public T orElseThrowUnchecked() {
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
    public void onSuccess(final Consumer<T> successConsumer) {
        successConsumer.accept(value);
    }

    @Override
    public void onError(final Consumer<Throwable> errorConsumer) {
        // do nothing - this is not an error
    }

    @Override
    public <E extends Throwable> Result<T> onError(
            final Class<E> errorClass,
            final Consumer<E> consumer
    ) {
        return this;
    }

    @Override
    public <R> Result<R> andThen(final Function<T, Callable<R>> f) {
        return result(f.apply(value));
    }

    @Override
    public Result<T> thenWith(final Function<T, WithResultContinuation<T>> f) {
        return f.apply(value).call(this);
    }

    @Override
    public Result<T> reduce(final Result<T> identity, final BinaryOperator<T> operator) {
        return flatMap(a -> identity.flatMap(b -> result(() -> operator.apply(a, b))));
    }

    @Override
    public <R> Result<R> flatMap(final Function<T, Result<R>> f) {
        return f.apply(value);
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
