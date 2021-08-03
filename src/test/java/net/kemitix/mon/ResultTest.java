package net.kemitix.mon;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.experimental.either.Either;
import net.kemitix.mon.maybe.Maybe;
import net.kemitix.mon.result.CheckedErrorResultException;
import net.kemitix.mon.result.ErrorResultException;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;
import net.kemitix.mon.result.SuccessVoid;
import net.kemitix.mon.result.UnexpectedErrorResultException;
import net.kemitix.mon.result.VoidCallable;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ResultTest implements WithAssertions {

    @Nested
    @DisplayName("Basic Properties")
    class BasicPropertiesTests {

        @Test
        @SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts"})
        void equality() {
            final RuntimeException runtimeException = new RuntimeException();
            // Success
            assertThat(Result.ok(1)).as("Success: same integer value").isEqualTo(Result.ok(1));
            assertThat(Result.ok(1)).as("Success: diff integer values").isNotEqualTo(Result.ok(2));
            assertThat(Result.ok(1).equals("1")).as("Success: string v integer").isFalse(); // NOPMD
            assertThat(Result.ok(1)).as("Success: success v error").isNotEqualTo(Result.error(runtimeException));
            // SuccessVoid
            assertThat(Result.ok()).as("SuccessVoid: void v void").isEqualTo(Result.ok());
            assertThat(Result.ok().hashCode()).as("SuccessVoid: hash v void").isNotNull();
            assertThat(Result.ok()).as("SuccessVoid: void v integer").isNotEqualTo(Result.ok(1));
            assertThat(Result.ok()).as("SuccessVoid: v ErrorVoid").isNotEqualTo(Result.error(runtimeException));
            // Error
            TypeReference<Integer> integerReference = TypeReference.create();
            assertThat(Result.error(integerReference, runtimeException)).as("error v error").isEqualTo(Result.error(integerReference, runtimeException));
            assertThat(Result.error(integerReference, runtimeException)).as("error v other error").isNotEqualTo(Result.error(integerReference, new RuntimeException()));
            assertThat(Result.error(integerReference, runtimeException)).as("error v string").isNotEqualTo("1");
            // ErrorVoid
            assertThat(Result.error(runtimeException)).as("same error value").isEqualTo(Result.error(runtimeException));
            assertThat(Result.error(runtimeException)).as("diff error values").isNotEqualTo(Result.error(new RuntimeException()));
            assertThat(Result.error(new RuntimeException()).equals("1")).as("error v string").isFalse(); // NOPMD
        }

        @Test
        void successHashCodesAreUnique() {
            assertThat(Result.ok(1).hashCode()).isNotEqualTo(Result.ok(2).hashCode());
        }

        @Test
        void errorHashCodesAreUnique() {
            // despite having 'equivalent' exceptions, the exceptions are distinct instances, so should be considered unique
            //given
            final RuntimeException exception1 = new RuntimeException("message");
            final RuntimeException exception2 = new RuntimeException("message");
            assumeThat(exception1.hashCode()).isNotEqualTo(exception2.hashCode());
            //then
            assertThat(Result.error(exception1).hashCode()).isNotEqualTo(Result.error(exception2).hashCode());
        }

        @Test
        void errorVHashCodesAreUnique() {
            // despite having 'equivalent' exceptions, the exceptions are distinct instances, so should be considered unique
            //given
            final RuntimeException exception1 = new RuntimeException("message");
            final RuntimeException exception2 = new RuntimeException("message");
            assumeThat(exception1.hashCode()).isNotEqualTo(exception2.hashCode());
            TypeReference<Integer> integerReference = TypeReference.create();
            //then
            assertThat(Result.error(integerReference, exception1).hashCode())
                    .isNotEqualTo(Result.error(integerReference, exception2).hashCode());
        }

        @Test
        void whenOkVoid_isOkay() {
            //when
            var result = Result.ok();
            //then
            assertThat(result.isOkay()).isTrue();
        }

        @Test
        void whenOkVoid_isError() {
            //when
            var result = Result.ok();
            //then
            assertThat(result.isError()).isFalse();
        }

        @Test
        void whenOkayVoid_match_isNull() {
            //when
            var result = Result.ok();
            //then
            result.match(
                    () -> assertThat(true).isTrue(),
                    error -> fail("not an error")
            );
        }

        @Test
        void whenOk_isOkay() {
            //when
            final Result<String> result = Result.ok("good");
            //then
            assertThat(result.isOkay()).isTrue();
        }

        @Test
        void whenOkay_isNotError() {
            //when
            final Result<String> result = Result.ok("good");
            //then
            assertThat(result.isError()).isFalse();
        }

        @Test
        void whenOkay_matchSuccess() {
            //given
            final Result<String> result = Result.ok("good");
            //then
            result.match(
                    success -> assertThat(success).isEqualTo("good"),
                    error -> fail("not an error")
            );
        }

        @Test
        void whenError_isError() {
            //when
            final Result<Integer> result = anError(new Exception());
            //then
            assertThat(result.isOkay()).isFalse();
        }

        @Test
        void whenError_isNotSuccess() {
            //when
            final Result<Integer> result = anError(new Exception());
            //then
            assertThat(result.isError()).isTrue();
        }

        @Test
        void whenError_matchError() {
            //given
            final Result<Integer> result = anError(new Exception("bad"));
            //then
            result.match(
                    success -> fail("not a success"),
                    error -> assertThat(error.getMessage()).isEqualTo("bad")
            );
        }

        @Test
        void okay_toString() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final String toString = ok.toString();
            //then
            assertThat(toString).contains("Result.Success{value=1}");
        }

        @Test
        void okayVoid_toString() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            final String toString = ok.toString();
            //then
            assertThat(toString).contains("Result.SuccessVoid{}");
        }

        @Test
        void error_toString() {
            //given
            final Result<Integer> error = anError(new RuntimeException("failed"));
            //when
            final String toString = error.toString();
            //then
            assertThat(toString).contains("Result.Error{error=java.lang.RuntimeException: failed}");
        }

        @Test
        void errorVoid_toString() {
            //given
            final ResultVoid error = Result.error(new RuntimeException("failed"));
            //when
            final String toString = error.toString();
            //then
            assertThat(toString).contains("Result.ErrorVoid{error=java.lang.RuntimeException: failed}");
        }

        @Test
        void value_whenResultOf_isOkay() {
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
        void exception_whenResultOf_isError() {
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
    }

    private Result<Integer> anError(Exception e) {
        return Result.ok(1)
                .flatMap(s -> Result.of(() -> {throw e;}));
    }

    @Nested
    @DisplayName("flatMap")
    class FlatMapTests {
        @Test
        void okay_whenFlatMapToOkay_isOkay() {
            //given
            final Result<String> ok = Result.ok("good");
            //when
            final Result<String> result = ok.flatMap(v -> Result.ok(v.toUpperCase()));
            //then
            result.match(
                    success -> assertThat(success).isEqualTo("GOOD"),
                    error -> fail("not an error")
            );
        }

        @Test
        void okay_whenFlatMapToError_isError() {
            //given
            final Result<String> result = Result.ok("good");
            //when
            final Result<Integer> flatMap = result.flatMap(v -> anError(new Exception("bad flat map")));
            //then
            assertThat(flatMap.isOkay()).isFalse();
        }

        @Test
        void error_whenFlatMapToOkay_isError() {
            //given
            final Result<Integer> result = anError(new Exception("bad"));
            //when
            final Result<Object> flatMap = result.flatMap(Result::ok);
            //then
            assertThat(flatMap.isError()).isTrue();
        }

        @Test
        void error_whenFlatMapToError_isError() {
            //given
            final Result<Integer> result = anError(new Exception("bad"));
            //when
            final Result<Integer> flatMap = result.flatMap(v -> anError(new Exception("bad flat map")));
            //then
            assertThat(flatMap.isError()).isTrue();
        }
    }

    @Nested
    @DisplayName("flatMapV")
    class FlatMapVTests {
        @Test
        void okay_whenFlatMapVToOkay_isOkayVoid() {
            //given
            final Result<String> result = Result.ok("good");
            //when
            final ResultVoid flatMap = result.flatMapV(v -> Result.ok());
            //then
            flatMap.match(
                    () -> assertThat(true).isTrue(),
                    error -> fail("not an error")
            );
        }

        @Test
        void okay_whenFlatMapVToError_isErrorVoid() {
            //given
            final Result<String> result = Result.ok("good");
            //when
            final ResultVoid flatMap = result.flatMapV(v -> Result.error(new Exception("bad flat map")));
            //then
            assertThat(flatMap.isOkay()).isFalse();
        }

        @Test
        void error_whenFlatMapVToOkay_isErrorVoid() {
            //given
            final Result<Integer> result = anError(new Exception("bad"));
            //when
            final ResultVoid flatMap = result.flatMapV(value -> Result.ok());
            //then
            assertThat(flatMap.isError()).isTrue();
        }

        @Test
        void error_whenFlatMapVToError_isErrorVoid() {
            //given
            final Result<Integer> result = anError(new Exception("bad"));
            //when
            final ResultVoid flatMap = result.flatMapV(v -> Result.error(new Exception("bad flat map")));
            //then
            assertThat(flatMap.isError()).isTrue();
        }
    }

    @Nested
    @DisplayName("map")
    class MapTest {
        @Test
        void okay_whenMapToOkay_isOkay() {
            //given
            final Result<Integer> okResult = Result.ok(1);
            //when
            final Result<String> result = okResult.map(value -> String.valueOf(value));
            //then
            result.match(
                    success -> assertThat(success).isEqualTo("1"),
                    error -> fail("not an error")
            );
        }

        @Test
        void okay_whenMapToError_isError() {
            //given
            final Result<Integer> okResult = Result.ok(1);
            //when
            final Result<String> result = okResult.map(value -> {
                throw new RuntimeException("map error");
            });
            //then
            result.match(
                    success -> fail("not an success"),
                    error -> assertThat(error).hasMessage("map error")
            );
        }

        @Test
        void error_whenMap_isError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> errorResult = anError(exception);
            //when
            final Result<String> result = errorResult.map(value -> String.valueOf(value));
            //then
            result.match(
                    success -> fail("not an success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }
    }

    @Nested
    @DisplayName("maybe")
    class MaybeTests {

        @Nested
        @DisplayName("fromMaybe")
        class FromMaybeTests {

            @Test
            void just_whenFromMaybe_isOkay() {
                //given
                final Maybe<Integer> just = Maybe.just(1);
                //when
                final Result<Integer> result = Result.from(just, () -> new RuntimeException());
                //then
                result.match(
                        success -> assertThat(success).isEqualTo(1),
                        error -> fail("not an error")
                );
            }

            @Test
            void nothing_whenFromMaybe_isError() {
                //given
                final Maybe<Object> nothing = Maybe.nothing();
                final RuntimeException exception = new RuntimeException();
                //when
                final Result<Object> result = Result.from(nothing, () -> exception);
                //then
                result.match(
                        success -> fail("not a success"),
                        error -> assertThat(error).isSameAs(exception)
                );
            }
        }

        @Nested
        @DisplayName("toMaybe")
        class ToMaybeTests {

            @Test
            void okay_whenToMaybe_isJust() {
                //given
                final Result<Integer> ok = Result.ok(1);
                //when
                final Maybe<Integer> maybe = Result.toMaybe(ok);
                //then
                assertThat(maybe.toOptional()).contains(1);
            }

            @Test
            void error_whenToMaybe_isNothing() {
                //given
                final Result<Integer> error = anError(new RuntimeException());
                //when
                final Maybe<Integer> maybe = Result.toMaybe(error);
                //then
                assertThat(maybe.toOptional()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("orElseThrow")
    class OrElseThrowTests {
        @Test
        void okay_whenOrElseThrow_isValue() throws Throwable {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final Integer value = ok.orElseThrow();
            //then
            assertThat(value).isEqualTo(1);
        }

        @Test
        void error_whenOrElseThrow_throws() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            assertThatThrownBy(() -> error.orElseThrow())
                    .isInstanceOf(CheckedErrorResultException.class)
                    .hasCause(exception);
        }

        @Test
        void okay_whenOrElseThrowT_isValue() throws Exception {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final Integer value = ok.orElseThrow(Exception.class);
            //then
            assumeThat(value).isEqualTo(1);
        }

        @Test
        void errorT_whenOrElseThrowT_throwsT() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //then
            assertThatThrownBy(() -> error.orElseThrow(RuntimeException.class)).isSameAs(exception);
        }

        @Test
        void errorR_whenOrElseThrowT_throwsWrappedR() {
            //given
            final IOException exception = new IOException();
            final Result<Integer> error = anError(exception);
            //then
            assertThatThrownBy(() -> error.orElseThrow(RuntimeException.class))
                    .isInstanceOf(UnexpectedErrorResultException.class)
                    .hasCause(exception);
        }
    }

    @Nested
    @DisplayName("orElseThrowUnchecked")
    class OrElseThrowUncheckedTests {
        @Test
        void okay_whenOrElseThrowUnchecked_isValue() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final Integer value = ok.orElseThrowUnchecked();
            //then
            assumeThat(value).isEqualTo(1);
        }

        @Test
        void error_whenOrElseThrowUnchecked_throwsWrapped() {
            //given
            final IOException exception = new IOException();
            final Result<Integer> error = anError(exception);
            //then
            assertThatThrownBy(() -> error.orElseThrowUnchecked())
                    .isInstanceOf(ErrorResultException.class)
                    .hasCause(exception);
        }
    }

    @Nested
    @DisplayName("invert")
    class InvertTests {
        @Test
        void justOkay_whenInvert_thenOkayJust() {
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
        void JustError_whenInvert_isError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Maybe<Result<Integer>> justError = Maybe.just(anError(exception));
            //when
            final Result<Maybe<Integer>> result = Result.swap(justError);
            //then
            result.match(
                    success -> fail("Not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Test
        void nothing_whenInvert_thenOkayNothing() {
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
    }

    @Nested
    @DisplayName("use cases")
    class UseCaseTests {
        @Test
        void useCase_whenOkay_thenReturnSuccess() {
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
        void useCase_whenFirstReadIsError_thenReturnError() {
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
        void useCase_whenSecondReadIsError_thenReturnError() {
            //given
            final UseCase useCase = UseCase.secondReadInvalid();
            //when
            final Result<Double> doubleResult = useCase.businessOperation("file klmno", "file pqrstu");
            //then
            doubleResult.match(
                    success -> fail("not okay"),
                    error -> assertThat(error)
                            .isInstanceOf(RuntimeException.class)
                            .hasMessage("file klmno")
            );
        }
    }

    @Nested
    @DisplayName("peek")
    class PeekTests {
        @Test
        void okay_whenPeek_isConsumed() {
            //given
            final Result<Integer> okay = Result.ok(1);
            final AtomicBoolean consumed = new AtomicBoolean(false);
            //when
            okay.peek(v -> consumed.set(true));
            //then
            assertThat(consumed).isTrue();
        }

        @Test
        void okay_whenPeek_isOriginal() {
            //given
            final Result<Integer> okay = Result.ok(1);
            final AtomicBoolean consumed = new AtomicBoolean(false);
            //when
            final Result<Integer> result = okay.peek(v -> consumed.set(true));
            //then
            assertThat(result).isSameAs(okay);
        }

        @Test
        void error_whenPeek_isNotConsumed() {
            //given
            final Result<Integer> error = anError(new RuntimeException());
            final AtomicBoolean consumed = new AtomicBoolean(false);
            //when
            error.peek(newValue -> consumed.set(true));
            //then
            assertThat(consumed).isFalse(); // peek should not occur
        }
        @Test
        void error_whenPeek_isSelf() {
            //given
            final Result<Integer> error = anError(new RuntimeException());
            final AtomicBoolean consumed = new AtomicBoolean(false);
            //when
            final Result<Integer> result = error.peek(v -> consumed.set(true));
            //then
            assertThat(result).isSameAs(error);
        }
    }

    @Nested
    @DisplayName("onError - all error")
    class OnErrorTests {
        @Test
        void okay_whenOnError_isIgnored() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            ok.onError(e -> fail("not an error"));
        }

        @Test
        void error_whenOnError_isConsumed() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(capture::set);
            //then
            assertThat(capture).hasValue(exception);
        }

        @Test
        void okayVoid_whenOnError_isIgnored() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            ok.onError(e -> fail("not an error"));
        }

        @Test
        void errorVoid_whenOnError_isConsumed() {
            //given
            final RuntimeException exception = new RuntimeException();
            final ResultVoid error = Result.error(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(capture::set);
            //then
            assertThat(capture).hasValue(exception);
        }
    }

    @Nested
    @DisplayName("onError - by type")
    class OnErrorByTypeTests {

        @Test
        @DisplayName("okay when on error is ignored")
        void okay_whenOnError_isIgnored() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            ok.onError(Throwable.class,
                    e -> fail("not an error"));
        }

        @Test
        @DisplayName("error with matching type is consumed")
        void error_whenOnError_isConsumed() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(RuntimeException.class,
                    capture::set);
            //then
            assertThat(capture).hasValue(exception);
        }

        @Test
        @DisplayName("error with non-matching type is ignored")
        void error_withNoMatch_whenOnError_isIgnored() {
            //given
            RuntimeException exception = new RuntimeException();
            Result<Integer> error = anError(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(Exception.class,
                    capture::set);
            //then
            assertThat(capture).hasValue(null);
        }

        @Test
        @DisplayName("okay void when on error is ignored")
        void okayVoid_whenOnError_isIgnored() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            ok.onError(Throwable.class,
                    e -> fail("not an error"));
        }

        @Test
        @DisplayName("error void with matching type is consumed")
        void errorVoid_whenOnError_isConsumed() {
            //given
            final RuntimeException exception = new RuntimeException();
            final ResultVoid error = Result.error(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(RuntimeException.class,
                    capture::set);
            //then
            assertThat(capture).hasValue(exception);
        }

        @Test
        @DisplayName("error void with non-matching type is ignored")
        void errorVoid_withNoMatch_whenOnError_isIgnored() {
            //given
            RuntimeException exception = new RuntimeException();
            ResultVoid error = Result.error(exception);
            final AtomicReference<Throwable> capture = new AtomicReference<>();
            //when
            error.onError(Exception.class,
                    capture::set);
            //then
            assertThat(capture).hasValue(null);
        }
    }

    @Nested
    @DisplayName("onSuccess")
    class OnSuccessTests {
        @Test
        void error_whenOnSuccess_isIgnored() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            error.onSuccess(e -> fail("not a success"));
        }

        @Test
        void okay_whenOnSuccess_isConsumed() {
            //given
            final AtomicReference<Integer> capture = new AtomicReference<>();
            final Result<Integer> ok = Result.ok(1);
            //when
            ok.onSuccess(capture::set);
            //then
            assertThat(capture).hasValue(1);
        }

        @Test
        void errorVoid_whenOnSuccess_isIgnored() {
            //given
            final RuntimeException exception = new RuntimeException();
            final ResultVoid error = Result.error(exception);
            //when
            error.onSuccess(() -> fail("not a success"));
        }

        @Test
        void okayVoid_whenOnSuccess_isConsumed() {
            //given
            final AtomicReference<Integer> capture = new AtomicReference<>();
            final ResultVoid ok = Result.ok();
            //when
            ok.onSuccess(() -> capture.set(1));
            //then
            assertThat(capture).hasValue(1);
        }
    }

    @Nested
    @DisplayName("recover")
    class RecoverTests {
        @Nested @DisplayName("Result")
        class ResultRecoveryTests {
            @Test
            void okay_whenRecover_thenNoChange() {
                //given
                final Result<Integer> ok = Result.ok(1);
                //when
                final Result<Integer> recovered = ok.recover(e -> Result.ok(2));
                //then
                assertThat(recovered).isSameAs(ok);
            }

            @Test
            void error_whenRecover_isSuccess() {
                //given
                final Result<Integer> error = anError(new RuntimeException());
                //when
                // recover can't change the type of the result
                final Result<Integer> recovered = error.recover(e -> Result.ok(2));
                //then
                recovered.peek(v -> assertThat(v).isEqualTo(2));
            }

            @Test
            void error_whenRecover_whereError_isUpdatedError() {
                //given
                final Result<Integer> error = anError(new RuntimeException("original"));
                //when
                final Result<Integer> recovered = error.recover(e -> anError(new RuntimeException("updated")));
                //then
                recovered.onError(e -> assertThat(e).hasMessage("updated"));
            }
        }
        @Nested @DisplayName("ResultVoid")
        class ResultVoidRecoveryTests {
            @Test
            void okayVoid_whenRecover_thenNoChange() {
                //given
                final ResultVoid ok = Result.ok();
                //when
                final ResultVoid recovered = ok.recover(e -> Result.ok());
                //then
                assertThat(recovered).isSameAs(ok);
            }

            @Test
            void error_whenRecover_isSuccess() {
                //given
                final ResultVoid error = Result.error(new RuntimeException());
                //when
                // recover can't change the type of the result
                final ResultVoid recovered = error.recover(e -> Result.ok());
                //then
                assertThat(recovered.isOkay()).isTrue();
            }

            @Test
            void error_whenRecover_whereError_isUpdatedError() {
                //given
                final ResultVoid error = Result.error(new RuntimeException("original"));
                //when
                final ResultVoid recovered = error.recover(e -> Result.error(new RuntimeException("updated")));
                //then
                recovered.onError(e -> assertThat(e).hasMessage("updated"));
            }
        }
    }

    @Nested
    @DisplayName("inject (recover for ResultVoid)")
    class InjectTests {
        @Test
        @DisplayName("SuccessVoid#inject is Success with Value")
        void okayVoid_whenInject_isOkayValue() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            final Result<Integer> result = ok.inject(() -> 1);
            //then
            assertThat(result).isEqualTo(Result.ok(1));
        }

        @Test
        @DisplayName("okayVoid when Inject fails then error")
        void okayVoid_whenInjectIsError_isError() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            final Result<Integer> result = ok.inject(() -> {throw new RuntimeException();});
            //then
            assertThat(result.isError()).isTrue();
        }

        @Test
        @DisplayName("errorVoid when inject is Success with Value")
        void errorVoid_whenInject_isInjectedValue() {
            //given
            final ResultVoid error = Result.error(new RuntimeException());
            //when
            final Result<Integer> result = error.inject(() -> 1);
            //then
            assertThat(result).isEqualTo(Result.ok(1));
        }

        @Test
        @DisplayName("errorVoid when Inject fails then original error")
        void errorVoid_whenInjectIsError_isOriginalError() {
            //given
            RuntimeException exception = new RuntimeException();
            final ResultVoid error = Result.error(exception);
            //when
            final Result<Integer> result = error.inject(() -> {throw new RuntimeException();});
            //then
            result.match(
                    x -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

    }

    @Nested
    @DisplayName("andThen")
    class AndThenTests {
        @Test
        void okay_whenAndThen_whereSuccess_isUpdatedSuccess() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final Result<String> result = ok.andThen(v -> () -> "success");
            //then
            result.match(
                    v -> assertThat(v).isEqualTo("success"),
                    e -> fail("not an error"));
        }

        @Test
        void okay_whenAndThen_whereError_isError() {
            //given
            final Result<Integer> ok = Result.ok(1);
            final RuntimeException exception = new RuntimeException();
            //when
            final Result<Object> result = ok.andThen(v -> () -> {
                throw exception;
            });
            //then
            result.match(
                    x -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void error_whereAndThen_whereSuccess_isError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            final Result<Object> result = error.andThen(v -> () -> "success");
            //then
            result.match(
                    x -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void error_whenAndThen_whereError_isOriginalError() {
            //given
            final RuntimeException exception1 = new RuntimeException();
            final Result<Integer> error = anError(exception1);
            //when
            final Result<Object> result = error.andThen(v -> () -> {
                throw new RuntimeException();
            });
            //then
            result.match(
                    x -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception1));
        }

        @Test
        void okayVoid_whenAndThen_whereSuccess_isUpdatedSuccess() {
            //given
            final ResultVoid ok = Result.ok();
            //when
            final ResultVoid result = ok.andThen(() -> {
                // do nothing
            });
            //then
            assertThat(result.isOkay()).isTrue();
        }

        @Test
        void okayVoid_whenAndThen_whereError_isError() {
            //given
            final ResultVoid ok = Result.ok();
            final RuntimeException exception = new RuntimeException();
            //when
            final ResultVoid result = ok.andThen(() -> {
                throw exception;
            });
            //then
            result.match(
                    () -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void errorVoid_whereAndThen_whereSuccess_isError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final ResultVoid error = Result.error(exception);
            //when
            final ResultVoid result = error.andThen(() -> {
                // do nothing
            });
            //then
            result.match(
                    () -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void errorVoid_whenAndThen_whereError_isOriginalError() {
            //given
            final RuntimeException exception1 = new RuntimeException();
            final ResultVoid error = Result.error(exception1);
            //when
            final ResultVoid result = error.andThen(() -> {
                throw new RuntimeException();
            });
            //then
            result.match(
                    () -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception1));
        }
    }

    @Nested
    @DisplayName("thenWith")
    class ThenWithTests {
        @Test
        void okay_whenThenWith_whereOkay_isOriginalSuccess() {
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
        void okay_whenThenWith_whereError_thenError() {
            //given
            final Result<Integer> ok = Result.ok(1);
            final RuntimeException exception = new RuntimeException();
            //when
            final Result<Integer> result = ok.thenWith(v -> () -> {
                throw exception;
            });
            //then
            result.match(
                    x -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void error_whenThenWith_whereOkay_thenOriginalError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            final Result<Integer> result = error.thenWith(v -> () -> {
                // do something with v
            });
            //then
            assertThat(result).isSameAs(error);
        }

        @Test
        void error_whenThenWith_whenError_thenOriginalError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            final Result<Integer> result = error.thenWith(v -> () -> {
                throw new RuntimeException();
            });
            //then
            assertThat(result).isSameAs(error);
        }

    }

    @Nested
    @DisplayName("thenWithV")
    class ThenWithVTests {
        @Test
        void okay_whenThenWithV_whereOkay_isSuccessVoid() {
            //given
            final Result<Integer> ok = Result.ok(1);
            //when
            final ResultVoid result = ok.thenWithV(v -> () -> {
                // do something with v
            });
            //then
            assertThat(result.isOkay()).isTrue();
        }

        @Test
        void okay_whenThenWithV_whereError_thenErrorVoid() {
            //given
            final Result<Integer> ok = Result.ok(1);
            final RuntimeException exception = new RuntimeException();
            //when
            final ResultVoid result = ok.thenWithV(v -> () -> {
                throw exception;
            });
            //then
            result.match(
                    () -> fail("not a success"),
                    e -> assertThat(e).isSameAs(exception));
        }

        @Test
        void error_whenThenWithV_whereOkay_thenErrorVoid() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            final ResultVoid result = error.thenWithV(v -> () -> {
                // do something with v
            });
            //then
            assertThat(result.isError()).isTrue();
        }

        @Test
        void error_whenThenWithV_whenError_thenErrorVoid() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> error = anError(exception);
            //when
            final ResultVoid result = error.thenWithV(v -> () -> {
                throw new RuntimeException();
            });
            //then
            assertThat(result.isError()).isTrue();
        }

    }

    @Nested
    @DisplayName("flatMapMaybe")
    class FlatMapMaybeTests {
        @Test
        void okayJust_whenFlatMapMaybe_whereOkayJust_thenIsOkayJust() {
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
        void okayJust_whenFlatMapMaybe_whereOkayNothing_thenIsOkayNothing() {
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
        void okayJust_whenFlatMapMaybe_whereError_thenIsError() {
            //given
            final Result<Maybe<Integer>> okJust = Result.ok(Maybe.just(1));
            final RuntimeException exception = new RuntimeException();
            //when
            final Result<Maybe<Integer>> result = Result.flatMapMaybe(okJust, v ->
                    Result.error(TypeReference.create(), exception));
            //then
            result.match(
                    success -> fail("Not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Test
        void okayNothing_whenFlatMapMaybe_thenDoNotApply() {
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
        void error_whenFlatMapMaybe_thenDoNotApply() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Maybe<Integer>> maybeResult = Result.error(TypeReference.create(), exception);
            //when
            final Result<Maybe<String>> result = Result.flatMapMaybe(maybeResult, maybe -> Result.ok(maybe.flatMap(v -> Maybe.just("2"))));
            //then
            result.match(
                    success -> fail("Not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }
    }

    @Nested
    @DisplayName("reduce")
    class ReduceTests {
        @Test
        void okayOkay_whenReduce_thenCombine() {
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
        void okayError_whenReduce_thenError() {
            //given
            final Result<Integer> result1 = Result.ok(1);
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> result10 = anError(exception);
            //when
            final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
            //then
            result11.match(
                    success -> fail("Not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Test
        void errorOkay_whenReduce_thenError() {
            //given
            final RuntimeException exception = new RuntimeException();
            final Result<Integer> result1 = anError(exception);
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
        void errorError_whenReduce_thenError() {
            //given
            final RuntimeException exception1 = new RuntimeException();
            final Result<Integer> result1 = anError(exception1);
            final RuntimeException exception10 = new RuntimeException();
            final Result<Integer> result10 = anError(exception10);
            //when
            final Result<Integer> result11 = result1.reduce(result10, (a, b) -> a + b);
            //then
            result11.match(
                    success -> fail("Not a success"),
                    error -> assertThat(error).isSameAs(exception1)
            );
        }
    }

    @Nested
    @DisplayName("applyOver - Result over a set")
    class ApplyOverTests {

        @Test
        @DisplayName("Empty List is Okay")
        void emptyListIsOkay() {
            //given
            var stream = Stream.empty();
            Consumer<Object> doNothingWithoutError = x -> {};
            //when
            var result = Result.applyOver(stream, doNothingWithoutError);
            //then
            assertThat(result.isOkay()).isTrue();
        }

        @Test
        @DisplayName("Single item list with valid result - is valid result")
        void singleItemValidIsValid() {
            //given
            var stream = Stream.of("foo");
            //when
            var result = Result.applyOver(stream, String::length, 0, Integer::sum);
            //then
            result.match(
                    success -> assertThat(success).isEqualTo(3),
                    error -> fail("not an error")
            );
        }

        @Test
        @DisplayName("Two item list consumed without error - is valid result")
        void twoItemsConsumedIsValid() {
            //given
            var acc = new AtomicInteger(0);
            var stream = Stream.of(1, 2);
            Consumer<Integer> accumulate = x -> acc.accumulateAndGet(x, Integer::sum);
            //when
            var result = Result.applyOver(stream, accumulate);
            //then
            result.match(
                    () -> assertThat(acc).hasValue(3),
                    e -> fail("should pass")
            );
        }

        @Test
        @DisplayName("Two item list with valid results - is valid result")
        void twoItemsValidIsValid() {
            //given
            var stream = Stream.of("aaa", "bb");
            //when
            var result = Result.applyOver(stream, String::length, 0, Integer::sum);
            //then
            result.match(
                    success -> assertThat(success).isEqualTo(5),
                    error -> fail("not an error")
            );
        }

        @Test
        @DisplayName("Single item list with error is error")
        void singleItemErrorIsError() {
            //given
            var stream = Stream.of("error");
            var exception = new RuntimeException();
            //when
            var result = Result.applyOver(stream, s -> {
                throw exception;
            }, 0, Integer::sum);
            //then
            result.match(
                    success -> fail("not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Nested @DisplayName("Two item list with two errors")
        class TwoErrorsTests {

            Stream<String> stream = Stream.of("ccc", "ddd");
            List<String> processed = new ArrayList<>();
            Result<Integer> result = Result.applyOver(stream, s -> {
                processed.add(s);
                throw new RuntimeException(s);
            }, 0, Integer::sum);

            @Test @DisplayName("is first error")
            void isFirstError() {
                result.match(
                        success -> fail("not a success"),
                        error -> assertThat(error).hasMessage("ccc")
                );
            }
            @Test
            @DisplayName("processing stopped after first error")
            void stoppedOnFirstError() {
                assertThat(processed).contains("ccc");
            }

            @Test @DisplayName("second item is not consumed")
            void secondErrorNotConsumed() {
                assertThat(processed).doesNotContain("ddd");
            }
        }

        @Nested @DisplayName("Two item list with okay then error")
        class TwoItemsOkayThenErrorTests {

            Stream<String> stream = Stream.of("ccccc", "ddd");
            List<String> processed = new ArrayList<>();
            Result<Integer> result = Result.applyOver(stream, s -> {
                processed.add(s);
                if ("ddd".equals(s)) {
                    throw new RuntimeException(s);
                }
                return s.length();
            }, 0, Integer::sum);

            @Test @DisplayName("is error")
            void isError() {
                result.match(
                        success -> fail("not a success"),
                        error -> assertThat(error).hasMessage("ddd")
                );
            }

        }
    }

    @Nested
    @DisplayName("flatApplyOver")
    class flatApplyOverTests {

        @Test
        @DisplayName("Single item list with valid result - is valid result")
        void singleItemValidIsValid() {
            //given
            var stream = Stream.of("foo");
            Function<String, Result<Integer>> f = s -> Result.ok(s.length());
            //when
            var result = Result.flatApplyOver(stream,
                    f, 0, Integer::sum);
            //then
            result.match(
                    success -> assertThat(success).isEqualTo(3),
                    error -> fail("not an error")
            );
        }

        @Test
        @DisplayName("Two item list with valid results - is valid result")
        void twoItemsValidIsValid() {
            //given
            var stream = Stream.of("aaa", "bb");
            Function<String, Result<Integer>> f = s -> Result.ok(s.length());
            //when
            var result = Result.flatApplyOver(stream, f, 0, Integer::sum);
            //then
            result.match(
                    success -> assertThat(success).isEqualTo(5),
                    error -> fail("not an error")
            );
        }

        @Test
        @DisplayName("Single item list with error is error")
        void singleItemErrorIsError() {
            //given
            var stream = Stream.of("error");
            var exception = new RuntimeException();
            Function<String, Result<Integer>> f = s -> anError(exception);
            //when
            var result = Result.flatApplyOver(stream, f, 0, Integer::sum);
            //then
            result.match(
                    success -> fail("not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Nested @DisplayName("Two item list with two errors")
        class TwoErrorsTests {

            Stream<String> stream = Stream.of("ccc", "ddd");
            List<String> processed = new ArrayList<>();
            Function<String, Result<Integer>> f = s -> Result.of(() -> {
                processed.add(s);
                throw new RuntimeException(s);
            });
            Result<Integer> result = Result.flatApplyOver(stream, f, 0, Integer::sum);

            @Test
            @DisplayName("error in result is the first error raised")
            void isFirstError() {
                result.match(
                        success -> fail("not a success"),
                        error -> assertThat(error).hasMessage("ccc")
                );
            }
            @Test
            @DisplayName("Processing stopped after first error")
            void stoppedOnFirstError() {
                assertThat(processed).contains("ccc");
            }

            @Test @DisplayName("second item is not consumed")
            void secondErrorNotConsumed() {
                assertThat(processed).doesNotContain("ddd");
            }
        }

        @Nested @DisplayName("Two item list with okay then error")
        class TwoItemsOkayTheErrorTests {

            Stream<String> stream = Stream.of("ccccc", "ddd");
            List<String> processed = new ArrayList<>();
            Function<String, Result<Integer>> f = s -> Result.of(() -> {
                processed.add(s);
                if ("ddd".equals(s)) {
                    throw new RuntimeException(s);
                }
                return s.length();
            });
            Result<Integer> result = Result.flatApplyOver(stream, f, 0, Integer::sum);

            @Test @DisplayName("is error")
            void isError() {
                result.match(
                        success -> fail("not a success"),
                        error -> assertThat(error).hasMessage("ddd")
                );
            }

        }
    }

    @Nested
    @DisplayName("toEither")
    class ToEitherTests {

        @Test
        @DisplayName("Success becomes Right")
        void successIsRight() {
            //given
            Result<String> result = Result.ok("success");
            //when
            Either<Throwable, String> either = result.toEither();
            //then
            either.match(
                    left -> fail("not a left"),
                    right -> assertThat(right).isEqualTo("success")
            );
        }

        @Test
        @DisplayName("Error becomes Left")
        void errorIsLeft() {
            //given
            RuntimeException exception = new RuntimeException();
            Result<Integer> result = anError(exception);
            //when
            Either<Throwable, Integer> either = result.toEither();
            //then
            either.match(
                    left -> assumeThat(left).isSameAs(exception),
                    right -> fail("not a right")
            );
        }
    }

    @Nested
    @DisplayName("from(Either)")
    class FromEitherTests {

        @Test @DisplayName("left is error")
        void leftIsError() {
            //given
            RuntimeException exception = new RuntimeException();
            Either<Throwable, String> either = Either.left(exception);
            //when
            Result<String> result = Result.from(either);
            //then
            result.match(
                    success -> fail("not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

        @Test @DisplayName("right is success")
        void rightIsSuccess() {
            //given
            Either<Throwable, String> either = Either.right("foo");
            //when
            Result<String> result = Result.from(either);
            //then
            result.match(
                    success -> assertThat(success).isEqualTo("foo"),
                    error -> fail("not an error")
            );
        }

    }

    @Nested
    @DisplayName("ofVoid")
    class OfVoidTests {

        @Test
        @DisplayName("no error is Success")
        void okayIsSuccess() {
            //given
            VoidCallable voidCallable = () -> {
                //do nothing
            };
            //when
            ResultVoid result = Result.ofVoid(voidCallable);
            //then
            result.match(
                    () -> assertThat(true).isTrue(),
                    error -> fail("not an error")
            );
        }

        @Test
        @DisplayName("throws exception is an Error")
        void exceptionIsError() {
            //given
            RuntimeException exception = new RuntimeException();
            VoidCallable voidCallable = () -> {
                throw exception;
            };
            //when
            ResultVoid result = Result.ofVoid(voidCallable);
            //then
            result.match(
                    () -> fail("not a success"),
                    error -> assertThat(error).isSameAs(exception)
            );
        }

    }

    /**
     * These include snippets from the Javadocs and are meant to prove that the examples are valid.
     */
    @Nested
    @DisplayName("javadoc documentation")
    class JavadocTests {
        @Nested
        @DisplayName("Static constructors")
        class StaticConstructors {
            @Test
            @DisplayName("ok")
            void ok() {
                ResultVoid okay = Result.ok();
                //
                assertThat(okay.isOkay()).isTrue();
            }

            @Test
            @DisplayName("ok(value)")
            void okValue() {
                Result<Integer> okay = Result.ok(1);
                //
                okay.match(
                        s -> assertThat(s).isEqualTo(1),
                        e -> fail("not an err")
                );
            }

            @Test
            @DisplayName("of")
            void of() {
                Result<Integer> okay = Result.of(() -> 1);
                Result<Integer> error = Result.of(() -> {
                    throw new RuntimeException();
                });
                //
                assertSoftly(s -> {
                    okay.match(
                            v -> s.assertThat(v).isEqualTo(1),
                            e -> fail("not an err")
                    );
                    error.match(
                            v -> fail("not a success"),
                            e -> s.assertThat(e).isInstanceOf(RuntimeException.class)
                    );
                });
            }

            @Test
            @DisplayName("ofVoid")
            void ofVoid() {
                ResultVoid okay = Result.ofVoid(() -> System.out.println("Hello, World!"));
                ResultVoid error = Result.ofVoid(() -> {
                    throw new Exception();
                });
                //
                assertSoftly(s -> {
                    s.assertThat(okay.isOkay()).isTrue();
                    s.assertThat(error.isError()).isTrue();
                });
            }

            @Test
            @DisplayName("error(Throwable)")
            void errorThrowable() {
                ResultVoid error = Result.error(new RuntimeException());
                //
                assertThat(error.isError()).isTrue();
            }

            @Test
            @DisplayName("error(Class, Throwable)")
            void errorClassThrowable() {
                Result<Integer> error = Result.error(TypeReference.create(), new RuntimeException());
                //
                assertThat(error.isError()).isTrue();
            }

            @Test
            @DisplayName("from Either")
            void fromEither() {
                Either<Throwable, String> eitherRight = Either.right("Hello, World!");
                Either<Throwable, String> eitherLeft = Either.left(new RuntimeException());
                Result<String> success = Result.from(eitherRight);
                Result<String> error = Result.from(eitherLeft);
                //
                assertSoftly(s -> {
                    success.match(
                            v -> s.assertThat(v).isEqualTo("Hello, World!"),
                            e -> fail("not an err")
                    );
                    error.match(
                            v -> fail("not a success"),
                            e -> s.assertThat(e).isInstanceOf(RuntimeException.class)
                    );
                });
            }

            @Test
            @DisplayName("from Maybe")
            void fromMaybe() {
                Maybe<Integer> maybe = Maybe.maybe(getValue());
                Result<Integer> result = Result.from(maybe, () -> new RuntimeException());
                //
                assertSoftly(s -> {
                    result.match(
                            v -> s.assertThat(v).isEqualTo(getValue()),
                            e -> fail("not an err")
                    );
                });
            }

        }
        @Nested
        @DisplayName("Static methods")
        class StaticMethodsTests {
            @Test
            @DisplayName("toMaybe")
            void toMaybe() {
                Result<Integer> result = Result.of(() -> getValue());
                Maybe<Integer> maybe = Result.toMaybe(result);
                //
                assertSoftly(s -> {
                    maybe.match(
                            j -> s.assertThat(j).isEqualTo(getValue()),
                            () -> fail("not a nothing")
                    );
                });
            }
            @Test
            @DisplayName("flatMayMaybe")
            void flatMapMaybe() {
                Result<Maybe<Integer>> result = Result.of(() -> Maybe.maybe(getValue()));
                Result<Maybe<Integer>> maybeResult = Result.flatMapMaybe(result,
                        maybe -> Result.of(() -> maybe.map(v -> v * 2)));
                //
                assertSoftly(s -> {
                    maybeResult.match(
                            m -> m.match(
                                    v -> s.assertThat(v).isEqualTo(2 * getValue()),
                                    () -> fail("not a nothing")),
                            e -> fail("not an err")
                    );
                });
            }
            @Test
            @DisplayName("flatApply(Stream, Consumer")
            void flatApplyOverStreamConsumer() {
                List<String> processed = new ArrayList<>();
                Consumer<String> consumer = s -> {
                    if ("dd".equals(s)) {
                        throw new RuntimeException("Invalid input: " + s);
                    }
                    processed.add(s);
                };

                Stream<String> okayStream = Stream.of("aa", "bb");
                ResultVoid resultOkay = Result.applyOver(okayStream, consumer);
                resultOkay.match(
                        () -> System.out.println("All processed okay."),
                        error -> System.out.println("Error: " + error.getMessage())
                );
                System.out.println("Processed: " + processed);
                // All processed okay.
                // Processed: [aa, bb]

                processed.add("--");
                Stream<String> errorStream = Stream.of("cc", "dd", "ee");// fails at 'dd'
                ResultVoid resultError = Result.applyOver(errorStream, consumer);
                resultError.match(
                        () -> System.out.println("All processed okay."),
                        error -> System.out.println("Error: " + error.getMessage())
                );
                System.out.println("Processed: " + processed);
                // Error: Invalid input: dd
                // Processed: [aa, bb, --, cc]

                assertSoftly(s -> {
                    s.assertThat(processed).containsExactly(
                            "aa", "bb", "--",
                            "cc"
                    );
                    resultError.match(
                            () -> s.fail("not a success"),
                            e -> s.assertThat(e).isInstanceOf(RuntimeException.class)
                                    .hasMessage("Invalid input: dd")
                    );
                });
            }
            @Test
            @DisplayName("applyOver(Stream, Function, BiFunction")
            void applyOverStreamFunctionBiFunction() {
                Function<String, Integer> f = s -> {
                    if ("dd".equals(s)) {
                        throw new RuntimeException("Invalid input: " + s);
                    }
                    return s.length();
                };

                assertSoftly(s -> {

                    Stream<String> okayStream = Stream.of("aa", "bb");
                    Result<Integer> resultOkay = Result.applyOver(okayStream, f, 0, Integer::sum);
                    resultOkay.match(
                            success -> s.assertThat(success).isEqualTo(4),
                            error -> s.fail("not an err")
                    );
                    // Total length: 4

                    Stream<String> errorStream = Stream.of("cc", "dd");
                    Result<Integer> resultError = Result.applyOver(errorStream, f, 0, Integer::sum);
                    resultError.match(
                            success -> s.fail("not a success"), // will not match
                            error -> s.assertThat(error.getMessage()).isEqualTo("Invalid input: dd")
                    );
                    // Error: Invalid input: dd

                });
            }
            @Test
            @DisplayName("flatApplyOver")
            void flatApplyOver() {
                Function<String, Result<Integer>> f = s -> {
                    if ("dd".equals(s)) {
                        return Result.error(TypeReference.create(), new RuntimeException("Invalid input: " + s));
                    }
                    return Result.ok(s.length());
                };

                assertSoftly(s -> {

                    Stream<String> okayStream = Stream.of("aa", "bb");
                    Result<Integer> resultOkay = Result.flatApplyOver(okayStream, f, 0, Integer::sum);
                    resultOkay.match(
                            success -> s.assertThat(success).isEqualTo(4),
                            error -> s.fail("not an err")
                    );
                    // Total length: 4

                    Stream<String> errorStream = Stream.of("cc", "dd");
                    Result<Integer> resultError = Result.flatApplyOver(errorStream, f, 0, Integer::sum);
                    resultError.match(
                            success -> s.fail("not a success"), // will not match
                            error -> s.assertThat(error.getMessage()).isEqualTo("Invalid input: dd")
                    );
                    // Error: Invalid input: dd
                });
            }

            @Test
            @DisplayName("thenWith")
            void thenWith() {
                AtomicInteger capture = new AtomicInteger();
                Supplier<Integer> doSomething = () -> 1;
                Consumer<Integer> doSomethingElse = capture::set;
                //
                Result<Integer> r = Result.of(() -> doSomething.get())
                        .thenWith(value -> () -> doSomethingElse.accept(value));
                //
                assertThat(capture).hasValue(1);
            }
        }
        @Nested
        @DisplayName("default methods")
        class DefaultMethodTests {

            @Test
            @DisplayName("toEither")
            void toEither() {
                Result<String> success = Result.ok("success");
                RuntimeException exception = new RuntimeException();
                Result<String> error = Result.error(TypeReference.create(), exception);

                Either<Throwable, String> eitherRight = success.toEither();
                Either<Throwable, String> eitherLeft = error.toEither();
                //
                assertSoftly(s -> {
                    eitherRight.match(
                            left -> s.fail("not a left"),
                            right -> s.assertThat(right).isEqualTo("success")
                    );
                    eitherLeft.match(
                            left -> s.assertThat(left).isSameAs(exception),
                            right -> s.fail("not a right")
                    );
                });
            }

        }

        @Nested
        @DisplayName("instance methods")
        class InstanceMethods {

            @Test
            @DisplayName("orElse")
            void orElse() {
                assertThatExceptionOfType(CheckedErrorResultException.class)
                        .isThrownBy(() -> Result.of(() -> getErrorValue()).orElseThrow())
                        .withCauseInstanceOf(RuntimeException.class);
            }

            @Test
            @DisplayName("toVoid")
            void toVoid() {
                ResultVoid result = Result.of(() -> getResultValue()).toVoid();
                //
                assertThat(result).isInstanceOf(SuccessVoid.class);
            }

            @Test
            @DisplayName("map")
            void map() {
                Result<String> result = Result.of(() -> getValue())
                        .map(v -> String.valueOf(v));
                //
                result.match(
                        success -> assertThat(success).isEqualTo("1"),
                        error -> fail("not en err")
                );
            }

            @Test
            @DisplayName("isOkay")
            void isOkay() {
                boolean isOkay = Result.of(() -> getValue()).isOkay();
                //
                assertThat(isOkay).isTrue();
            }

            @Test
            @DisplayName("isError")
            void isError() {
                boolean isError = Result.of(() -> getValue()).isError();
                //
                assertThat(isError).isFalse();
            }

            @Test
            @DisplayName("onErrorClassConsumer")
            void onErrorClassConsumer() {
                AtomicReference<Exception> err = new AtomicReference<>();
                Exception exception = new UnsupportedOperationException();
                //
                Result.of(() -> {
                            throw exception;
                        })
                        .onError(UnsupportedOperationException.class,
                                e -> err.set(e));
                //
                assertThat(err).hasValue(exception);
            }

            @Test
            @DisplayName("onErrorConsumer")
            void onErrorConsumer() {
                AtomicReference<Throwable> err = new AtomicReference<>();
                Exception exception = new UnsupportedOperationException();
                //
                Result.of(() -> {
                            throw exception;
                        })
                        .onError(e -> err.set(e));
                //
                assertThat(err).hasValue(exception);
            }

            @Test
            @DisplayName("orElseThrowUnchecked")
            void orElseThrowUnchecked() {
                Integer result = Result.of(() -> getValue())
                        .orElseThrowUnchecked();
                //
                assertThat(result).isEqualTo(1);
            }

            @Test
            @DisplayName("orElseThrowClass")
            void orElseThrowClass() throws IOException {
                Integer result = Result.of(() -> getValue())
                        .orElseThrow(IOException.class);
                //
                assertThat(result).isEqualTo(1);
            }

            @Test
            @DisplayName("peek")
            void peek() {
                AtomicInteger capture = new AtomicInteger();
                //
                Result<Integer> result = Result.of(() -> getValue())
                        .peek(v -> capture.set(v));
                //
                assertThat(capture).hasValue(getValue());
            }

            @Test
            @DisplayName("recover")
            void recover() {
                Result<Integer> result = Result.of(() -> getErrorValue())
                        .recover(e -> Result.of(() -> getSafeValue(e)));
                //
                result.match(
                        s -> assertThat(s).isEqualTo(2),
                        e -> fail("not an error")
                );
            }

            @Test
            @DisplayName("match")
            void match() {
                AtomicReference<Object> capture = new AtomicReference<>();
                //
                Result.of(()-> getValue())
                        .match(
                                success -> capture.set(success),
                                error -> capture.set(error)
                        );
                //
                assertThat(capture).hasValue(getValue());
            }

            @Test
            @DisplayName("flatMap")
            void flatMap() {
                Result<String> result =
                        Result.of(() -> getValue())
                                .flatMap(v -> Result.of(() -> String.valueOf(v)));
                //
                result.match(
                        s -> assertThat(s).isEqualTo("1"),
                        e -> fail("not an err")
                );
            }

            @Test
            @DisplayName("flatMapV")
            void flatMapV() {
                ResultVoid result = Result.of(() -> getValue())
                        .flatMapV(v -> Result.ok());
                //
                assertThat(result.isOkay()).isTrue();
            }

            @Test
            @DisplayName("onSuccess")
            void onSuccess() {
                AtomicInteger capture = new AtomicInteger();
                //
                Result.of(() -> getValue())
                        .onSuccess(v -> capture.set(v));
                //
                assertThat(capture).hasValue(getValue());
            }

        }

        private Result<Integer> getResultValue() {
            return Result.ok(getValue());
        }

        private Integer getValue() {
            return 1;
        }

        private Integer getSafeValue(Throwable e) {
            return 2;
        }

        private Integer getErrorValue() {
            throw new RuntimeException();
        };
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
            return Result.error(TypeReference.create(), new RuntimeException(fileName));
        }

        private Result<Integer> adjustValue(final Integer value) {
            return Result.ok(value + 2);
        }

        private Result<Double> calculateAverage(final Integer val1, final Integer val2) {
            return Result.ok((double) (val1 + val2) / 2);
        }

    }
}
