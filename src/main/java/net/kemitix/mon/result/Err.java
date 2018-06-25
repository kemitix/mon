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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An Error Result.
 *
 * @param <T> the type of the value in the Result if it has been a success
 */
@RequiredArgsConstructor
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
        return Result.error(error);
    }

    @Override
    public <R> Result<R> map(final Function<T, R> f) {
        return Result.error(error);
    }

    @Override
    public void match(final Consumer<T> onSuccess, final Consumer<Throwable> onError) {
        onError.accept(error);
    }

    @Override
    public Result<Maybe<T>> maybe(final Predicate<T> predicate) {
        return Result.error(error);
    }

    @Override
    public T orElseThrow() throws Throwable {
        throw error;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Err && Objects.equals(error, ((Err) other).error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }

    @Override
    public String toString() {
        return String.format("Result.Error{error=%s}", error);
    }
}
