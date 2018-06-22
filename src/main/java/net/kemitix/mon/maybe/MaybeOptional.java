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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Enables emulation parts of and conversion to the Java Optional class for Maybe.
 *
 * @param <T> the type of the content of the Just
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
interface MaybeOptional<T> {

    /**
     * Provide a value to use when Maybe is Nothing.
     *
     * @param supplier supplier for an alternate value
     *
     * @return a Maybe
     */
    T orElseGet(Supplier<T> supplier);

    /**
     * A value to use when Maybe is Nothing.
     *
     * @param otherValue an alternate value
     *
     * @return a Maybe
     */
    T orElse(T otherValue);

    /**
     * Convert the Maybe to an {@link Optional}.
     *
     * @return an Optional containing a value for a Just, or empty for a Nothing
     */
    Optional<T> toOptional();

    /**
     * Throw the exception if the Maybe is a Nothing.
     *
     * @param e the exception to throw
     *
     * @throws Exception if the Maybe is a Nothing
     */
    @SuppressWarnings("illegalthrows")
    void orElseThrow(Supplier<Exception> e) throws Exception;

}
