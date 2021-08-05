package net.kemitix.mon.result;

import org.apiguardian.api.API;

import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Base interface for {@link Result} and {@link ResultVoid}.
 */
public interface BaseResult {

    /**
     * Checks if the Result is an error.
     *
     * <pre><code>
     * boolean isError = Result.of(() -&gt; getValue())
     *                         .isError();
     * </code></pre>
     *
     * @return true if the Result is an error.
     */
    @API(status = STABLE)
    boolean isError();

    /**
     * Checks if the Result is a success.
     *
     * <pre><code>
     * boolean isOkay = Result.of(() -&gt; getValue())
     *                        .isOkay();
     * </code></pre>
     *
     * @return true if the Result is a success.
     */
    @API(status = STABLE)
    boolean isOkay();

    /**
     * A handler for error states.
     *
     * <p>If the {@code Result} is an error, then supply the error
     * to the {@code Consumer}. Does nothing if the {@code Result} is a
     * success.</p>
     *
     * <p>When this is an error then tne Consumer will be supplied with the
     * error. When this is a success, then nothing happens.</p>
     *
     * <pre><code>
     * void handleError(Throwable e) {...}
     * Result.of(() -&gt; doSomething())
     *       .onError(e -&gt; handleError(e));
     * </code></pre>
     *
     * @param errorConsumer the consumer to handle the error
     */
    @API(status = STABLE)
    void onError(Consumer<Throwable> errorConsumer);

}
