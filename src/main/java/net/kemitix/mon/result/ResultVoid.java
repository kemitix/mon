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
     *           () -> doSomething(),
     *           error -> handleError(error)
     *       );
     * </code></pre>
     *
     * @param onSuccess the Consumer to pass the value of a successful Result to
     * @param onError   the Consumer to pass the error from an error Result to
     */
    @API(status = STABLE)
    void match(Runnable onSuccess, Consumer<Throwable> onError);

    /**
     * Attempts to restore an error {@code ResultVoid} to a success.
     *
     * <p>When the Result is already a success, then the result is returned
     * unmodified.</p>
     *
     * <pre><code>
     * void doSomethingRisky(String s) throws Exception {...}
     * ResultVoid result = Result.ofVoid(() -> doSomethingRisky("first"))
     *                           .recover(e -> Result.ofVoid(() -> doSomethingRisky("second")));
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
     * Result.ofVoid(() -> doSomethingRisky()) // ResultVoid
     *       .onSuccess(() -> handleSuccess());
     * </code></pre>
     *
     * <p>When this is a success then tne Consumer will be supplied with the
     * success value. When this is an error, then nothing happens.</p>
     *
     * @param runnable the call if the Result is a success
     */
    @API(status = STABLE)
    void onSuccess(Runnable runnable);

    <E extends Throwable> ResultVoid onError(
            Class<E> errorClass,
            Consumer<E> consumer
    );

    ResultVoid andThen(VoidCallable f);

    <T> Result<T> inject(Callable<T> f);

}
