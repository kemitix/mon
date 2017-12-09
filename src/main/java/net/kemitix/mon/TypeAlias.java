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
 * <p>{@link #toString()}, {@link #equals(Object)} and {@link #hashCode()} are all transparent, returning the value
 * for the type being aliased.</p>
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

    /**
     * Constructor.
     *
     * @param value the value
     */
    protected TypeAlias(final T value) {
        this.value = value;
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
            final TypeAlias other = (TypeAlias) o;
            final Object otherValue = other.getValue();
            final Class<?> otherValueClass = otherValue.getClass();
            return otherValueClass.equals(getValue().getClass())
                   && otherValue.equals(getValue());
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
     */
    public T getValue() {
        return value;
    }
}
