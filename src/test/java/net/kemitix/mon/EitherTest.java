package net.kemitix.mon;

import net.kemitix.mon.experimental.either.Either;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;

class EitherTest implements WithAssertions {
    @Nested
    @DisplayName("isLeft()")
    public class IsLeft {
        @Test
        void whenLeft_isLeft() {
            //when
            final Either<Integer, String> either = Either.left(1);
            //then
            assertThat(either.isLeft()).isTrue();
        }
        @Test
        void whenRight_isNotLeft() {
            //when
            final Either<Integer, String> either = Either.right("1");
            //then
            assertThat(either.isLeft()).isFalse();
        }
    }
    @Nested
    @DisplayName("isRight()")
    public class IsRight {
        @Test
        void whenLeft_isNotRight() {
            //when
            final Either<Integer, String> either = Either.left(1);
            //then
            assertThat(either.isRight()).isFalse();
        }
        @Test
        void whenRight_isRight() {
            //when
            final Either<Integer, String> either = Either.right("1");
            //then
            assertThat(either.isRight()).isTrue();
        }
    }
    @Nested
    @DisplayName("match()")
    public class Match {
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
    }
    @Nested
    @DisplayName("mapLeft()")
    public class MapLeft {
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
    @Nested
    @DisplayName("mapRight()")
    public class MapRight {
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
    }
    @Nested
    @DisplayName("flatMapLeft()")
    public class FlatMapLeft {
        @Test
        void givenLeft_whenFlatMapLeft_whenResultLeft_thenIsNewLeft() {
            //given
            Either<Integer, String> either = Either.left(2);
            //when
            Either<Integer, String> result = either.flatMapLeft(l -> Either.left(l * 2));
            //then
            result.match(
                    left -> assertThat(left).isEqualTo(4),
                    right -> fail("Not a right")
            );
        }
        @Test
        void givenLeft_whenFlatMapLeft_whenResultRight_thenIsRight() {
            //given
            Either<Integer, String> either = Either.left(2);
            //when
            Either<Integer, String> result = either.flatMapLeft(l -> Either.right("recovered"));
            //then
            result.match(
                    left -> fail("Not a left"),
                    right -> assertThat(right).isEqualTo("recovered")
            );
        }
        @Test
        void givenRight_whenFlatMapLeft_thenDoNothing() {
            //given
            Either<Integer, String> either = Either.right("2");
            //when
            Either<Integer, String> result = either.flatMapLeft(l -> Either.left(l * 2));
            //then
            result.match(
                    left -> fail("Not a left"),
                    right -> assertThat(right).isEqualTo("2")
            );
        }
    }
    @Nested
    @DisplayName("flatMapRight()")
    public class FlatMapRight {
        @Test
        void givenLeft_whenFlatMapRight_thenDoNothing() {
            //given
            Either<Integer, String> either = Either.left(2);
            //when
            Either<Integer, String> result = either.flatMapRight(l -> Either.right(l + "2"));
            //then
            result.match(
                    left -> assertThat(left).isEqualTo(2),
                    right -> fail("Not a right")
            );
        }
        @Test
        void givenRight_whenFlatMapRight_whenResultRight_thenIsNewRight() {
            //given
            Either<Integer, String> either = Either.right("2");
            //when
            Either<Integer, String> result = either.flatMapRight(l -> Either.right(l + "2"));
            //then
            result.match(
                    left -> fail("Not a left"),
                    right -> assertThat(right).isEqualTo("22")
            );
        }
        @Test
        void givenRight_whenFlatMapRight_whenResultLeft_thenIsLeft() {
            //given
            Either<Integer, String> either = Either.right("2");
            //when
            Either<Integer, String> result = either.flatMapRight(l -> Either.left(7));
            //then
            result.match(
                    left -> assertThat(left).isEqualTo(7),
                    right -> fail("Not a right")
            );
        }
    }
    @Nested @DisplayName("getLeft") public class GetLeft {
        @Test
        @DisplayName("when is a Left then get the value")
        public void whenLeft_thenGetValue() {
            //given
            Either<String, Integer> either = Either.left("value");
            //when
            Optional<String> left = either.getLeft();
            //then
            assertThat(left).contains("value");
        }

