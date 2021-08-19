package net.kemitix.mon.result;

import org.apiguardian.api.API;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * A @{link Result} with no value.
 */
public interface ResultVoid extends BaseResult {

    /**
     * Matches the Result, either success or error, and supplies the appropriate
     * Consumer with the value or error.
     *
     * <pre><code>
     * Result.ok()
     *       .match(
     *           () -&gt; doSomething(),
     *           error -&gt; handleError(error)
     *       );
     * </code></pre>
     *
     * @param onSuccess the Consumer to pass the value of a successful Result to
     * @param onError   the Consumer to pass the error from an error Result to
     * @return the original ResultVoid
     */
    @API(status = STABLE)
    ResultVoid match(Runnable onSuccess, Consumer<Throwable> onError);

    /**
     * Attempts to restore an error {@code ResultVoid} to a success.
     *
     * <p>When the Result is already a success, then the result is returned
     * unmodified.</p>
     *
     * <pre><code>
     * void doSomethingRisky(String s) throws Exception {...}
     * ResultVoid result = Result.ofVoid(() -&gt; doSomethingRisky("first"))
     *                           .recover(e -&gt; Result.ofVoid(() -&gt; doSomethingRisky("second")));
     * </code></pre>
     *
     * @param f the function to recover from the error
     * @return if Result is an error, a new Result, either a Success, or if
     * recovery is not possible another error. If the Result is already a
     * success, then this returns itself.
     */
    @API(status = STABLE)
    ResultVoid recover(Function<Throwable, ResultVoid> f);


    /**
     * A handler for success states.
     *
     * <pre><code>
     * void doSomethingRisky() throws Exception {...}
     * void handleSuccess() {...}
     * Result.ofVoid(() -&gt; doSomethingRisky()) // ResultVoid
     *       .onSuccess(() -&gt; handleSuccess());
     * </code></pre>
     *
     * <p>When this is a success then tne Consumer will be supplied with the
     * success value. When this is an error, then nothing happens.</p>
     *
     * @param runnable the call if the Result is a success
     * @return the original ResultVoid
     */
    @API(status = STABLE)
    ResultVoid onSuccess(Runnable runnable);

    /**
     * A handler for error state, when the error matches the errorClass.
     *
     * <p>If the `Result` is an error and that error is an instance of the
     * errorClass, then supply the error to the `Consumer`. Does nothing if the
     * error is not an instance of the errorClass, or is a success.</p>
     *
     * <p>Similar to the catch block in a try-catch.</p>
     *
     * <pre><code>
     * void handleError(UnsupportedOperationException e) {...}
     * Result.of(() -&gt; getValue())
     *       .onError(UnsupportedOperationException.class,
     *                e -&gt; handleError(e))
     * </code></pre>
     *
     * @param errorClass the class of Throwable to match
     * @param consumer the consumer to call if it matches
     * @param <E> the Type of the Throwable to match
     * @return the original unmodified Result
     */
    @API(status = STABLE)
    <E extends Throwable> ResultVoid onError(
            Class<E> errorClass,
            Consumer<E> consumer
    );

    /**
     * Execute the callable if the {@code Result} is a success, ignore it if is an error.
     *
     * <pre><code>
     * Result.ofVoid(() -&gt; doSomethingRisky())
     *       .andThen(() -&gt; doSomethingRisky("again"));
     * </code></pre>
     *
     * @param f   the function to map the Success value into the Callable
     * @return itself unless the callable fails when it will return a new error Result
     */
    @API(status = STABLE)
    ResultVoid andThen(VoidCallable f);

    /**
     * Replaces the current Result with the result of the callable.
     *
     * <p>Discards the success/error state or the current Result.</p>
     *
     * <p>If the callable results in a new error, then that error will be in the returned Result.</p>
     *
     * <pre><code>
     * Result&lt;Integer&gt; result = Result.ofVoid(() -&gt; doSomethingRisky())
     *                          .inject(() -&gt; 1);
     * </code></pre>
     *
     * @param callable the callable to create the new value
     * @param <T> the type of the new value
     * @return a new Result with the result of callable
     */
    default <T> Result<T> inject(Callable<T> callable) {
        return Result.of(callable);
    }

}
