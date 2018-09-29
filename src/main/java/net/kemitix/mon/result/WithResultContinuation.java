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

/**
 * A Callable-like interface for performing an action with a Result that, if there are no errors is returned as-is, but
 * if there is an error then a new error Result is returned.
 *
 * @param <T> the type of the current Result value
 */
@FunctionalInterface
public interface WithResultContinuation<T> {

    /**
     * Method to make use of the Result value.
     *
     * @throws Exception to replace the current Result with an error
     */
    public abstract void run() throws Exception;

    @SuppressWarnings({"illegalcatch", "javadocmethod"})
    public default Result<T> call(final Result<T> currentResult) {
        try {
            run();
        } catch (Throwable e) {
            return Result.error(e);
        }
        return currentResult;
    }
}
