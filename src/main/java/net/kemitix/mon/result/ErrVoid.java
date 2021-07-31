package net.kemitix.mon.result;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public void match(
            final Runnable onSuccess,
            final Consumer<Throwable> onError
    ) {
        onError.accept(error);
    }

    @Override
    public ResultVoid recover(final Function<Throwable, ResultVoid> f) {
        return f.apply(error);
    }

    @Override
    public void onSuccess(final Runnable runnable) {
        // do nothing
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
    public <T> Result<T> inject(final Callable<T> f) {
        final Result<T> result = Result.of(f);
        if (result.isError()) {
            return new Err<>(error);// the original error
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Result.ErrorVoid{error=%s}", error);
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
