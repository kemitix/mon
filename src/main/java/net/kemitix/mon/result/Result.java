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

import net.kemitix.mon.Functor;
import net.kemitix.mon.maybe.Maybe;

import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * An Either type for holding a result or an error (Throwable).
 *
 * @param <T> the type of the result when a success
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings("methodcount")
public interface Result<T> extends Functor<T, Result<?>> {

    /**
     * Creates a Result from the Maybe, where the Result will be an error if the Maybe is Nothing.
     *
     * @param maybe the Maybe the might contain the value of the Result
     * @param error the error that will be the Result if maybe is Nothing
     * @param <T>   the type of the Maybe and the Result
     * @return a Result containing the value of the Maybe when it is a Just, or the error when it is Nothing
     */
    public static <T> Result<T> fromMaybe(final Maybe<T> maybe, final Supplier<Throwable> error) {
        return maybe.map(Result::ok)
                .orElseGet(() -> Result.error(error.get()));
    }

    /**
     * Create a Result for an error.
     *
     * @param error the error (Throwable)
     * @param <T>   the type had the result been a success
     * @return an error Result
     */
    public static <T> Result<T> error(final Throwable error) {
        return new Err<>(error);
    }

    /**
     * Create a Result for a output of the Callable.
     *
     * @param callable the callable to produce the result
     * @param <T>      the type of the value
     * @return a Result
     */
    @SuppressWarnings("illegalcatch")
    public static <T> Result<T> of(final Callable<T> callable) {
        try {
            return Result.ok(callable.call());
        } catch (final Exception e) {
            return Result.error(e);
        }
    }

    /**
     * Create a Result for a success.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return a successful Result
     */
    public static <T> Result<T> ok(final T value) {
        return new Success<>(value);
    }

    /**
     * Creates a {@link Maybe} from the Result, where the Result is a success, then the Maybe will contain the value.
     *
     * <p>However, if the Result is an error then the Maybe will be nothing.</p>
     *
     * @param result the Result the might contain the value of the Result
     * @param <T>    the type of the Maybe and the Result
     * @return a Result containing the value of the Maybe when it is a Just, or the error when it is Nothing
     */
    @SuppressWarnings("illegalcatch")
    public static <T> Maybe<T> toMaybe(final Result<T> result) {
        try {
            return Maybe.just(result.orElseThrow());
        } catch (final Throwable throwable) {
            return Maybe.nothing();
        }
    }

    /**
     * Extracts the successful value from the result, or throws the error Throwable.
     *
     * @return the value if a success
     * @throws Throwable the result is an error
     */
    @SuppressWarnings("illegalthrows")
    public abstract T orElseThrow() throws Throwable;

    /**
     * Swaps the inner Result of a Maybe, so that a Result is on the outside.
     *
     * @param maybeResult the Maybe the contains a Result
     * @param <T>         the type of the value that may be in the Result
     * @return a Result containing a Maybe, the value in the Maybe was the value in a successful Result within the
     * original Maybe. If the original Maybe is Nothing, the Result will contain Nothing. If the original Result was an
     * error, then the Result will also be an error.
     */
    public static <T> Result<Maybe<T>> swap(final Maybe<Result<T>> maybeResult) {
        return maybeResult.orElseGet(() -> Result.ok(null))
                .flatMap(value -> Result.ok(Maybe.maybe(value)));
    }

    /**
     * Returns a new Result consisting of the result of applying the function to the contents of the Result.
     *
     * @param f   the mapping function the produces a Result
     * @param <R> the type of the value withing the Result of the mapping function
     * @return a Result
     */
    public abstract <R> Result<R> flatMap(Function<T, Result<R>> f);

    /**
     * Applies the function to the contents of a Maybe within the Result.
     *
     * @param maybeResult the Result that may contain a value
     * @param f           the function to apply to the value
     * @param <T>         the type of the original Result
     * @param <R>         the type of the updated Result
     * @return a new Maybe within a Result
     */
    public static <T, R> Result<Maybe<R>> flatMapMaybe(
            final Result<Maybe<T>> maybeResult,
            final Function<Maybe<T>, Result<Maybe<R>>> f
    ) {
        return maybeResult.flatMap(f);
    }

