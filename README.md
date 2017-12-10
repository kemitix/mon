Mon
===

TypeAlias for Java

[![GitHub release](https://img.shields.io/github/release/kemitix/mon.svg)]()
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d57096b0639d496aba9a7e43e7cf5b4c)](https://www.codacy.com/app/kemitix/mon?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kemitix/mon&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/kemitix/mon.svg?branch=master)](https://travis-ci.org/kemitix/mon)
[![Coverage Status](https://coveralls.io/repos/github/kemitix/mon/badge.svg?branch=master)](https://coveralls.io/github/kemitix/mon?branch=master)
[![codecov](https://codecov.io/gh/kemitix/mon/branch/master/graph/badge.svg)](https://codecov.io/gh/kemitix/mon)

## Maven

```xml
<dependency>
    <groupId>net.kemitix</groupId>
    <artifactId>mon</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Usage

### TypeAlias

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
Goal goal = Goal.of("goal");

void foo(final Goal goal) {
    System.out.println("The goal is " + goal.getValue());
}
```

### Maybe (Just & Nothing)

```java
assertThat(Maybe.maybe(null)).isEqualTo(Maybe.nothing());
assertThat(Maybe.maybe(1)).isEqualTo(Maybe.just(1));
assertThat(Maybe.nothing()
                .orElseGet(() -> 1)).isEqualTo(1);
assertThat(Maybe.just(1)
                .orElseGet(() -> 2)).isEqualTo(1);
assertThat(Maybe.nothing()
                .orElse(1)).isEqualTo(1);
assertThat(Maybe.just(1)
                .orElse(2)).isEqualTo(1);
assertThat(Maybe.just(1)
                .filter(v -> v > 2)).isEqualTo(Maybe.nothing());
assertThat(Maybe.just(3)
                .filter(v -> v > 2)).isEqualTo(Maybe.just(3));
assertThat(Maybe.just(1)
                .toOptional()).isEqualTo(Optional.of(1));
assertThat(Maybe.nothing()
                .toOptional()).isEqualTo(Optional.empty());
assertThat(Maybe.fromOptional(Optional.of(1))).isEqualTo(Maybe.just(1));
assertThat(Maybe.fromOptional(Optional.empty())).isEqualTo(Maybe.nothing());
final AtomicInteger reference = new AtomicInteger(0);
assertThat(Maybe.just(1).peek(reference::set)).isEqualTo(Maybe.just(1));
assertThat(reference).hasValue(1);
assertThat(Maybe.nothing().peek(v -> reference.incrementAndGet())).isEqualTo(Maybe.nothing());
assertThat(reference).hasValue(1);
assertThatCode(() -> Maybe.just(1).orElseThrow(IllegalStateException::new))
        .doesNotThrowAnyException();
assertThatThrownBy(() -> Maybe.nothing().orElseThrow(IllegalStateException::new))
        .isInstanceOf(IllegalStateException.class);
```
