package net.kemitix.mon.result;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ResultVoid extends BaseResult {

    void match(Runnable onSuccess, Consumer<Throwable> onError);

    ResultVoid recover(Function<Throwable, ResultVoid> f);

    void onSuccess(Runnable runnable);

    <E extends Throwable> ResultVoid onError(
            Class<E> errorClass,
            Consumer<E> consumer
    );

    ResultVoid andThen(VoidCallable f);

    <T> Result<T> inject(Callable<T> f);

}
