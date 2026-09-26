# Komp-Geom

[![Tests](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom/overview)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=coverage)](https://sonarcloud.io/summary/overall?id=cponfick_komp-geom)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=alert_status)](https://sonarcloud.io/summary/overall?id=cponfick_komp-geom)

Komp-Geom is a Kotlin Multiplatform library for computational geometry. It provides immutable-by-default geometric
primitives, precision-aware operations, mutable alternatives for performance-sensitive code, and a growing collection of
geometry algorithms.

The library currently targets JVM, JavaScript, WebAssembly, and Kotlin/Native platforms including Linux, Windows, macOS,
iOS, Android Native, watchOS, and tvOS.

> [!IMPORTANT]
> Komp-Geom is currently in a pre-1.0 release (`0.4.0-rc7`). The public API may change before `1.0.0`.

## Quick start

```kotlin
import io.github.cponfick.kompgeom.algorithms.convexhull.Quickhull2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

val points = listOf(
    Vec2(0.0, 0.0),
    Vec2(2.0, 0.0),
    Vec2(1.0, 1.0),
    Vec2(1.0, 0.25),
)

val hull = Quickhull2(points).execute()
println(hull)
```

For more examples and the complete API reference, see the [documentation](https://cponfick.github.io/komp-geom/). A
visualization application is also available in
the [komp-geom-visualizer](https://github.com/cponfick/komp-geom-visualizer) project.

## Installation

The latest release is available
on [Maven Central](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom/overview). Replace `VERSION` below
with the version you want to use.

### Kotlin Multiplatform

Add the dependency to the appropriate source set, usually `commonMain`:

```kotlin
dependencies {
    implementation("io.github.cponfick:komp-geom:VERSION")
}
```

### JVM-only projects

JVM-only projects can depend on the JVM-specific artifact:

```kotlin
dependencies {
    implementation("io.github.cponfick:komp-geom-jvm:VERSION")
}
```

Maven users can use the corresponding artifact in their `pom.xml`:

```xml

<dependency>
    <groupId>io.github.cponfick</groupId>
    <artifactId>komp-geom</artifactId>
    <version>VERSION</version>
</dependency>
```

For a JVM-only project, use `komp-geom-jvm` as the `artifactId`.

## Features

- **Geometric primitives** for one-, two-, and three-dimensional Euclidean spaces
- **Vectors and points** with arithmetic, products, distances, normalization, and related operations
- **Lines and line segments** in 2D and 3D
- **Polygons** in 2D and 3D
- **Affine transformations** in 1D, 2D, and 3D
- **Polar coordinates** in 2D
- **Precision-aware comparisons** using configurable `DoubleEquivalence` implementations
- **Immutable and mutable variants**, allowing callers to choose clarity or reduced allocation overhead
- **Cross-platform behavior** from shared Kotlin code

## Algorithms and data structures

### Algorithms

| Algorithm                      | Implementation             | Dimensions | Mutable input | Time complexity                          | Space complexity |
|--------------------------------|----------------------------|------------|---------------|------------------------------------------|------------------|
| Closest pair                   | Naive                      | 2D, 3D     | Yes           | `O(n²)`                                  | `O(1)`           |
| Closest pair                   | Divide and conquer         | 2D         | Yes           | `O(n log n)`                             | `O(n)`           |
| Convex hull                    | Quickhull                  | 2D         | Yes           | `O(n log n)` average, `O(n²)` worst case | `O(n)`           |
| Segment intersection detection | Shamos–Hoey sweep line     | 2D         | Yes           | `O(n log n)`                             | `O(n)`           |
| Segment intersection reporting | Bentley–Ottmann sweep line | 2D         | Yes           | `O((n + k) log n)`                       | `O(n + k)`       |

**Sweep precision limitation:** These complexity bounds assume consistent geometric predicates. Near the floating-point
tolerance boundary, pairwise segment intersection and computed sweep events can disagree, so the sweeps may miss
intersections even for finite inputs. Do not rely on them for guaranteed topology in such cases. A reproducible failing
case and plans for robust predicates are tracked in [issue #184](https://github.com/cponfick/komp-geom/issues/184).

If you are looking for an algorithm that is not listed, feel free
to [open an issue](https://github.com/cponfick/komp-geom/issues) or contribute an implementation.

### Data structures

| Data structure | Implementation           | Operations                                                      | Time complexity                                   | Space complexity |
|----------------|--------------------------|-----------------------------------------------------------------|---------------------------------------------------|------------------|
| Sorted map     | `MutableRedBlackTreeMap` | Insert, delete, lookup, neighbor queries, and ordered iteration | `O(log n)` per update or lookup; `O(n)` iteration | `O(n)`           |

## Precision handling

Floating-point arithmetic introduces rounding errors that can affect geometric calculations. Komp-Geom uses
`DoubleEquivalence` to make comparisons explicit and configurable.

The default implementation, `EpsilonDoubleEquivalence`, uses `GEOMETRIC_EPSILON = 1e-10`. For finite values, two values
are considered equal when:

```text
abs(a - b) <= epsilon * max(1, abs(a), abs(b))
```

This combines an absolute tolerance near zero with a relative tolerance for larger values:

```kotlin
import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence

val relaxed = EpsilonDoubleEquivalence(epsilon = 1e-6)

relaxed.eq(0.3000001, 0.3) // true
```

Most geometry types and operations use `DEFAULT_DOUBLE_EQUIVALENCE` by default, while operations that accept an
equivalence can be given application-specific precision:

```kotlin
import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.github.cponfick.kompgeom.euclidean.twod.AffineTransformationMatrix2

val precision = EpsilonDoubleEquivalence(epsilon = 1e-8)
val first = AffineTransformationMatrix2.createRotation(Math.PI / 4.0)
val second = AffineTransformationMatrix2.createRotation(0.7853981634)

val equivalent = first.eq(second, precision)
```

Choose an epsilon appropriate for the scale and accuracy requirements of your application. For consistent behavior,
create one equivalence instance and pass it to the operations that support custom precision instead of relying on
mutable global state.

## Immutability and performance

The standard geometric types are immutable. This makes values easier to share, reason about, and use in functional-style
code. Mutable alternatives are available where in-place updates are useful, including:

- `MutableVec1`, `MutableVec2`, and `MutableVec3`
- `MutableSeg2` and `MutableSeg3`
- Mutable algorithm implementations where applicable

Benchmarks show that mutable implementations can substantially reduce allocations and improve throughput for repeated
operations. For example, the affine transformation benchmark reports the following improvements for one million
transformations:

- 2.66× faster on JVM
- 2.59× faster on JavaScript
- 2.10× faster on Linux Native

See the [benchmark results](docs/benchmarks/affine_transformation.md) for details and context. Benchmark results depend
on the platform, runtime, hardware, and workload.

## Documentation and examples

- [API documentation](https://cponfick.github.io/komp-geom/)
- [Visualization application](https://github.com/cponfick/komp-geom-visualizer)
- [Benchmark results](docs/benchmarks/affine_transformation.md)
- [Contributing guide](CONTRIBUTING.md)
- [MIT license](LICENSE)

## Contributing

Contributions, bug reports, feature requests, and documentation improvements are welcome. Please read
the [contributing guide](CONTRIBUTING.md) before opening a pull request.

Before submitting a change, run:

```bash
./gradlew spotlessApply
./gradlew spotlessCheck
./gradlew allTests
```

`allTests` runs the tests available on the current host. Browser-based JavaScript and WebAssembly tests require Chrome;
CI covers additional platforms.
