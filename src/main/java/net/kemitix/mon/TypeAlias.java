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

import java.util.function.Function;

/**
 * Type Alias for other types.
 *
 * @param <T> the type of the alias
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("abstractclassname")
public abstract class TypeAlias<T> {

    /**
     * The value.
     */
    private final T value;

    private final Class<? super T> type;

    /**
     * Constructor.
     *
     * @param value the value
     * @param type  the type of the value
     */
    protected TypeAlias(
            final T value,
            final Class<? super T> type
                       ) {
        this.value = value;
        this.type = type;
    }

    /**
     * Map the TypeAlias into another value.
     *
     * @param f   the function to create the new value
     * @param <R> the type of the new value
     *
     * @return a TypeAlias
     */
    public final <R> R map(final Function<T, R> f) {
        return f.apply(getValue());
    }

    @Override
    public final int hashCode() {
        return getValue().hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean equals(final Object o) {
        if (o instanceof TypeAlias) {
            if (((TypeAlias) o).type.equals(type)) {
                return ((TypeAlias<T>) o).map(getValue()::equals);
            } else {
                return false;
            }
        }
        return map(o::equals);
    }

    @Override
    public final String toString() {
        return getValue().toString();
    }

    /**
     * Get the value of the type alias.
     *
     * @return the value
     *
     * @deprecated try using {@link #map(Function)}
     */
    @Deprecated
    public T getValue() {
        return value;
    }
}