    /**
     * Checks if the Result is an error.
     *
     * @return true if the Result is an error.
     */
    public abstract boolean isError();

    /**
     * Checks if the Result is a success.
     *
     * @return true if the Result is a success.
     */
    public abstract boolean isOkay();

    @Override
    public abstract <R> Result<R> map(Function<T, R> f);

    /**
     * Matches the Result, either success or error, and supplies the appropriate Consumer with the value or error.
     *
     * @param onSuccess the Consumer to pass the value of a successful Result to
     * @param onError   the Consumer to pass the error from an error Result to
     */
    public abstract void match(Consumer<T> onSuccess, Consumer<Throwable> onError);

    /**
     * Wraps the value within the Result in a Maybe, either a Just if the predicate is true, or Nothing.
     *
     * @param predicate the test to decide
     * @return a Result containing a Maybe that may or may not contain a value
     */
    public abstract Result<Maybe<T>> maybe(Predicate<T> predicate);

    /**
     * Provide the value within the Result, if it is a success, to the Consumer, and returns this Result.
     *
     * @param consumer the Consumer to the value if a success
     * @return this Result
     */
    public abstract Result<T> peek(Consumer<T> consumer);

    /**
     * Provide a way to attempt to recover from an error state.
     *
     * @param f the function to recover from the error
     * @return a new Result, either a Success, or if recovery is not possible an other Err.
     */
    public abstract Result<T> recover(Function<Throwable, Result<T>> f);

    /**
     * A handler for error states.
     *
     * <p>When this is an error then tne Consumer will be supplier with the error. When this is a success, then nothing
     * happens.</p>
     *
     * @param errorConsumer the consumer to handle the error
     */
    public abstract void onError(Consumer<Throwable> errorConsumer);

    /**
     * Maps a Success Result to another Result using a Callable that is able to throw a checked exception.
     *
     * <p>Combination of {@link #flatMap(Function)} and {@link #of(Callable)}.</p>
     *
     * <pre><code>
     *     Integer doSomething() {...}
     *     String doSomethingElse(final Integer value) {...}
     *     Result&lt;String&gt; r = Result.of(() -&gt; doSomething())
     *                              .andThen(value -&gt; () -&gt; doSomethingElse(value));
     * </code></pre>
     *
     * <p>When the Result is an Err, then the original error is carried over and the Callable is never called.</p>
     *
     * @param f   the function to map the Success value into the Callable
     * @param <R> the type of the final Result
     * @return a new Result
     */
    public abstract <R> Result<R> andThen(Function<T, Callable<R>> f);

    /**
     * Perform the continuation with the current Result value then return the current Result, assuming there was no
     * error in the continuation.
     *
     * <pre><code>
     *     Integer doSomething() {...}
     *     void doSomethingElse(final Integer value) {...}
     *     Result&lt;Integer&gt; r = Result.of(() -&gt; doSomething())
     *                              .thenWith(value -&gt; () -&gt; doSomethingElse(value));
     * </code></pre>
     *
     * <p>Where the Result is an Err, then the Result is returned immediately and the continuation is ignored.</p>
     * <p>Where the Result is a Success, then if an exception is thrown by the continuation the Result returned will be
     * a new error Result containing that exception, otherwise the original Result will be returned.</p>
     *
     * @param f the function to map the Success value into the result continuation
     * @return the Result or a new error Result
     */
    public abstract Result<T> thenWith(Function<T, WithResultContinuation<T>> f);

    /**
     * Reduce two Results of the same type into one using the reducing function provided.
     *
     * <p>If either Result is an error, then the reduce will return the error. If both are errors, then the error of
     * {@code this} Result will be returned.</p>
     *
     * @param identify the identify Result
     * @param operator the function to combine the values the Results
     * @return a Result containing the combination of the two Results
     */
    public abstract Result<T> reduce(Result<T> identify, BinaryOperator<T> operator);
}
