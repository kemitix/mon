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

package net.kemitix.mon;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An almost-monad type-alias.
 *
 * @param <T> the type of the alias
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Mon<T> implements Functor<T> {

    /**
     * The value.
     */
    private final T value;

    /**
     * Create a factory for creating validated instances.
     *
     * <p>The value can never be null.</p>
     *
     * @param validator the validator function
     * @param onValid   the function to create when valid
     * @param onInvalid the function to create when invalid
     * @param <T>       the type of the value
     * @param <R>       the type of the factory output
     *
     * @return a function to apply to values to alias
     */
    public static <T, R> Function<T, R> factory(
            @NonNull final Predicate<T> validator,
            @NonNull final Function<Mon<T>, R> onValid,
            @NonNull final Supplier<R> onInvalid
                                               ) {
        return v -> {
            if (v != null && validator.test(v)) {
                return onValid.apply(Mon.of(v));
            }
            return onInvalid.get();
        };
    }

    /**
     * Create a new Mon for the value.
     *
     * @param v   the value
     * @param <T> the type of the value
     *
     * @return a Mon containing the value
     */
    public static <T> Mon<T> of(@NonNull final T v) {
        return new Mon<>(v);
    }

    @Override
    public final <R> Mon<R> map(final Function<T, R> f) {
        return Mon.of(f.apply(value));
    }

    /**
     * Returns a Mon consisting of the results of replacing the content of this Mon with the contents of a mapped Mon
     * produced by applying the provided mapping function to the content of the Mon.
     *
     * @param f   the mapping function the produces a Mon
     * @param <R> the type of the result of the mapping function
     *
     * @return a Mon containing the result of the function
     */
    public final <R> Mon<R> flatMap(final Function<T, Mon<R>> f) {
        return f.apply(value);
    }

    /**
     * The hashcode.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * equals.
     *
     * @param o the object to compare to
     *
     * @return true if they are the same
     */
    @Override
    @SuppressWarnings("npathcomplexity")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Mon<?> mon = (Mon<?>) o;

        return value.equals(mon.value);
    }
}
