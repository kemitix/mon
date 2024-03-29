package net.kemitix.mon.result;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Error Result with no value type.
 */
public class ErrVoid implements ResultVoid {

    private final Throwable error;

    ErrVoid(final Throwable error) {
        this.error = error;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isOkay() {
        return false;
    }

    @Override
    public ResultVoid match(
            final Runnable onSuccess,
            final Consumer<Throwable> onError
    ) {
        onError.accept(error);
        return this;
    }

    @Override
    public ResultVoid recover(final Function<Throwable, ResultVoid> f) {
        return f.apply(error);
    }

    @Override
    public ResultVoid onSuccess(final Runnable runnable) {
        return this;
    }

    @Override
    public void onError(final Consumer<Throwable> errorConsumer) {
        errorConsumer.accept(error);
    }

    @Override
    public <E extends Throwable> ResultVoid onError(
            final Class<E> errorClass,
            final Consumer<E> consumer
    ) {
        if (error.getClass().isAssignableFrom(errorClass)) {
            consumer.accept((E) error);
        }
        return this;
    }

    @Override
    public ResultVoid andThen(final VoidCallable f) {
        return this;
    }

    @Override
    public String toString() {
        return String.format("Result.ErrVoid{error=%s}", error);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof ErrVoid && Objects.equals(error, ((ErrVoid) other).error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }

}
