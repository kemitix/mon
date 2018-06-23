package net.kemitix.mon;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;
import net.kemitix.mon.result.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class ResultTest implements WithAssertions {

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