package net.kemitix.mon;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;
import net.kemitix.mon.result.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assumptions.assumeThat;

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
    public void successHashCodesAreUnique() {
        assertThat(Result.ok(1).hashCode()).isNotEqualTo(Result.ok(2).hashCode());
    }

    @Test
    public void errorHashCodesAreUnique() {
        // despite having 'equivalent' exceptions, the exceptions are distinct instances, so should be considered unique
        //given
        final RuntimeException exception1 = new RuntimeException("message");
        final RuntimeException exception2 = new RuntimeException("message");
        assumeThat(exception1.hashCode()).isNotEqualTo(exception2.hashCode());
        //then
        assertThat(Result.error(exception1).hashCode()).isNotEqualTo(Result.error(exception2).hashCode());
    }

    @Test
    public void whenOk_isOkay() {
        //when
        final Result<String> result = Result.ok("good");
        //then
        assertThat(result.isOkay()).isTrue();
    }

    @Test
    public void whenOkay_isNotError() {
        //when
        final Result<String> result = Result.ok("good");
        //then
        assertThat(result.isError()).isFalse();
    }

    @Test
    public void whenOkay_matchSuccess() {
        //given
        final Result<String> result = Result.ok("good");
        //then
        result.match(
                success -> assertThat(success).isEqualTo("good"),
                error -> fail("not an error")
        );
    }

    @Test
    public void whenError_isError() {
        //when
        final Result<String> result = Result.error(new Exception());
        //then
        assertThat(result.isOkay()).isFalse();
    }

    @Test
    public void whenError_isNotSuccess() {
        //when
        final Result<String> result = Result.error(new Exception());
        //then
        assertThat(result.isError()).isTrue();
    }

    @Test
    public void whenError_matchError() {
        //given
        final Result<Object> result = Result.error(new Exception("bad"));
        //then
        result.match(
                success -> fail("not a success"),
                error -> assertThat(error.getMessage()).isEqualTo("bad")
        );
    }

    @Test
    public void okay_whenFlatMapToOkay_isOkay() {
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
    public void okay_whenFlatMapToError_isError() {
        //given
        final Result<String> result = Result.ok("good");
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.error(new Exception("bad flat map")));
        //then
        assertThat(flatMap.isOkay()).isFalse();
    }

    @Test
    public void error_whenFlatMapToOkay_isError() {
        //given
        final Result<String> result = Result.error(new Exception("bad"));
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.ok(v.toUpperCase()));
        //then
        assertThat(flatMap.isError()).isTrue();
    }

    @Test
    public void error_whenFlatMapToError_isError() {
        //given
        final Result<String> result = Result.error(new Exception("bad"));
        //when
        final Result<String> flatMap = result.flatMap(v -> Result.error(new Exception("bad flat map")));
        //then
        assertThat(flatMap.isError()).isTrue();
    }

    @Test
    public void okay_whenMap_isOkay() {
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
    public void okay_whenMaybe_wherePasses_isOkayJust() {
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
    public void okay_whenMaybe_whereFails_isOkayNothing() {
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
    public void error_whenMaybe_wherePasses_isError() {
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
    public void error_whenMaybe_whereFails_isError() {
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
    public void just_whenFromMaybe_isOkay() {
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
    public void nothing_whenFromMaybe_isError() {
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
    public void okay_whenToMaybe_isJust() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Maybe<Integer> maybe = Result.toMaybe(ok);
        //then
        assertThat(maybe.toOptional()).contains(1);
    }

    @Test
    public void error_whenToMaybe_isNothing() {
        //given
        final Result<Object> error = Result.error(new RuntimeException());
        //when
        final Maybe<Object> maybe = Result.toMaybe(error);
        //then
        assertThat(maybe.toOptional()).isEmpty();
    }

    @Test
    public void okay_whenOrElseThrow_isValue() throws Throwable {
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
        assertThatThrownBy(() -> error.orElseThrow()).isSameAs(exception);
    }

    @Test
    public void justOkay_whenInvert_thenOkayJust() {
        //given
        final Maybe<Result<Integer>> justSuccess = Maybe.just(Result.ok(1));
        //when
        final Result<Maybe<Integer>> result = Result.swap(justSuccess);
        //then
        result.match(
                success -> assertThat(success.toOptional()).contains(1),
                error -> fail("Not an error")
        );
    }

    @Test
    public void JustError_whenInvert_isError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Maybe<Result<Object>> justError = Maybe.just(Result.error(exception));
        //when
        final Result<Maybe<Object>> result = Result.swap(justError);
        //then
        result.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void nothing_whenInvert_thenOkayNothing() {
        //given
        final Maybe<Result<Integer>> nothing = Maybe.nothing();
        //when
        final Result<Maybe<Integer>> result = Result.swap(nothing);
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
    public void okay_toString() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final String toString = ok.toString();
        //then
        assertThat(toString).contains("Result.Success{value=1}");
    }

    @Test
    public void error_toString() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException("failed"));
        //when
        final String toString = error.toString();
        //then
        assertThat(toString).contains("Result.Error{error=java.lang.RuntimeException: failed}");
    }

    @Test
    public void value_whenResultOf_isOkay() {
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
    public void exception_whenResultOf_isError() {
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
    public void okay_whenPeek_isConsumed() {
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
    public void error_whenPeek_isNotConsumed() {
        //given
        final Result<Integer> result = Result.error(new RuntimeException());
        final AtomicReference<Integer> consumed = new AtomicReference<>(0);
        //when
        final Result<Integer> peeked = result.peek(consumed::set);
        //then
        assertThat(consumed).hasValue(0);
        assertThat(peeked).isSameAs(result);
    }

    @Test
    public void okay_whenOnError_isIgnored() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        ok.onError(e -> fail("not an error"));
    }

    @Test
    public void error_whenOnError_isConsumed() {
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
    public void okay_whenRecover_thenNoChange() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Result<Integer> recovered = ok.recover(e -> Result.ok(2));
        //then
        assertThat(recovered).isSameAs(ok);
    }

    @Test
    public void error_whenRecover_isSuccess() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException());
        //when
        final Result<Integer> recovered = error.recover(e -> Result.ok(2));
        //then
        recovered.peek(v -> assertThat(v).isEqualTo(2));
    }

    @Test
    public void error_whenRecover_whereError_isUpdatedError() {
        //given
        final Result<Integer> error = Result.error(new RuntimeException("original"));
        //when
        final Result<Integer> recovered = error.recover(e -> Result.error(new RuntimeException("updated")));
        //then
        recovered.onError(e -> assertThat(e).hasMessage("updated"));
    }

    @Test
    public void okay_whenAndThen_whereSuccess_isUpdatedSuccess() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Result<String> result = ok.andThen(v -> () -> "success");
        //then
        assertThat(result.isOkay()).isTrue();
        result.peek(v -> assertThat(v).isEqualTo("success"));
    }

    @Test
    public void okay_whenAndThen_whereError_isError() {
        //given
        final Result<Integer> ok = Result.ok(1);
        final RuntimeException exception = new RuntimeException();
        //when
        final Result<Object> result = ok.andThen(v -> () -> {
            throw exception;
        });
        //then
        assertThat(result.isError()).isTrue();
        result.onError(e -> assertThat(e).isSameAs(exception));
    }

    @Test
    public void error_whereAndThen_whereSuccess_isError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Object> error = Result.error(exception);
        //when
        final Result<Object> result = error.andThen(v -> () -> "success");
        //then
        assertThat(result.isError()).isTrue();
        result.onError(e -> assertThat(e).isSameAs(exception));
    }

    @Test
    public void error_whenAndThen_whereError_isOriginalError() {
        //given
        final RuntimeException exception1 = new RuntimeException();
        final Result<Object> error = Result.error(exception1);
        //when
        final Result<Object> result = error.andThen(v -> () -> {
            throw new RuntimeException();
        });
        //then
        assertThat(result.isError()).isTrue();
        result.onError(e -> assertThat(e).isSameAs(exception1));
    }

    @Test
    public void okay_whenThenWith_whereOkay_isOriginalSuccess() {
        //given
        final Result<Integer> ok = Result.ok(1);
        //when
        final Result<Integer> result = ok.thenWith(v -> () -> {
            // do something with v
        });
        //then
        assertThat(result).isSameAs(ok);
    }

    @Test
    public void okay_whenThenWith_whereError_thenError() {
        //given
        final Result<Integer> ok = Result.ok(1);
        final RuntimeException exception = new RuntimeException();
        //when
        final Result<Integer> result = ok.thenWith(v -> () -> {
            throw exception;
        });
        //then
        assertThat(result.isError()).isTrue();
        result.onError(e -> assertThat(e).isSameAs(exception));
    }

    @Test
    public void error_whenThenWith_whereOkay_thenOriginalError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> error = Result.error(exception);
        //when
        final Result<Integer> result = error.thenWith(v -> () -> {
            // do something with v
        });
        //then
        assertThat(result).isSameAs(error);
    }

    @Test
    public void error_whenThenWith_whenError_thenOriginalError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> error = Result.error(exception);
        //when
        final Result<Integer> result = error.thenWith(v -> () -> {
            throw new RuntimeException();
        });
        //then
        assertThat(result).isSameAs(error);
    }

    @Test
    public void okayJust_whenFlatMapMaybe_whereOkayJust_thenIsOkayJust() {
        //given
        final Result<Maybe<Integer>> okJust = Result.ok(Maybe.just(1));
        //when
        final Result<Maybe<String>> result = Result.flatMapMaybe(okJust, maybe -> Result.ok(maybe.flatMap(v -> Maybe.just("2"))));
        //then
        result.match(
                success -> assertThat(success.toOptional()).contains("2"),
                error -> fail("Not an error")
        );
    }

    @Test
    public void okayJust_whenFlatMapMaybe_whereOkayNothing_thenIsOkayNothing() {
        //given
        final Result<Maybe<Integer>> okJust = Result.ok(Maybe.just(1));
        //when
        final Result<Maybe<String>> result = Result.flatMapMaybe(okJust, maybe -> Result.ok(maybe.flatMap(v -> Maybe.nothing())));
        //then
        result.match(
                success -> assertThat(success.toOptional()).isEmpty(),
                error -> fail("Not an error")
        );
    }

    @Test
    public void okayJust_whenFlatMapMaybe_whereError_thenIsError() {
        //given
        final Result<Maybe<Integer>> okJust = Result.ok(Maybe.just(1));
        final RuntimeException exception = new RuntimeException();
        //when
        final Result<Maybe<Integer>> result = Result.flatMapMaybe(okJust, v -> Result.error(exception));
        //then
        result.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void okayNothing_whenFlatMapMaybe_thenDoNotApply() {
        //given
        final Result<Maybe<Integer>> okNothing = Result.ok(Maybe.nothing());
        //when
        final Result<Maybe<String>> result = Result.flatMapMaybe(okNothing, maybe -> Result.ok(maybe.flatMap(v -> Maybe.just("2"))));
        //then
        result.match(
                success -> assertThat(success.toOptional()).isEmpty(),
                error -> fail("Not an error")
        );
    }

    @Test
    public void error_whenFlatMapMaybe_thenDoNotApply() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Maybe<Integer>> maybeResult = Result.error(exception);
        //when
        final Result<Maybe<String>> result = Result.flatMapMaybe(maybeResult, maybe -> Result.ok(maybe.flatMap(v -> Maybe.just("2"))));
        //then
        result.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void okayOkay_whenReduce_thenCombine() {
        //given
        final Result<Integer> result1 = Result.ok(1);
        final Result<Integer> result10 = Result.ok(10);
        //when
        final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
        //then
        result11.match(
                success -> assertThat(success).isEqualTo(11),
                error -> fail("Not an error")
        );
    }

    @Test
    public void okayError_whenReduce_thenError() {
        //given
        final Result<Integer> result1 = Result.ok(1);
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> result10 = Result.error(exception);
        //when
        final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
        //then
        result11.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void errorOkay_whenReduce_thenError() {
        //given
        final RuntimeException exception = new RuntimeException();
        final Result<Integer> result1 = Result.error(exception);
        final Result<Integer> result10 = Result.ok(10);
        //when
        final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
        //then
        result11.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception)
        );
    }

    @Test
    public void errorError_whenReduce_thenError() {
        //given
        final RuntimeException exception1 = new RuntimeException();
        final Result<Integer> result1 = Result.error(exception1);
        final RuntimeException exception10 = new RuntimeException();
        final Result<Integer> result10 = Result.error(exception10);
        //when
        final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
        //then
        result11.match(
                success -> fail("Not a success"),
                error -> assertThat(error).isSameAs(exception1)
        );
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
            return readIntFromFile(fileName1)
                    .andThen(intFromFile1 -> () -> adjustValue(intFromFile1))
                    .flatMap(adjustedIntFromFile1 -> readIntFromFile(fileName2)
                            .flatMap(intFromFile2 -> adjustedIntFromFile1
                                    .flatMap(aif1 -> calculateAverage(aif1, intFromFile2))));
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