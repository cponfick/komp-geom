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
- **Affine Transformations**: Support for 1D, 2D, and 3D affine transformations on vectors.
- **Polar Coordinates**: Implementation of polar coordinates in 2D space.

## Algorithms

The following is a list of implemented algorithms. If you are missing an algorithm, feel free to open an issue or
contribute a pull request.

| Algorithm    | Implementation     | Supported Dimensions | Runtime Complexity | Space Complexity |
|--------------|--------------------|----------------------|--------------------|------------------|
| Closest Pair | Naive              | 2D, 3D               | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | 2D                   | O(n log n)         | O(n)             |
| Convex Hull  | QuickHull          | 2D                   | O(n log n)         | O(n)             |

## Contributing

Contributions are welcome! Please check the [contributing guidelines](CONTRIBUTING.md) for more information on how to
get started.
Feel free to open issues for bugs, feature requests, or general questions.
