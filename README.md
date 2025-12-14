[![tests](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=cponfick_komp-geom)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=coverage)](https://sonarcloud.io/summary/new_code?id=cponfick_komp-geom)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=cponfick_komp-geom)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=bugs)](https://sonarcloud.io/summary/new_code?id=cponfick_komp-geom)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=cponfick_komp-geom&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=cponfick_komp-geom)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom/overview)

# Computational Geometry in Kotlin

This repository contains a collection of algorithms and data structures for computational geometry implemented in Kotlin
Multiplatform (KMP). The library focuses on providing efficient solutions to common geometric problems across multiple
platforms. It aims to provide a solid foundation for building complex geometric algorithms and applications while
offering idiomatic Kotlin APIs.

The following platforms are currently supported:

- JVM
- JS
- WebAssembly (WASM)
- Native (iOS, Linux, Windows, macOS)

> [!IMPORTANT]
> This project is in its early stages. Until the first stable release 1.0.0, the API may change frequently. After
> the 1.0.0 release, versioning will follow semantic versioning principles.

## Usage Examples

Usage examples are provided inside the [documentation](https://cponfick.github.io/komp-geom/).

## Demo Application

A demo application is available at [komp-geom-visualization](https://github.com/cponfick/komp-geom-visualizer). It
allows you to visualize the algorithms and data structures implemented in this library. The goal is to provide visual
representations for most, if not all, algorithms and data structures.

## Installation

To add the library to your multiplatform project, include the following dependency:

**Gradle:**

```kotlin
implementation("io.github.cponfick:komp-geom:{VERSION}")
```

**Maven:**

```xml

<dependency>
    <groupId>io.github.cponfick</groupId>
    <artifactId>komp-geom</artifactId>
    <version>{VERSION}</version>
</dependency>
```

You can also use the library directly in a JVM-only project by adding the following dependency:

**Gradle:**

```kotlin
implementation("io.github.cponfick:komp-geom-jvm:{VERSION}")
```

**Maven:**

```xml

<dependency>
    <groupId>io.github.cponfick</groupId>
    <artifactId>komp-geom-jvm</artifactId>
    <version>{VERSION}</version>
</dependency>
```

## Core Components

This section provides an overview of the core components of the library, which are designed to serve as building blocks
for implementing geometric algorithms.

The library currently provides the following geometric elements:

- **Vectors**: Implementation of 1D, 2D, and 3D vectors with basic operations such as addition, subtraction, and dot
  product.
- **Lines**: Representation of lines in 2D and 3D space.
- **Line Segments**: Representation of line segments in 2D and 3D space.
- **Polygons**: Representation of polygons in 2D and 3D space.
- **Affine Transformations**: Support for 1D, 2D, and 3D affine transformations on vectors.
- **Polar Coordinates**: Implementation of polar coordinates in 2D space.

## Precision Handling

Floating-point arithmetic inherently introduces small rounding errors that can cause issues in geometric computations.
The library addresses this challenge through a configurable epsilon-based comparison system, ensuring robust and
reliable geometric operations across all platforms.

### Default Precision

By default, the library uses `GEOMETRIC_EPSILON` (1e-10) as the tolerance threshold. Two double values are considered
equal if their absolute difference is within this epsilon:

```kotlin
// Using default precision
val a = 0.30000000000000004
val b = 0.3
DEFAULT_DOUBLE_EQUIVALENCE.eq(a, b)  // true
```

### Custom Precision

You can create custom `DoubleEquivalence` instances to adjust precision for specific use cases:

```kotlin
// More lenient precision for approximate calculations
val relaxed = DoubleEquivalence(epsilon = 1e-6)
relaxed.eq(0.3000001, 0.3)  // true

// Stricter precision for high-accuracy requirements
val strict = DoubleEquivalence(epsilon = 1e-12)
strict.eq(0.30000000001, 0.3)  // false
```

### Available Comparison Operations

The `DoubleEquivalence` class provides a complete set of comparison methods:

```kotlin
val precision = DoubleEquivalence()

precision.eq(a, b)      // Equal to
precision.eqZero(a)     // Equal to zero
precision.lt(a, b)      // Less than
precision.lte(a, b)     // Less than or equal to
precision.gt(a, b)      // Greater than
precision.gte(a, b)     // Greater than or equal to
```

### Precision in Geometric Operations

Many geometric data structures and algorithms accept an optional `DoubleEquivalence` parameter to control
precision-aware operations. The parameter defaults to `DEFAULT_DOUBLE_EQUIVALENCE`, making it optional in most cases:

```kotlin
// Comparing transformation matrices with default precision
val matrix1 = AffineTransformationMatrix3.createRotationX(Math.PI / 4)
val matrix2 = AffineTransformationMatrix3.createRotationX(0.7853981634)
matrix1.eq(matrix2)  // Uses default precision

// Custom precision for specific requirements
matrix1.eq(matrix2, DoubleEquivalence(epsilon = 1e-9))  // true
```

### Global Precision Configuration

For applications requiring consistent custom precision across all operations, you can modify the global defaults:

```kotlin
// Adjust global epsilon (affects all new DoubleEquivalence instances)
GEOMETRIC_EPSILON = 1e-8

// Replace the global default equivalence
DEFAULT_DOUBLE_EQUIVALENCE = DoubleEquivalence(epsilon = 1e-8)
```

**Note:** Modifying global defaults should be done during application initialization, as it affects all subsequent
geometric operations throughout the library.

## Immutability and Performance

The library follows Kotlin's philosophy of immutability by default. Immutable data structures offer several advantages:

- **Thread Safety**: Immutable objects can be safely shared across threads without synchronization
- **Predictability**: Operations never modify existing objects, making code easier to reason about
- **Functional Style**: Enables a more functional programming approach with pure functions

However, for performance-critical applications involving large-scale operations, immutability can introduce overhead
due to object allocations. To address this, the library also provides **mutable implementations** for certain data
types that modify objects in-place. Currently, mutable implementations are available for:

- Vectors (1D, 2D, 3D)

Further, following algorithms support mutable implementations:

- Affine Transformations (1D, 2D, 3D)
- Closest Pair (2D, 3D)
- Convex Hull (2D)

### Benchmark Results

Based on [benchmark results](docs/benchmarks/affine_transformation.md), mutable implementations offer significant
performance improvements when performing large numbers of operations:

- **2.66× faster** on JVM for 1M affine transformations
- **2.59× faster** on JS for 1M affine transformations
- **2.10× faster** on Native (Linux) for 1M affine transformations

## Algorithms

The following is a list of implemented algorithms. If you are missing an algorithm, feel free to open an issue or
contribute a pull request.

| Algorithm    | Implementation     | Supported Dimensions | Mutable Input<br/>Supported | Runtime Complexity | Space Complexity |
|--------------|--------------------|----------------------|-----------------------------|--------------------|------------------|
| Closest Pair | Naive              | 2D, 3D               | yes                         | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | 2D                   | yes                         | O(n log n)         | O(n)             |
| Convex Hull  | QuickHull          | 2D                   | yes                         | O(n log n)         | O(n)             |

## Contributing

Contributions are welcome! Please check the [contributing guidelines](CONTRIBUTING.md) for more information on how to
get started.
Feel free to open issues for bugs, feature requests, or general questions.
