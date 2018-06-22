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

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Enables emulation parts of and conversion to the Java Stream class for Maybe.
 *
 * @param <T> the type of the content of the Just
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
interface MaybeStream<T> {

    /**
     * Converts the Maybe into either a single value stream or and empty stream.
     *
     * @return a Stream containing the value or nothing.
     */
    Stream<T> stream();

    /**
     * Filter a Maybe by the predicate, replacing with Nothing when it fails.
     *
     * @param predicate the test
     *
     * @return the Maybe, or Nothing if the test returns false
     */
    Maybe<T> filter(Predicate<T> predicate);

    /**
     * Provide the value within the Maybe, if it exists, to the Supplier, and returns the Maybe.
     *
     * @param consumer the Consumer to the value if present
     *
     * @return the Maybe
     */
    Maybe<T> peek(Consumer<T> consumer);

}
