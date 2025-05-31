[![Continuous Integration](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml) [![Maven Central Version](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom-jvm/overview)


# Computational Geometry in Kotlin 

This repository contains a collection of algorithms and data structures for computational geometry implemented in KMP (
Kotlin Multiplatform). The focus is on providing efficient solutions to common geometric problems on multiple platforms.

Currently, following platforms are supported:

- JVM
- JS

## Usage Example

### Kotlin
Add `implementation("io.github.cponfick:komp-geom-kotlin:{VERSION}")` to your `build.gradle.kts` file.

Start using the algorithms, or the core data structures, by importing the relevant classes.

```kotlin
import io.github.cponfick.kompgeom.algorithms.closestpair.ClosestPairDivideAndConquer
import io.github.cponfick.kompgeom.core.Point2

fun main() {
  val point1 = Point2()
  val point2 = Point2(1.0f, 1.0f)
  val point3 = Point2(2.0f, 2.0f)

  val closestPair = ClosestPairDivideAndConquer(listOf(point1, point2, point3))
  val result = closestPair.run()
}
```

### Java
Add `implementation("io.github.cponfick:komp-geom-jvm:{VERSION}")` to your `build.gradle` file.

Start using the algorithms, or the core data structures, by importing the relevant classes.

```java
import io.github.cponfick.kompgeom.algorithms.closestpair.ClosestPairDivideAndConquer;
import io.github.cponfick.kompgeom.core.Point2;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final Point2 point1 = new Point2(0, 0);
        final Point2 point2 = new Point2(1, 1);
        final Point2 point3 = new Point2(2, 2);

        final ClosestPairDivideAndConquer closestPair = new ClosestPairDivideAndConquer(List.of(point1, point2, point3));
        final var result = closestPair.run();
    }
}
```

## Implemented Algorithms

| Algorithm    | Implementation     | Runtime Complexity | Space Complexity |
|--------------|--------------------|--------------------|------------------|
| Closest Pair | Naive              | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | O(n log n)         | O(n)             |