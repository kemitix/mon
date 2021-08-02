## Result
Allows handling error conditions without the need to `catch` exceptions.

* [Full Documentation](https://kemitix.github.io/mon/) 

### Example

```java
import net.kemitix.mon.result.Result;

import java.io.IOException;

class ResultExample implements Runnable {

    public static void main(String[] args) {
        new ResultExample().run();
    }

    @Override
    public void run() {
        Result.of(() -> callRiskyMethod())
              .flatMap(state -> doSomething(state))
              .match(
                  success -> System.out.println(success),
                  error -> error.printStackTrace()
              );
    }

    private String callRiskyMethod() throws IOException {
        return "I'm fine";
    }

    private Result<String> doSomething(String state) {
        return Result.of(() -> state + ", it's all good.");
    }

}
```

In the above example the string `"I'm fine"` is returned by
`callRiskyMethod()` within a successful `Result`. The `.flatMap()` call,
unwraps that `Result` and, as it is a success, passes the contents to
`doSomething()`, which in turn returns a `Result` that the `.flatMap()` call
returns. `match()` is called on the `Result` and, being a success, will call
the success `Consumer`.

Had `callRiskyMethod()` thrown an exception it would have been caught by the
`Result.of()` method, which would then return an error `Result`. An error
`Result` would skip the `flatMap` and continue at the `match()` where it
would have called the error `Consumer`.

### Instance Methods

#### `Result<R> flatMap(Function<T,Result<R>> f)`

If the `Result` is a success, then return a new `Result` containing the result
of applying the function to the contents of the `Result`. If the `Result` is an
error, then return the error.

```java
class ResultFlatMapExample {
    Result<String> result =
            Result.of(() -> getValue())
                    .flatMap(v -> Result.of(() -> String.valueOf(v)));
}
```
---
#### `Result<R> andThen(Function<T,Callable<R>> f)`

Maps a successful `Result` to another `Result` using a `Callable` that is able
to throw a checked exception.

```java
class ResultAndThenExample {
    Result<String> result =
            Result.of(() -> getValue())
                    .andThen(v -> () -> {
                        throw new IOException();
                    });
}
```
---
#### `void match(Consumer<T> onSuccess, Consumer<Throwable> onError)`

Matches the `Result`, either success or error, and supplies the appropriate
`Consumer` with the value or error.

```java
class ResultMatchExample {
    public static void main(String[] args) {
        Result.of(()-> getValue())
                .match(
                        success ->System.out.println(success),
                        error ->System.err.println(error.getMessage())
                );
    }
}
```
---
#### `Result<T> recover(Function<Throwable,Result<T>> f)`

Provide a way to attempt to recover from an error state.

```java
class ResultRecoverExample {
    Result<Integer> result = Result.of(() -> getValue())
            .recover(e -> Result.of(() -> getSafeValue(e)));
}
```
---
#### `Result<T> peek(Consumer<T> consumer)`

Provide the value within the Result, if it is a success, to the `Consumer`,
and returns this Result.

```java
class ResultPeekExample {
    Result<Integer> result = Result.of(() -> getValue())
            .peek(v -> System.out.println(v));
}
```
---
#### `Result<T> thenWith(Function<T,WithResultContinuation<T>> f)`

Perform the continuation with the current `Result` value then return the
current `Result`, assuming there was no error in the continuation.

```java
class ResultThenWithExample {
    Result<Integer> result =
            Result.of(() -> getValue())
                    .thenWith(v -> () -> System.out.println(v))
                    .thenWith(v -> () -> {
                        throw new IOException();
                    });
}
```
---
#### `Result<Maybe<T>> maybe(Predicate<T> predicate)`

Wraps the value within the `Result` in a `Maybe`, either a `Just` if the
predicate is true, or `Nothing`.

```java
class ResultMaybeExample {
    Result<Maybe<Integer>> result = Result.of(() -> getValue())
            .maybe(v -> v % 2 == 0);
}
```
---
#### `<E extends Exception> T orElseThrow(Class<E> type) throws E`

Extracts the successful value from the `Result`, or throws the error when it
is of the given type. Any other errors will be thrown inside an
`UnexpectedErrorResultException`.

```java
class ResultOrElseThrowExample {
    Integer result = Result.of(() -> getValue())
            .orElseThrow(IOException.class);
}
```
---
#### `T orElseThrowUnchecked()`

Extracts the successful value from the `Result`, or throws the error within
an `ErrorResultException`.

```java
class ResultOrElseThrowUncheckedExample {
    Integer result = Result.of(() -> getValue())
            .orElseThrowUnchecked();
}
```
---
