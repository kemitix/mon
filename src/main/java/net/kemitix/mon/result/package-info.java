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

/**
 * &lt;h1&gt;Result&lt;/h1&gt;
 *
 * Allows handling error conditions without the need to {@code catch}
 * exceptions.
 *
 * <p>When a {@link Result} is returned from a method, it will be in one of two
 * states: {@link Success} or {@link Err}. The {@code Success} state will
 * contain a value from the method. The {@code Err} state will contain a
 * {@link java.lang.Throwable} detailing the reason for the failure.</p>
 *
 * <p>Methods returning a {@code Result} should not throw any exceptions.</p>
 *
 * <p>{@code Result} is a Monad.</p>
 *
 * <h2>Static Constructors:</h2>
 * <ul>
 *     <li>{@link Result#ok()}</li>
 *     <li>{@link Result#ok(Object)}</li>
 *     <li>{@link Result#of(Callable)}</li>
 *     <li>{@link Result#ofVoid(VoidCallable)}</li>
 *     <li>{@link Result#error(Throwable)}</li>
 * </ul>
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */

package net.kemitix.mon.result;
