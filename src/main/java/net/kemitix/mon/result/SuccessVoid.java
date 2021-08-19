package net.kemitix.mon.result;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Successful Result, with no value.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessVoid implements ResultVoid {

    private static final ResultVoid INSTANCE = new SuccessVoid();

    /**
     * Get the SuccessVoid instance.
     *
     * <p>The SuccessVoid, having no value, represents the state of success.</p>
     *
     * @return the SuccessVoid
     */
    public static ResultVoid getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isOkay() {
        return true;
    }

    @Override
    public ResultVoid match(final Runnable onSuccess, final Consumer<Throwable> onError) {
        onSuccess.run();
        return this;
    }

    @Override
    public ResultVoid recover(final Function<Throwable, ResultVoid> f) {
        return this;
    }

    @Override
    public ResultVoid onSuccess(final Runnable runnable) {
        runnable.run();
        return this;
    }

    @Override
    public void onError(final Consumer<Throwable> errorConsumer) {
        // do nothing - this is not an error
    }

    @Override
    public <E extends Throwable> ResultVoid onError(
            final Class<E> errorClass,
            final Consumer<E> consumer
    ) {
        return this;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public ResultVoid andThen(final VoidCallable f) {
        try {
            f.call();
            return this;
        } catch (Exception e) {
            return new ErrVoid(e);
        }
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof SuccessVoid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(SuccessVoid.class);
    }

    @Override
    public String toString() {
        return "Result.SuccessVoid{}";
    }

}
