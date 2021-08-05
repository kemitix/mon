package net.kemitix.mon;

import net.kemitix.mon.result.ThrowableFunction;

/**
 * The ThrowableFunctor is used for types that can be mapped over.
 *
 * <p>A ThrowableFunctor is identical to a normal Functor except that the
 * map method may throw an exception.</p>
 *
 * <p>Implementations of ThrowableFunctor should satisfy the following laws:</p>
 *
 * <ul>
 *     <li>map id  ==  id</li>
 *     <li>map (f . g)  ==  map f . map g</li>
 * </ul>
 *
 * @param <T> the type of the Functor
 * @param <F> the type of the mapped Functor
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface ThrowableFunctor<T, F extends ThrowableFunctor<?, ?>> {

    /**
     * Applies the function to the value within the ThrowableFunctor, returning
     * the result within another ThrowableFunctor.
     *
     * @param f   the function to apply
     * @param <R> the type of the content of the mapped functor
     *
     * @return a ThrowableFunctor containing the result of the function
     * {@code f} applied to the value
     */
    <R> F map(ThrowableFunction<T, R, ?> f);
}
