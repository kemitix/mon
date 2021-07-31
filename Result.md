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

### Static Constructors

### Static Methods

These static methods provide integration with the `Maybe` class.

#### `Result<Maybe<T>> invert(Maybe<Result<T>> maybeResult)`

Swaps the `Result` within a `Maybe`, so that `Result` contains a `Maybe`.

```java
class ResultInvertExample {
    Maybe<Result<Integer>> maybe = Maybe.maybe(Result.of(() -> getValue()));
    Result<Maybe<Integer>> result = Result.invert(maybe);
}
```
---
#### `Result<Maybe<R>> flatMapMaybe(Result<Maybe<T>> maybeResult, Function<Maybe<T>,Result<Maybe<R>>> f)`

Applies the function to the contents of a `Maybe` within the `Result`.

```java
class ResultOkExample {
    Result<Maybe<Integer>> result = Result.of(() -> Maybe.maybe(getValue()));
    Result<Maybe<Integer>> maybeResult = Result.flatMapMaybe(result,
            maybe -> Result.of(() -> maybe.map(v -> v * 2)));
}
```
---
#### `Result<R> applyOver(Stream<N> stream, Function<N, R> f, R zero, BiFunction<R, R, R> accumulator)`

Applies a function to a stream of values, folding the results using the zero
value and accumulator function.

If any value results in an error when applying the function, then processing
stops, and a Result containing that error is returned.

Returns a success Result of the accumulated outputs if all values were
transformed successfully by the function, or an error Result for the first value
that failed.

```java
class ApplyOverExample {

    public static void main(String[] args) {
        Function<String, Integer> f = s -> {
            if ("dd".equals(s)) {
                throw new RuntimeException("Invalid input: " + s);
            }
            return s.length();
        };

        Stream<String> okayStream = Stream.of("aa", "bb");
        Result<Integer> resultOkay = Result.applyOver(okayStream, f, 0, Integer::sum);
        resultOkay.match(
                success -> System.out.println("Total length: " + success),
                error -> System.out.println("Error: " + error.getMessage())
        );
        // Total length: 4

        Stream<String> errorStream = Stream.of("cc", "dd");
        Result<Integer> resultError = Result.applyOver(errorStream, f, 0, Integer::sum);
        resultError.match(
                success -> System.out.println("Total length: " + success), // will not match
                error -> System.out.println("Error: " + error.getMessage())
        );
        // Error: Invalid input: dd
    }
}
```
---
#### `Result<Void> applyOver(Stream<N> stream, Consumer<N> consumer)`

Applies a consumer to a stream of values.

If any value results in an error when accepted by the consumer, then processing
stops, and a Result containing that error is returned.

Returns a success Result (with no value) if all values were consumed
successfully by the function, or an error Result for the first value that
failed.

```java
import net.kemitix.mon.result.Result;

import java.util.function.Consumer;

class ApplyOverExample {

    public static void main(String[] args) {
        List<String> processed = new ArrayList<>();
        Consumer<String> consumer = s -> {
            if ("dd".equals(s)) {
                throw new RuntimeException("Invalid input: " + s);
            }
            processed.add(s);
        };

        Stream<String> okayStream = Stream.of("aa", "bb");
        Result<Void> resultOkay = Result.applyOver(okayStream, consumer);
        resultOkay.match(
                success -> System.out.println("All processed okay."),
                error -> System.out.println("Error: " + error.getMessage())
        );
        System.out.println("Processed: " + processed);
        // All processed okay.
        // Processed: [aa, bb]

        processed.clear();
        Stream<String> errorStream = Stream.of("cc", "dd", "ee");
        Result<Void> resultError = Result.applyOver(errorStream, consumer);
        resultError.match(
                success -> System.out.println("All processed okay."),
                error -> System.out.println("Error: " + error.getMessage())
        );
        System.out.println("Processed: " + processed);
        // Error: Invalid input: dd
        // Processed: [cc]
    }
}
```
---
#### `Result<R> flatApplyOver(Stream<T> stream, Function<T, Result<R>> f, R zero, BiFunction<R, R, R> accumulator)`

