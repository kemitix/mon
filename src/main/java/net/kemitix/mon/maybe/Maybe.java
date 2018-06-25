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

import lombok.NonNull;
import net.kemitix.mon.Functor;

import java.util.function.Function;

/**
 * A value that may or may not be present.
 *
 * @param <T> the type of the content of the Just
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface Maybe<T> extends Functor<T, Maybe<?>>, MaybeStream<T>, MaybeOptional<T> {

    /**
     * Create a Maybe for the value that may or may not be present.
     *
     * @param value the value, may be null
     * @param <T>   the type of the value
     * @return a Maybe, either a Just, or Nothing if value is null
     */
    static <T> Maybe<T> maybe(final T value) {
        if (value == null) {
            return nothing();
        }
        return just(value);
    }

    /**
     * Create a Maybe for a lack of a value.
     *
     * @param <T> the type of the missing value
     * @return an empty Maybe
     */
    @SuppressWarnings("unchecked")
    static <T> Maybe<T> nothing() {
        return (Maybe<T>) Nothing.INSTANCE;
    }

    /**
     * Create a Maybe for the value that is present.
     *
     * @param value the value, not null
     * @param <T>   the type of the value
     * @return a Maybe of the value
     */
    static <T> Maybe<T> just(@NonNull final T value) {
        return new Just<>(value);
    }

    @Override
    <R> Maybe<R> map(Function<T, R> f);
}
