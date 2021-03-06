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

package net.kemitix.mon.experimental.either;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Either type holding a left value.
 *
 * @param <L> the type of the Either for left value
 * @param <R> the type of the Either for right value
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@RequiredArgsConstructor
class Left<L, R> implements Either<L, R> {

    private final L value;

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public void match(final Consumer<L> onLeft, final Consumer<R> onRight) {
        onLeft.accept(value);
    }

    @Override
    public <T> Either<T, R> mapLeft(final Function<L, T> f) {
        return new Left<>(f.apply(value));
    }

    @Override
    public <T> Either<L, T> mapRight(final Function<R, T> f) {
        return new Left<>(value);
    }

    @Override
    public <T> Either<T, R> flatMapLeft(final Function<L, Either<T, R>> f) {
        return f.apply(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Either<L, T> flatMapRight(final Function<R, Either<L, T>> f) {
        return (Either<L, T>) this;
    }

    @Override
    public Optional<L> getLeft() {
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<R> getRight() {
        return Optional.empty();
    }
}
