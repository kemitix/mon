package net.kemitix.mon.reader;

/**
 * Returns a program ready to run upon the supply of a suitable environment.
 *
 * @param <E> The type of the environment required by the program.
 * @param <R> The type of the result returned by the program.
 */
public interface Reader<E, R> {

    /**
     * Executes the program.
     *
     * @param env the required environment
     * @return the result of the program
     */
    R run(E env);

}
