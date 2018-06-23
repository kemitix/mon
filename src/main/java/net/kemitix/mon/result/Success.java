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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Successful Result.
 *
 * @param <T> the type of the value in the Result
 */
@RequiredArgsConstructor
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
}