        @Test
        @DisplayName("when is a Right then is empty")
        public void whenRight_thenGetEmpty() {
            //given
            Either<Integer, String> either = Either.right("value");
            //when
            Optional<Integer> left = either.getLeft();
            //then
            assertThat(left).isEmpty();
        }
    }
    @Nested @DisplayName("getRight") public class GetRight {
        @Test
        @DisplayName("when is a Left then is empty")
        public void whenLeft_thenGetEmpty() {
            //given
            Either<String, Integer> either = Either.left("value");
            //when
            Optional<Integer> left = either.getRight();
            //then
            assertThat(left).isEmpty();
        }

        @Test
        @DisplayName("when is a Right then get the value")
        public void whenRight_thenGetValue() {
            //given
            Either<Integer, String> either = Either.right("value");
            //when
            Optional<String> left = either.getRight();
            //then
            assertThat(left).contains("value");
        }
    }
//    @Nested
//    @DisplayName("compose()")
//    public class Compose {
//
//        BiFunction<String, String, Either<String, Integer>> leftToLeftComposer =
//                (la, lb) -> Either.left(la + lb);
//
//        BiFunction<String, String, Either<String, Integer>> leftToRightComposer =
//                (la, lb) -> Either.right(Integer.parseInt(la) + Integer.parseInt(lb));
//
//        BiFunction<Integer, Integer, Either<String, Integer>> rightToLeftComposer =
//                (la, lb) -> Either.left("" + la + lb);
//
//        BiFunction<Integer, Integer, Either<String, Integer>> rightToRightComposer =
//                (la, lb) -> Either.right(la + lb);
//
//        @Test
//        public void givenRightAndRight_whenComposeRight_then() {
//            //given
//            Either<String, Integer> ea = Either.right(3);
//            Either<String, Integer> eb = Either.right(7);
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToRightComposer,
//                    rightToRightComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//
//        @Test
//        public void givenRightAndRight_whenComposeLeft_then() {
//            //given
//            Either<String, Integer> ea = Either.right(3);
//            Either<String, Integer> eb = Either.right(7);
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToLeftComposer,
//                    rightToLeftComposer);
//            //then
//            result.match(
//                    left ->  assertThat(left).isEqualTo("a+b"),
//                    right -> fail("Not a right")
//            );
//        }
//        // compose left with left
//        @Test
//        public void givenLeftAndLeft_whenComposeRight_then() {
//            //given
//            Either<String, Integer> ea = Either.left("3");
//            Either<String, Integer> eb = Either.left("7");
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToRightComposer,
//                    rightToRightComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//        @Test
//        public void givenLeftAndLeft_whenComposeLeft_then() {
//            //given
//            Either<String, Integer> ea = Either.left("3");
//            Either<String, Integer> eb = Either.left("7");
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToLeftComposer,
//                    rightToLeftComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//        // compose left with right
//        @Test
//        public void givenLeftAndRight_whenComposeRight_then() {
//            //given
//            Either<String, Integer> ea = Either.left("3");
//            Either<String, Integer> eb = Either.right(7);
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToRightComposer,
//                    rightToRightComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//        @Test
//        public void givenLeftAndRight_whenComposeLeft_then() {
//            //given
//            Either<String, Integer> ea = Either.left("3");
//            Either<String, Integer> eb = Either.right(7);
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToLeftComposer,
//                    rightToLeftComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//        // compose right with left
//        @Test
//        public void givenRightAndLeft_whenComposeRight_then() {
//            //given
//            Either<String, Integer> ea = Either.right(3);
//            Either<String, Integer> eb = Either.left("7");
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToRightComposer,
//                    rightToRightComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//        @Test
//        public void givenRightAndLeft_whenComposeLeft_then() {
//            //given
//            Either<String, Integer> ea = Either.right(3);
//            Either<String, Integer> eb = Either.left("7");
//            //when
//            Either<String, Integer> result = ea.compose(eb,
//                    leftToLeftComposer,
//                    rightToLeftComposer);
//            //then
//            result.match(
//                    left -> fail("Not a left"),
//                    right -> assertThat(right).isEqualTo(10)
//            );
//        }
//    }
}
