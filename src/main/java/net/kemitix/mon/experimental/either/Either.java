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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Either type for holding a one of two possible values, a left and a right, that may be of different types.
 *
 * @param <L> the type of the Either for left value
 * @param <R> the type of the Either for right value
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("methodcount")
public interface Either<L, R> {

    /**
     * Create a new Either holding a left value.
     *
     * @param l   the left value
     * @param <L> the type of the left value
     * @param <R> the type of the right value
     * @return a Either holding the left value
     */
    static <L, R> Either<L, R> left(final L l) {
        return new Left<>(l);
    }

    /**
     * Create a new Either holding a right value.
     *
     * @param r   the right value
     * @param <L> the type of the left value
     * @param <R> the type of the right value
     * @return a Either holding the right value
     */
    static <L, R> Either<L, R> right(final R r) {
        return new Right<>(r);
    }

    /**
     * Checks if the Either holds a left value.
     *
     * @return true if this Either is a left
     */
    boolean isLeft();

    /**
     * Checks if the Either holds a right value.
     *
     * @return true if this Either is a right
     */
    boolean isRight();

    /**
     * Matches the Either, invoking the correct Consumer.
     *
     * @param onLeft  the Consumer to invoke when the Either is a left
     * @param onRight the Consumer to invoke when the Either is a right
     */
    void match(Consumer<L> onLeft, Consumer<R> onRight);

    /**
     * Map the function across the left value.
     *
     * @param f   the function to apply to any left value
     * @param <T> the type to change the left value to
     * @return a new Either
     */
    <T> Either<T, R> mapLeft(Function<L, T> f);

    /**
     * Map the function across the right value.
     *
     * @param f   the function to apply to any right value
     * @param <T> the type to change the right value to
     * @return a new Either
     */
    <T> Either<L, T> mapRight(Function<R, T> f);

    /**
     * FlatMap the function across the left value.
     *
     * @param f   the function to apply to any left value
     * @param <T> the type to change the left value to
     * @return a new Either if is a Left, else this
     */
    <T> Either<T, R> flatMapLeft(Function<L, Either<T, R>> f);

    /**
     * FlatMap the function across the right value.
     *
     * @param f   the function to apply to any right value
     * @param <T> the type to change the right value to
     * @return a new Either if is a Right, else this
     */
    <T> Either<L, T> flatMapRight(Function<R, Either<L, T>> f);

    /**
     * Returns an Optional containing the left value, if is a left, otherwise
     * returns an empty Optional.
     *
     * @return An Optional containing any left value
     */
    Optional<L> getLeft();

    /**
     * Returns an Optional containing the right value, if is a right, otherwise
     * returns an empty Optional.
     *
     * @return An Optional containing any right value
     */
    Optional<R> getRight();
}
