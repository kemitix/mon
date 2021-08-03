package net.kemitix.mon.result;

import org.apiguardian.api.API;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.STABLE;

public interface ResultVoid extends BaseResult {

    /**
     * Matches the Result, either success or error, and supplies the appropriate Consumer with the value or error.
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

    ResultVoid recover(Function<Throwable, ResultVoid> f);

    void onSuccess(Runnable runnable);

    <E extends Throwable> ResultVoid onError(
            Class<E> errorClass,
            Consumer<E> consumer
    );

    ResultVoid andThen(VoidCallable f);

    <T> Result<T> inject(Callable<T> f);

}
