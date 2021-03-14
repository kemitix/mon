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

package net.kemitix.mon.result;

/**
 * An Unchecked wrapper for unexpected exceptions thrown within a {@link Result}.
 *
 * <p>Used by {@link Result#orElseThrow(Class)} when the {@link Result} is an error of a different type to the
 * parameter.</p>
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public final class UnexpectedErrorResultException extends RuntimeException {

    private static final long serialVersionUID = 959355287011172256L;

    private UnexpectedErrorResultException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new object.
     *
     * @param cause the cause
     * @return a {@link UnexpectedErrorResultException} containing the cause
     */
    static UnexpectedErrorResultException with(final Throwable cause) {
        return new UnexpectedErrorResultException(cause);
    }
}
