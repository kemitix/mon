Mon
===

TypeAlias, Maybe and Result for Java

[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/https/oss.sonatype.org/net.kemitix/mon.svg?style=for-the-badge)](https://oss.sonatype.org/content/repositories/releases/net/kemitix/mon/)
[![Maven Central](https://img.shields.io/maven-central/v/net.kemitix/mon.svg?style=for-the-badge)](https://search.maven.org/#search|ga|1|g%3A"net.kemitix"%20AND%20a%3A"mon")

[![SonarQube Coverage](https://img.shields.io/sonar/https/sonarcloud.io/net.kemitix%3Amon/coverage.svg?style=for-the-badge)](https://sonarcloud.io/dashboard?id=net.kemitix%3Amon)
[![SonarQube Tech Debt](https://img.shields.io/sonar/https/sonarcloud.io/net.kemitix%3Amon/tech_debt.svg?style=for-the-badge)](https://sonarcloud.io/dashboard?id=net.kemitix%3Amon)

[![Jenkins](https://img.shields.io/jenkins/s/https/jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon.svg?style=for-the-badge)](https://jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon/)
[![Jenkins tests](https://img.shields.io/jenkins/t/https/jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon.svg?style=for-the-badge)](https://jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon/)
[![Jenkins coverage](https://img.shields.io/jenkins/c/https/jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon.svg?style=for-the-badge)](https://jenkins.kemitix.net/job/GitLab/job/kemitix%252Fmon/)

[![Codacy grade](https://img.shields.io/codacy/grade/d57096b0639d496aba9a7e43e7cf5b4c.svg?style=for-the-badge)](https://app.codacy.com/project/kemitix/mon/dashboard)

## Maven

```xml
<dependency>
    <groupId>net.kemitix</groupId>
    <artifactId>mon</artifactId>
    <version>RELEASE</version>
</dependency>
```

The latest version should be shown above with the nexus and maven-central badges.

## Usage

### TypeAlias

More of a type-wrapper really. It's as close as I could get to a Haskell type alias in Java.

```java
class Goal extends TypeAlias<String> {
    private Goal(final String goal) {
        super(goal);
    }
    public static Goal of(final String goal) {
        return new Goal(goal);
    }
}
```

```java
class Example {
    Goal goal = Goal.of("goal");
    void foo(final Goal goal) {
        System.out.println("The goal is " + goal.getValue());
    }
}
```

### Maybe

A non-final substitute for Optional with `peek()` and `stream()` methods.

```java
class Test {
    @Test
    public void maybeTests() {
        // Constructors: maybe(T), just(T) and nothing()
        assertThat(Maybe.maybe(null)).isEqualTo(Maybe.nothing());
        assertThat(Maybe.maybe(1)).isEqualTo(Maybe.just(1));
        // .orElseGet(Supplier<T>)
        assertThat(Maybe.nothing().orElseGet(() -> 1)).isEqualTo(1);
        assertThat(Maybe.just(1).orElseGet(() -> 2)).isEqualTo(1);
        // .orElse(Supplier<T>)
        assertThat(Maybe.nothing().orElse(1)).isEqualTo(1);
        assertThat(Maybe.just(1).orElse(2)).isEqualTo(1);
        // .filter(Predicate<T>)
        assertThat(Maybe.just(1).filter(v -> v > 2)).isEqualTo(Maybe.nothing());
        assertThat(Maybe.just(3).filter(v -> v > 2)).isEqualTo(Maybe.just(3));
        assertThat(Maybe.just(1).toOptional()).isEqualTo(Optional.of(1));
        assertThat(Maybe.nothing().toOptional()).isEqualTo(Optional.empty());
        // .fromOptional(Optional<T>) is deprecated
        assertThat(Maybe.fromOptional(Optional.of(1))).isEqualTo(Maybe.just(1));
        assertThat(Maybe.fromOptional(Optional.empty())).isEqualTo(Maybe.nothing());
        // An alternative to using .fromOptional(Optional<T>)
        assertThat(Optional.of(1).map(Maybe::just).orElse(Maybe::nothing)).isEqualTo(Maybe.just(1));
        assertThat(Optional.empty().map(Maybe::just).orElse(Maybe::nothing)).isEqualTo(Maybe.nothing());
        // .peek(Consumer<T>)
        final AtomicInteger reference = new AtomicInteger(0);
        assertThat(Maybe.just(1).peek(reference::set)).isEqualTo(Maybe.just(1));
        assertThat(reference).hasValue(1);
        assertThat(Maybe.nothing().peek(v -> reference.incrementAndGet())).isEqualTo(Maybe.nothing());
        assertThat(reference).hasValue(1);
        // .orElseThrow(Supplier<Exception>)
        assertThatCode(() -> Maybe.just(1).orElseThrow(IllegalStateException::new)).doesNotThrowAnyException();
        assertThatThrownBy(() -> Maybe.nothing().orElseThrow(IllegalStateException::new)).isInstanceOf(IllegalStateException.class);
        // .stream()
        assertThat(Maybe.just(1).stream()).containsExactly(1);
        assertThat(Maybe.nothing().stream()).isEmpty();
    }
}
```

### Result

A container for method return values that may raise an Exception. Useful for when a checked exceptions can't be added 
to the method signature.

```java
package net.kemitix.mon;

import net.kemitix.mon.result.Result;

import java.io.IOException;

class ResultExample implements Runnable {

    public static void main(String[] args) {
        new ResultExample().run();
    }

    @Override
    public void run() {
        System.out.println("run");
        final Result<Integer> goodResult = goodMethod();
        if (goodResult.isOkay()) {
            doGoodThings();
        }
        if (goodResult.isError()) {
            notCalled(0);
        }

        goodResult.flatMap(number -> convertToString(number))
                .flatMap(str -> stringLength(str))
                .match(
                        success -> System.out.format("Length is %s%n", success),
                        error -> System.out.println("Count not determine length")
                );

        final Result<Integer> badResult = badMethod();
        badResult.match(
                success -> notCalled(success),
                error -> handleError(error)
        );
    }

    private Result<Integer> goodMethod() {
        System.out.println("goodMethod");
        return Result.ok(1);
    }

    private void doGoodThings() {
        System.out.println("doGoodThings");
    }

    private void notCalled(final Integer success) {
        System.out.println("notCalled");
    }

    private Result<String> convertToString(final Integer number) {
        System.out.println("convertToString");
        return Result.ok(String.valueOf(number));
    }

    private Result<Integer> stringLength(final String value) {
        System.out.println("stringLength");
        if (value == null) {
            return Result.error(new NullPointerException("value is null"));
        }
        return Result.ok(value.length());
    }

    // doesn't need to declare "throws IOException"
    private Result<Integer> badMethod() {
        System.out.println("badMethod");
        return Result.error(new IOException("error"));
    }

    private void handleError(final Throwable error) {
        System.out.println("handleError");
        throw new RuntimeException("Handled exception", error);
    }

}
```
Will output:
```text
run
goodMethod
doGoodThings
convertToString
stringLength
Length is 1
badMethod
handleError
Exception in thread "main" java.lang.RuntimeException: Handled exception
	at net.kemitix.mon.ResultExample.handleError(ResultExample.java:72)
	at net.kemitix.mon.ResultExample.lambda$run$5(ResultExample.java:34)
	at net.kemitix.mon.result.Err.match(Err.java:56)
	at net.kemitix.mon.ResultExample.run(ResultExample.java:32)
	at net.kemitix.mon.ResultExample.main(ResultExample.java:10)
Caused by: java.io.IOException: error
	at net.kemitix.mon.ResultExample.badMethod(ResultExample.java:67)
	at net.kemitix.mon.ResultExample.run(ResultExample.java:31)
	... 1 more
```
