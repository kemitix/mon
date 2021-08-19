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

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Error Result.
 *
 * @param <T> the type of the value in the Result if it has been a success
 */
@RequiredArgsConstructor
@SuppressWarnings({"methodcount", "PMD.TooManyMethods", "PMD.ExcessivePublicCount",
        "PMD.CyclomaticComplexity"})
class Err<T> implements Result<T> {

    private final Throwable error;

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isOkay() {
        return false;
    }

    @Override
    public <R> Result<R> flatMap(final Function<T, Result<R>> f) {
        return new Err<>(error);
    }

    @Override
    public ResultVoid flatMapV(final Function<T, ResultVoid> f) {
        return new ErrVoid(error);
    }

    @Override
    public <R> Result<R> map(final ThrowableFunction<T, R, ?> f) {
        return new Err<>(error);
    }

    @Override
    public Result<T> match(final Consumer<T> onSuccess, final Consumer<Throwable> onError) {
        onError.accept(error);
        return this;
    }

    @Override
    public T orElseThrow() throws CheckedErrorResultException {
        throw CheckedErrorResultException.with(error);
    }

    @Override
    @SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
    public <E extends Exception> T orElseThrow(final Class<E> type) throws E {
        if (type.isInstance(error)) {
            throw (E) error;
        }
        throw UnexpectedErrorResultException.with(error);
    }

    @Override
    public T orElseThrowUnchecked() {
        throw ErrorResultException.with(error);
    }

    @Override
    public Result<T> peek(final Consumer<T> consumer) {
        return this;
    }

    @Override
    public Result<T> recover(final Function<Throwable, Result<T>> f) {
        return f.apply(error);
    }

    @Override
    public void onSuccess(final Consumer<T> successConsumer) {
        // do nothing
    }

    @Override
    public void onError(final Consumer<Throwable> errorConsumer) {
        errorConsumer.accept(error);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Throwable> Result<T> onError(
            final Class<E> errorClass,
            final Consumer<E> consumer
    ) {
        if (error.getClass().isAssignableFrom(errorClass)) {
            consumer.accept((E) error);
        }
        return this;
    }

    @Override
    public <R> Result<R> andThen(final Function<T, Callable<R>> f) {
        return (Result<R>) this;
    }

    @Override
    public Result<T> thenWith(final Function<T, WithResultContinuation<T>> f) {
        return this;
    }

    @Override
    public ResultVoid thenWithV(final Function<T, WithResultContinuation<T>> f) {
        return toVoid();
    }

    @Override
    public Result<T> reduce(final Result<T> identify, final BinaryOperator<T> operator) {
        return this;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Err
                && Objects.equals(error, ((Err) other).error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }

    @Override
    public String toString() {
        return String.format("Result.Error{error=%s}", error);
    }

    @Override
    public ResultVoid toVoid() {
        return new ErrVoid(error);
    }
}
