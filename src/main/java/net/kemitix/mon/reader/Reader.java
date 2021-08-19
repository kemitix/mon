package net.kemitix.mon.reader;

import org.apiguardian.api.API;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Returns a program ready to run upon the supply of a suitable environment.
 *
 * @param <E> The type of the environment required by the program.
 * @param <R> The type of the result returned by the program.
 */
@API(status = API.Status.EXPERIMENTAL, since = "3.0.1")
@FunctionalInterface
public interface Reader<E, R> {

    /**
     * Executes the program.
     *
     * @param env the required environment
     * @return the result of the program
     */
    R run(E env);

    /**
     * Applies the function provided to the reader when it is run.
     *
     * @param f the function, which takes an {@link E} as its only parameter
     * @param <V> the type of the functions output
     * @return a new Reader to provide the result of the supplied function
     */
    @API(status = API.Status.EXPERIMENTAL)
    default <V> Reader<E, V> map(Function<R, V> f) {
        return e -> f.apply(run(e));
    }

    /**
     * Applies the function provided to the reader when it is run.
     *
     * @param f the function, which takes an {@link E} and the previously
     *          generated value as its two parameters
     * @param <V> the type of the functions output
     * @return a new Reader to provided the result of the supplied function
     */
    @API(status = API.Status.EXPERIMENTAL)
    default <V> Reader<E, V> andThen(BiFunction<E, R, V> f) {
        return env -> f.apply(env, run(env));
    }
}
