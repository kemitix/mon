package net.kemitix.mon.result;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SuccessVoid implements ResultVoid {

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isOkay() {
        return true;
    }

    @Override
    public void match(final Runnable onSuccess, final Consumer<Throwable> onError) {
        onSuccess.run();
    }

    @Override
    public ResultVoid recover(final Function<Throwable, ResultVoid> f) {
        return this;
    }

    @Override
    public void onSuccess(final Runnable runnable) {
        runnable.run();
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
            return Result.error(e);
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
