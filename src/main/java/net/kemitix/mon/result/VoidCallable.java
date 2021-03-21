package net.kemitix.mon.result;

/**
 * A task that returns void and may throw an exception.
 *
 * <p>Implementors define a single method with no arguments called call.
 * The VoidCallable interface is similar to Callable, but does not return a value.</p>
 */
@FunctionalInterface
public interface VoidCallable {
    /**
     * Executes and may throw an exception.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    void call() throws Exception;
}
