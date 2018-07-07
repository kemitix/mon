package net.kemitix.mon;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;
import net.kemitix.mon.result.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class ResultTest implements WithAssertions {

    @Test
    public void equality() {
        assertThat(Result.ok(1)).isEqualTo(Result.ok(1));
        assertThat(Result.ok(1)).isNotEqualTo(Result.ok(2));
        final RuntimeException runtimeException = new RuntimeException();
        assertThat(Result.ok(1)).isNotEqualTo(Result.error(runtimeException));
        assertThat(Result.error(runtimeException)).isEqualTo(Result.error(runtimeException));
        assertThat(Result.error(runtimeException)).isNotEqualTo(Result.error(new RuntimeException()));
        assertThat(Result.ok(1).equals("1")).isFalse();
        assertThat(Result.error(new RuntimeException()).equals("1")).isFalse();
    }

    @Test
    public void successHashCode() {
        assertThat(Result.ok(1).hashCode()).isNotEqualTo(Result.ok(2).hashCode());
    }

    @Test
    public void errorHashCode() {
        // despite having 'equivalent' exceptions, the exceptions are distinct instances, so should be considered unique
        final RuntimeException exception1 = new RuntimeException("message");
        final RuntimeException exception2 = new RuntimeException("message");
        assertThat(exception1.hashCode()).isNotEqualTo(exception2.hashCode());
        final Result<Object> error1 = Result.error(exception1);
        final Result<Object> error2 = Result.error(exception2);
        assertThat(error1.hashCode()).isNotEqualTo(error2.hashCode());
    }

    @Test
    public void createSuccess_isSuccess() {
        //when
        final Result<String> result = Result.ok("good");
        //then
        assertThat(result.isOkay()).isTrue();
    }

    @Test
    public void createSuccess_isNotError() {
        //when
        final Result<String> result = Result.ok("good");
        //then
        assertThat(result.isError()).isFalse();
    }

    @Test
    public void createSuccess_matchSuccess() {
        //given
        final Result<String> result = Result.ok("good");
        //then
        result.match(
                success -> assertThat(success).isEqualTo("good"),
                error -> fail("not an error")
        );
    }

    @Test
    public void createError_isError() {
        //when
        final Result<String> result = Result.error(new Exception());
        //then
        assertThat(result.isOkay()).isFalse();
    }

    @Test
    public void createError_isNotSuccess() {
        //when
        final Result<String> result = Result.error(new Exception());
        //then
        assertThat(result.isError()).isTrue();
    }

    @Test
    public void createError_matchError() {
        //given
        final Result<Object> result = Result.error(new Exception("bad"));
        //then
        result.match(
                success -> fail("not a success"),
                error -> assertThat(error.getMessage()).isEqualTo("bad")
        );
    }

    @Test
    public void successFlatMap_success_isSuccess() {
        //given
        final Result<String> result = Result.ok("good");
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.ok(v.toUpperCase()));
        //then
        assertThat(flatMap.isOkay()).isTrue();
        flatMap.match(
                success -> assertThat(success).isEqualTo("GOOD"),
                error -> fail("not an error")
        );
    }

    @Test
    public void successFlatMap_error_isError() {
        //given
        final Result<String> result = Result.ok("good");
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.error(new Exception("bad flat map")));
        //then
        assertThat(flatMap.isOkay()).isFalse();
    }

    @Test
    public void errorFlatMap_success_isError() {
        //given
        final Result<String> result = Result.error(new Exception("bad"));
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.ok(v.toUpperCase()));
        //then
        assertThat(flatMap.isError()).isTrue();
    }

    @Test
    public void errorFlatMap_error_isError() {
        //given
        final Result<String> result = Result.error(new Exception("bad"));
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.error(new Exception("bad flat map")));
        //then
        assertThat(flatMap.isError()).isTrue();
    }

    @Test
    public void success_whenMap_isSuccess() {
        //given
        final Result<Integer> okResult = Result.ok(1);
        //when
        final Result<String> result = okResult.map(value -> String.valueOf(value));
        //then
        assertThat(result.isOkay()).isTrue();
        result.match(
                success -> assertThat(success).isEqualTo("1"),
                error -> fail("not an error")
        );
    }

    @Test
    public void error_whenMap_isError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> errorResult = Result.error(exception);
        //when
        final Result<String> result = errorResult.map(value -> String.valueOf(value));
        //then
        assertThat(result.isError()).isTrue();
        result.match(
                success -> fail("not an success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void successMaybe_whenPasses_isSuccessJust() {
        //given
        final Result<Integer> okResult = Result.ok(1);
        //when
        final Result<Maybe<Integer>> maybeResult = okResult.maybe(value -> value >= 0);
        //then
        assertThat(maybeResult.isOkay()).isTrue();
        maybeResult.match(
                success -> assertThat(success.toOptional()).contains(1),
                error -> fail("not an error")
        );
    }

    @Test
    public void successMaybe_whenFails_isSuccessNothing() {
        //given
        final Result<Integer> okResult = Result.ok(1);
        //when
        final Result<Maybe<Integer>> maybeResult = okResult.maybe(value -> value >= 4);
        //then
        assertThat(maybeResult.isOkay()).isTrue();
        maybeResult.match(
                success -> assertThat(success.toOptional()).isEmpty(),
                error -> fail("not an error")
        );
    }

    @Test
    public void errorMaybe_whenPasses_isError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> errorResult = Result.error(exception);
        //when
        final Result<Maybe<Integer>> maybeResult = errorResult.maybe(value -> value >= 0);
        //then
        assertThat(maybeResult.isError()).isTrue();
        maybeResult.match(
                success -> fail("not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void errorMaybe_whenFails_isError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> errorResult = Result.error(exception);
        //when
        final Result<Maybe<Integer>> maybeResult = errorResult.maybe(value -> value >= 4);
        //then
        assertThat(maybeResult.isError()).isTrue();
        maybeResult.match(
                success -> fail("not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void justMaybe_isSuccess() {
        //given
        final Maybe<Integer> just = Maybe.just(1);
        //when
        final Result<Integer> result = Result.fromMaybe(just, () -> new RuntimeException());
        //then
        assertThat(result.isOkay()).isTrue();
        result.match(
                success -> assertThat(success).isEqualTo(1),
                error -> fail("not an error")
        );
    }

    @Test
    public void nothingMaybe_isError() {
        //given
        final Maybe<Object> nothing = Maybe.nothing();
        final RuntimeException exception = new RuntimeException();
        //when
        final Result<Object> result = Result.fromMaybe(nothing, () -> exception);
        //then
        assertThat(result.isError()).isTrue();
        result.match(
                success -> fail("not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void success_toMaybe_isJust() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Maybe<Integer> maybe = Result.toMaybe(ok);
        //then
        assertThat(maybe.toOptional()).contains(1);
    }

    @Test
    public void error_toMaybe_isNothing() {
        //given
        final Result<Object> error = Result.error(new RuntimeException());
        //when
        final Maybe<Object> maybe = Result.toMaybe(error);
        //then
        assertThat(maybe.toOptional()).isEmpty();
    }

    @Test
    public void success_whenOrElseThrow_isValue() throws Throwable {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Integer value = ok.orElseThrow();
        //then
        assertThat(value).isEqualTo(1);
    }

    @Test
    public void error_whenOrElseThrow_throws() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> error = Result.error(exception);
        //when
        assertThatThrownBy(() -> error.orElseThrow())
                .isSameAs(exception);
    }

    @Test
    public void JustSuccess_invert_thenSuccessJust() {
        //given
        final Maybe<Result<Integer>> justSuccess = Maybe.just(Result.ok(1));
        //when
        final Result<Maybe<Integer>> result = Result.invert(justSuccess);
        //then
        result.match(
                success -> assertThat(success.toOptional()).contains(1),
                error -> fail("Not an error")
        );
    }

    @Test
    public void JustError_invert_thenError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Maybe<Result<Object>> justError = Maybe.just(Result.error(exception));
        //when
        final Result<Maybe<Object>> result = Result.invert(justError);
        //then
        result.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void Nothing_invert_thenSuccessNothing() {
        //given
        final Maybe<Result<Integer>> nothing = Maybe.nothing();
        //when
        final Result<Maybe<Integer>> result = Result.invert(nothing);
        //then
        result.match(
                success -> assertThat(success.toOptional()).isEmpty(),
                error -> fail("Not an error")
        );
    }

    @Test
    public void useCase_whenOkay_thenReturnSuccess() {
        //given
        final UseCase useCase = UseCase.isOkay();
        //when
        final Result<Double> doubleResult = useCase.businessOperation("file a", "file bc");
        //then
        assertThat(doubleResult.isOkay()).isTrue();
        doubleResult.match(
                success -> assertThat(success).isEqualTo(7.5),
                error -> fail("not an error")
        );
    }

    @Test
    public void useCase_whenFirstReadIsError_thenReturnError() {
        //given
        final UseCase useCase = UseCase.firstReadInvalid();
        //when
        final Result<Double> doubleResult = useCase.businessOperation("file def", "file ghij");
        //then
        assertThat(doubleResult.isOkay()).isFalse();
        doubleResult.match(
                success -> fail("not okay"),
                error -> assertThat(error)
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("file def")
        );
    }

    @Test
    public void useCase_whenSecondReadIsError_thenReturnError() {
        //given
        final UseCase useCase = UseCase.secondReadInvalid();
        //when
        final Result<Double> doubleResult = useCase.businessOperation("file klmno", "file pqrstu");
        //then
        assertThat(doubleResult.isOkay()).isFalse();
        doubleResult.match(
                success -> fail("not okay"),
                error -> assertThat(error)
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("file klmno")
        );
    }

    @Test
    public void success_toString() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final String toString = ok.toString();
        //then
        assertThat(toString).contains("Result.Success{value=1}");
    }

    @Test
    public void err_toString() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException("failed"));
        //when
        final String toString = error.toString();
        //then
        assertThat(toString).contains("Result.Error{error=java.lang.RuntimeException: failed}");
    }

    @Test
    public void resultOf_okay_isOkay() {
        //given
        final Callable<String> c = () -> "okay";
        //when
        final Result<String> result = Result.of(c);
        //then
        result.match(
                success -> assertThat(success).isEqualTo("okay"),
                error -> fail("not an error")
        );
    }

    @Test
    public void resultOf_error_isError() {
        //given
        final Callable<String> c = () -> {
            throw new IOException();
        };
        //when
        final Result<String> result = Result.of(c);
        //then
        result.match(
                success -> fail("not a success"),
                error -> assertThat(error).isInstanceOf(IOException.class)
        );
    }

    @Test
    public void success_peek_consumes() {
        //given
        final Result<Integer> result = Result.ok(1);
        final AtomicReference<Integer> consumed = new AtomicReference<>(0);
        //when
        final Result<Integer> peeked = result.peek(consumed::set);
        //then
        assertThat(consumed).hasValue(1);
        assertThat(peeked).isSameAs(result);
    }

    @Test
    public void error_peek_doesNothing() {
        //given
        final Result<Integer> result = Result.error(new RuntimeException());
        final AtomicReference<Integer> consumed = new AtomicReference<>(0);
        //when
        final Result<Integer> peeked = result.peek(consumed::set);
        //then
        assertThat(consumed).hasValue(0);
        assertThat(peeked).isSameAs(result);
    }

    @Test public void success_whenOnError_thenIgnore() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        ok.onError(e -> fail("not an error"));
    }

    @Test public void error_whenOnError_thenConsume() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> error = Result.error(exception);
        final AtomicReference<Throwable> capture = new AtomicReference<>();
        //when
        error.onError(capture::set);
        //then
        assertThat(capture).hasValue(exception);
    }

    @Test
    public void success_whenRecover_thenNoChange() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Result<Integer> recovered = ok.recover(e -> Result.ok(2));
        //then
        recovered.peek(v -> assertThat(v).isEqualTo(1));
    }

    @Test
    public void error_whenRecover_thenSuccess() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException());
        //when
        final Result<Integer> recovered = error.recover(e -> Result.ok(2));
        //then
        recovered.peek(v -> assertThat(v).isEqualTo(2));
    }

    @Test
    public void error_whenRecoverFails_thenUpdatedError() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException("original"));
        //when
        final Result<Integer> recovered = error.recover(e -> Result.error(new RuntimeException("updated")));
        //then
        recovered.onError(e -> assertThat(e).hasMessage("updated"));
    }

    @RequiredArgsConstructor
    private static class UseCase {

        private final Boolean okay;

        static UseCase isOkay() {
            return new UseCase(true);
        }

        static UseCase firstReadInvalid() {
            return new UseCase(false);
        }

        static UseCase secondReadInvalid() {
            return new UseCase(false);
        }

        Result<Double> businessOperation(final String fileName1, final String fileName2) {
            return readIntFromFile(fileName1).flatMap(intFromFile1 ->
                    adjustValue(intFromFile1).flatMap(adjustedIntFromFile1 ->
                            readIntFromFile(fileName2).flatMap(intFromFile2 ->
                                    calculateAverage(adjustedIntFromFile1, intFromFile2))));
        }

        private Result<Integer> readIntFromFile(final String fileName) {
            if (okay) {
                return Result.ok(fileName.length());
            }
            return Result.error(new RuntimeException(fileName));
        }

        private Result<Integer> adjustValue(final Integer value) {
            return Result.ok(value + 2);
        }

        private Result<Double> calculateAverage(final Integer val1, final Integer val2) {
            return Result.ok((double) (val1 + val2) / 2);
        }

    }
}