package net.kemitix.mon;

import net.kemitix.mon.experimental.either.Either;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class EitherTest implements WithAssertions {

    @Test
    void whenLeft_isLeft() {
        //when
        final Either<Integer, String> either = Either.left(1);
        //then
        assertThat(either.isLeft()).isTrue();
    }

    @Test
    void whenLeft_isNotRight() {
        //when
        final Either<Integer, String> either = Either.left(1);
        //then
        assertThat(either.isRight()).isFalse();
    }

    @Test
    void whenRight_isNotLeft() {
        //when
        final Either<Integer, String> either = Either.right("1");
        //then
        assertThat(either.isLeft()).isFalse();
    }

    @Test
    void whenRight_isRight() {
        //when
        final Either<Integer, String> either = Either.right("1");
        //then
        assertThat(either.isRight()).isTrue();
    }

    @Test
    void whenLeft_matchLeft() {
        //given
        final Either<Integer, String> either = Either.left(1);
        //then
        either.match(
                left -> assertThat(left).isEqualTo(1),
                right -> fail("Not a right")
        );
    }

    @Test
    void whenRight_matchRight() {
        //given
        final Either<Integer, String> either = Either.right("1");
        //then
        either.match(
                left -> fail("Not a left"),
                right -> assertThat(right).isEqualTo("1")
        );
    }

    @Test
    void givenLeft_whenMapLeft_thenMap() {
        //given
        final Either<Integer, String> either = Either.left(2);
        //when
        final Either<Integer, String> result = either.mapLeft(l -> l * 2);
        //then
        result.match(
                left -> assertThat(left).isEqualTo(4),
                right -> fail("Not a right")
        );
    }

    @Test
    void givenRight_whenMapRight_thenMap() {
        //given
        final Either<Integer, String> either = Either.right("2");
        //when
        final Either<Integer, String> result = either.mapRight(l -> l + "2");
        //then
        result.match(
                left -> fail("Not a left"),
                right -> assertThat(right).isEqualTo("22")
        );
    }

    @Test
    void givenLeft_whenMapRight_thenDoNothing() {
        //given
        final Either<Integer, String> either = Either.left(2);
        //when
        final Either<Integer, String> result = either.mapRight(r -> r + "x");
        //then
        result.match(
                left -> assertThat(left).isEqualTo(2),
                right -> fail("Not a right")
        );
    }

    @Test
    void givenRight_whenMapLeft_thenDoNothing() {
        //given
        final Either<Integer, String> either = Either.right("2");
        //when
        final Either<Integer, String> result = either.mapLeft(l -> l * 2);
        //then
        result.match(
                left -> fail("Not a left"),
                right -> assertThat(right).isEqualTo("2")
        );
    }
}
