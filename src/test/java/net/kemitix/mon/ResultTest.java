package net.kemitix.mon;

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
}