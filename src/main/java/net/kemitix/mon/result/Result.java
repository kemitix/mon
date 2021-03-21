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

import net.kemitix.mon.Functor;
import net.kemitix.mon.experimental.either.Either;
import net.kemitix.mon.maybe.Maybe;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An Either type for holding a result or an error (Throwable).
 *
 * @param <T> the type of the result when a success
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressWarnings({"methodcount", "PMD.TooManyMethods", "PMD.ExcessivePublicCount"})
public interface Result<T> extends Functor<T, Result<?>> {

    /**
     * Creates a Result from the Maybe, where the Result will be an error if the Maybe is Nothing.
     *
     * @param maybe the Maybe the might contain the value of the Result
     * @param error the error that will be the Result if maybe is Nothing
     * @param <T>   the type of the Maybe and the Result
     * @return a Result containing the value of the Maybe when it is a Just, or the error when it is Nothing
     */
    static <T> Result<T> from(final Maybe<T> maybe, final Supplier<Throwable> error) {
        return maybe.map(Result::ok)
                .orElseGet(() -> Result.error(error.get()));
    }

    /**
     * Creates a Result from the Either, where the Result will be an error if
     * the Either is a Left, and a success if it is a Right.
     *
     * @param either the either that could contain an error in left or a value in right
     * @param <T>    the type of the right value
     * @return a Result containing the right value of the Either when it is a
     * Right, or the left error when it is a Left.
     */
    static <T> Result<T> from(Either<Throwable, T> either) {
        return Result.from(
                Maybe.fromOptional(either.getRight()),
                () -> either.getLeft().get());
    }

    /**
     * Create a Result for an error.
     *
     * @param error the error (Throwable)
     * @param <T>   the type had the result been a success
     * @return an error Result
     */
    default <T> Result<T> err(final Throwable error) {
        return new Err<>(error);
    }

    /**
     * Create a Result for an error.
     *
     * @param error the error (Throwable)
     * @param <T>   the type had the result been a success
     * @return an error Result
     */
    static <T> Result<T> error(final Throwable error) {
        return new Err<>(error);
    }

    /**
     * Create a Result for a output of the Callable.
     *
     * @param callable the callable to produce the result
     * @param <T>      the type of the value
     * @return a Result
     */
    @SuppressWarnings({"illegalcatch", "PMD.AvoidCatchingThrowable", "PMD.AvoidDuplicateLiterals"})
    default <T> Result<T> result(final Callable<T> callable) {
        try {
            return Result.ok(callable.call());
        } catch (final Throwable e) {
            return Result.error(e);
        }
    }

    /**
     * Create a Result for a output of the Callable.
     *
     * @param callable the callable to produce the result
     * @param <T>      the type of the value
     * @return a Result
     */
    @SuppressWarnings({"illegalcatch", "PMD.AvoidCatchingThrowable"})
    static <T> Result<T> of(final Callable<T> callable) {
        try {
            return Result.ok(callable.call());
        } catch (final Throwable e) {
            return Result.error(e);
        }
    }

    /**
     * Create a Result for calling a Callable that produces no output.
     *
     * @param callable the callable to call
     * @return a Result with no value
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    static Result<Void> ofVoid(final Callable<Void> callable) {
        try {
            callable.call();
            return Result.ok(null);
        } catch (final Throwable e) {
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
    default <T> Result<T> success(final T value) {
        return new Success<>(value);
    }

    /**
     * Create a Result for a success.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return a successful Result
     */
    static <T> Result<T> ok(final T value) {
        return new Success<>(value);
    }

