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
    <version>0.3.0</version>
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