Applies a function to a stream of values, folding the results using the zero
value and accumulator function.

If any value results in an error when applying the function, then processing
stops, and a Result containing that error is returned.

Returns a success Result of the accumulated function outputs if all values were
transformed successfully, or an error Result for the first value that failed.

Similar to `Result<R> applyOver(Stream<N> stream, Function<N, R> f, R zero, BiFunction<R, R, R> accumulator)`
except that the result of the `f` function is a `Result`, and to a `flatMap`
method in that the Result is not nested with in another Result.

```java
class ApplyOverExample {

    public static void main(String[] args) {
        Function<String, Result<Integer>> f = s -> {
            if ("dd".equals(s)) {
                return Result.error(new RuntimeException("Invalid input: " + s));
            }
            return Result.ok(s.length());
        };

        Stream<String> okayStream = Stream.of("aa", "bb");
        Result<Integer> resultOkay = Result.flatApplyOver(okayStream, f, 0, Integer::sum);
        resultOkay.match(
                success -> System.out.println("Total length: " + success),
                error -> System.out.println("Error: " + error.getMessage())
        );
        // Total length: 4

        Stream<String> errorStream = Stream.of("cc", "dd");
        Result<Integer> resultError = Result.flatApplyOver(errorStream, f, 0, Integer::sum);
        resultError.match(
                success -> System.out.println("Total length: " + success), // will not match
                error -> System.out.println("Error: " + error.getMessage())
        );
        // Error: Invalid input: dd
    }
}
```
---
### Instance Methods

#### `Result<R> map(Function<T,R> f)`

If the `Result` is a success, then apply the function to the value within the
`Result`, returning the result within another `Result`. If the `Result` is an
error, then return the error.

```java
class ResultMapExample {
    Result<String> result = Result.of(() -> getValue())
            .map(v -> String.valueOf(v));
}
```
---
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
#### `T orElseThrow()`

Extracts the successful value from the `Result`, or throws the error
within a `CheckedErrorResultException`.

```java
class ResultOrElseThrowExample {
    Integer result = Result.of(() -> getValue())
            .orElseThrow();
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
#### `void onError(Consumer<Throwable> errorConsumer)`

A handler for error states. If the `Result` is an error, then supply the error
to the `Consumer`. Does nothing if the `Result` is a success.

```java
class ResultOnErrorExample {
    public static void main(String[] args) {
        Result.of(() -> getValue())
            .onError(e -> handleError(e));
    }
}
```
---
#### `<E extends Throwable> Result<T> onError(Class<E> errorClass, Consumer<E> consumer)`

A handler for error state, when the error matches the errorClass. If the
`Result` is an error and that error is an instance of the errorClass, then
supply the error to the `Consumer`. Does nothing if the error is not an instance
of the errorClass, or is a success.

Similar to the catch block in a try-catch.

```java
class ResultOnErrorExample {
    public static void main(String[] args) {
        Result.of(() -> getValue())
            .onError(UnsupportedOperationException.class,
                    e -> handleError(e));
    }
}
```
---
#### `boolean isOkay()`

Checks if the `Result` is a success.

```java
class ResultIsOkayExample {
    boolean isOkay = Result.of(() -> getValue())
            .isOkay();
}
```
---
#### `boolean isError()`

Checks if the `Result` is an error.

```java
class ResultIsErrorExample {
    boolean isError = Result.of(() -> getValue())
            .isError();
}
```
---
#### `Either<Throwable, T> toEither()`

Converts the `Result` into an `Either`. A `Success` will become a `Right` and an
`Error` will become a `Left`.

```java
import net.kemitix.mon.result.Result;

class ResultToEitherExample {
    Result<String> success = Result.ok("success");
    Result<String> error = Result.error(new RuntimeException());

    Either<Throwable, String> eitherRight = success.toEither();
    Either<Throwable, String> eitherLeft = error.toEither();
}
```