    /**
     * Creates a success Result with no value.
     *
     * @return a successful Result
     */
    static Result<Void> ok() {
        return Result.ok(null);
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
    static <T> Maybe<T> toMaybe(final Result<T> result) {
        try {
            return Maybe.just(result.orElseThrow());
        } catch (final CheckedErrorResultException throwable) {
            return Maybe.nothing();
        }
    }

    /**
     * Converts the `Result` into an `Either`.
     *
     * @return A `Right` for a success or a `Left` for an error.
     */
    default Either<Throwable, T> toEither() {
        var either = new AtomicReference<Either<Throwable, T>>();
        match(
                success -> either.set(Either.right(success)),
                error -> either.set(Either.left(error))
        );
        return either.get();
    }

    /**
     * Extracts the successful value from the result, or throws the error within a {@link CheckedErrorResultException}.
     *
     * @return the value if a success
     * @throws CheckedErrorResultException if the result is an error
     */
    T orElseThrow() throws CheckedErrorResultException;

    /**
     * Extracts the successful value from the result, or throws the error Throwable.
     *
     * @param type the type of checked exception that may be thrown
     * @param <E>  the type of the checked exception to throw
     * @return the value if a success
     * @throws E if the result is an error
     */
    <E extends Exception> T orElseThrow(Class<E> type) throws E;

    /**
     * Extracts the successful value from the result, or throws the error in a {@link UnexpectedErrorResultException}.
     *
     * @return the value if a success
     */
    T orElseThrowUnchecked();

    /**
     * Swaps the inner Result of a Maybe, so that a Result is on the outside.
     *
     * @param maybeResult the Maybe the contains a Result
     * @param <T>         the type of the value that may be in the Result
     * @return a Result containing a Maybe, the value in the Maybe was the value in a successful Result within the
     * original Maybe. If the original Maybe is Nothing, the Result will contain Nothing. If the original Result was an
     * error, then the Result will also be an error.
     */
    static <T> Result<Maybe<T>> swap(final Maybe<Result<T>> maybeResult) {
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
    <R> Result<R> flatMap(Function<T, Result<R>> f);

    /**
     * Applies the function to the contents of a Maybe within the Result.
     *
     * @param maybeResult the Result that may contain a value
     * @param f           the function to apply to the value
     * @param <T>         the type of the original Result
     * @param <R>         the type of the updated Result
     * @return a new Maybe within a Result
     */
    static <T, R> Result<Maybe<R>> flatMapMaybe(
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
    boolean isError();

    /**
     * Checks if the Result is a success.
     *
     * @return true if the Result is a success.
     */
    boolean isOkay();

    @Override
    <R> Result<R> map(Function<T, R> f);

    /**
     * Matches the Result, either success or error, and supplies the appropriate Consumer with the value or error.
     *
     * @param onSuccess the Consumer to pass the value of a successful Result to
     * @param onError   the Consumer to pass the error from an error Result to
     */
    void match(Consumer<T> onSuccess, Consumer<Throwable> onError);

    /**
     * Wraps the value within the Result in a Maybe, either a Just if the predicate is true, or Nothing.
     *
     * @param predicate the test to decide
     * @return a Result containing a Maybe that may or may not contain a value
     */
    Result<Maybe<T>> maybe(Predicate<T> predicate);

    /**
     * Provide the value within the Result, if it is a success, to the Consumer, and returns this Result.
     *
     * @param consumer the Consumer to the value if a success
     * @return this Result
     */
    Result<T> peek(Consumer<T> consumer);

    /**
     * Provide a way to attempt to recover from an error state.
     *
     * <p>When the Result is not an Err, this is a no-op.</p>
     *
     * @param f the function to recover from the error
     * @return if Result is an Err, a new Result, either a Success, or if recovery is not possible an other Err.
     * If Result is a Success, then this results itself.
     */
    Result<T> recover(Function<Throwable, Result<T>> f);

    /**
     * A handler to success states.
     *
     * <p>When this is a success then tne Consumer will be supplied with the
     * success value. When this is an error, then nothing happens.</p>
     *
     * @param successConsumer the consumer to handle the success
     */
    void onSuccess(Consumer<T> successConsumer);

    /**
     * A handler for error states.
     *
     * <p>When this is an error then tne Consumer will be supplied with the
     * error. When this is a success, then nothing happens.</p>
     *
     * @param errorConsumer the consumer to handle the error
     */
    void onError(Consumer<Throwable> errorConsumer);

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
    <R> Result<R> andThen(Function<T, Callable<R>> f);

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
    Result<T> thenWith(Function<T, WithResultContinuation<T>> f);

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
    Result<T> reduce(Result<T> identify, BinaryOperator<T> operator);

    /**
     * Applies a function to a stream of values, folding the results using the
     * zero value and accumulator function.
     *
     * <p>If any value results in an error when applying the function, then
     * processing stops and a Result containing that error is returned,</p>
     *
     * @param stream      the values to apply the function to
     * @param f           the function to apply to the values
     * @param zero        the initial value to use with the accumulator
     * @param accumulator the function to combine function outputs together
     * @param <N>         the type of the stream values
     * @param <R>         the type of the output value
     * @return a Success Result of the accumulated function outputs if all
     * values were transformed successfully by the function, or an Err Result
     * for the first value that failed.
     */
    static <N, R> Result<R> applyOver(
            Stream<N> stream,
            Function<N, R> f,
            R zero,
            BiFunction<R, R, R> accumulator
    ) {
        var acc = new AtomicReference<>(Result.ok(zero));
        stream.map(t -> Result.of(() -> f.apply(t)))
                .peek(r ->
                        r.onSuccess(vNew ->
                                acc.getAndUpdate(rResult ->
                                        rResult.map(vOld ->
                                                accumulator.apply(vNew, vOld)))))
                .dropWhile(Result::isOkay)
                .limit(1)
                .forEach(acc::set);
        return acc.get();
    }

    /**
     * Applies a consumer to a stream of values.
     *
     * <p>If any value results in an error when accepted by the consumer, then
     * processing stops and a Result containing that error is returned,</p>
     *
     * @param stream   the value to supply to the consumer
     * @param consumer the consumer to receive the values
     * @param <N>      the type of the stream values
     * @return a Success Result (with no value) if all values were transformed
     * successfully by the function, or an Err Result for the first value that
     * failed.
     */
    static <N> Result<Void> applyOver(
            Stream<N> stream,
            Consumer<N> consumer
    ) {
        return applyOver(stream, n -> {
            consumer.accept(n);
            return null;
        }, null, (unused1, unused2) -> null);
    }

    /**
     * Applies a function to a stream of values, folding the results using the
     * zero value and accumulator function.
     *
     * <p>If any value results in an error when applying the function, then
     * processing stops and a Result containing that error is returned,</p>
     *
     * @param stream      the values to apply the function to
     * @param f           the function to apply to the values
     * @param zero        the initial value to use with the accumulator
     * @param accumulator the function to combine function outputs together
     * @param <T>         the type of the stream values
     * @param <R>         the type of the output value
     * @return a Success Result of the accumulated function outputs if all
     * values were transformed successfully by the function, or an Err Result
     * for the first value that failed.
     */
    static <T, R> Result<R> flatApplyOver(
            Stream<T> stream,
            Function<T, Result<R>> f,
            R zero,
            BiFunction<R, R, R> accumulator
    ) {
        var acc = new AtomicReference<>(Result.ok(zero));
        stream.map(f)
                .peek(r -> r.onSuccess(vNew ->
                        acc.getAndUpdate(rResult ->
                                rResult.map(vOld ->
                                        accumulator.apply(vNew, vOld)))))
                .dropWhile(Result::isOkay)
                .limit(1)
                .forEach(acc::set);
        return acc.get();
    }

}
