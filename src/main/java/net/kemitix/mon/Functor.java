/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Paul Campbell
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
 * The Functor is used for types that can be mapped over.
 *
 * <p>Implementations of Functor should satisfy the following laws:</p>
 *
 * <ul>
 *     <li>map id  ==  id</li>
 *     <li>map (f . g)  ==  map f . map g</li>
 * </ul>
 *
 * @param <T> the type of the Functor
 * @param <F> the type of the mapped Functor
 *
 * @author Tomasz Nurkiewicz (?@?.?)
 */
public interface Functor<T, F extends Functor<?, ?>> {

    /**
     * Applies the function to the value within the Functor, returning the result within a Functor.
     *
     * @param f   the function to apply
     * @param <R> the type of the content of the mapped functor
     *
     * @return a Functor containing the result of the function {@code f} applied to the value
     */
    public abstract <R> F map(Function<T, R> f);
}
