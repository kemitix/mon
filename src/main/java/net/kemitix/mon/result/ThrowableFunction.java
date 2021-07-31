package net.kemitix.mon.result;

/**
 * Represents a function that accepts one argument and produces a result.
 * This is a functional interface whose functional method is apply(Object).
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the exception that could be thrown
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws E if the function fails
     */
    R apply(T value) throws E;

}
