[![Continuous Integration](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml)
![badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/cponfick/459bdccf80e8da31dbb845b91488f8e1/raw/komp-geom-coverage-badge.json)
[![CodeQL Advanced](https://github.com/cponfick/komp-geom/actions/workflows/codeql.yml/badge.svg)](https://github.com/cponfick/komp-geom/actions/workflows/codeql.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom/overview)

# Computational Geometry in Kotlin

This repository contains a collection of algorithms and data structures for computational geometry implemented in KMP (
Kotlin Multiplatform). The focus is on providing efficient solutions to common geometric problems on multiple platforms.
Further, it aims to provide a solid foundation for building more complex geometric algorithms and applications, while
providing kotlin idiomatic APIs.

Currently, following platforms are supported:

- JVM
- JS
- WebAssembly (WASM)
- Native (iOS, Linux, Windows, macOS)

> [!IMPORTANT]
> This project is in its early stages. Until the first stable release 1.0.0, the API may change frequently. Following
> the 1.0.0 release versioning will obey semantic versioning principles.

## Contributing

Contributions are welcome! Check the [contributing guidelines](CONTRIBUTING.md) for more information on how to get started.
Feel free to open issues for bugs, feature requests, or general questions.

## Installation

To add the library to your multiplatform project include following dependency:

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

It is also possible to use the java library directly in a JVM project by adding the dependency:

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

This section provides an overview of the core components of the library, which are designed to be used as building blocks for implementing geometric algorithms.

Currently, the library provides the following geometric elements:

- **Vectors**: Implementation of 1D, 2D, and 3D vectors with basic operations like addition, subtraction, and dot product.
- **Affine Transformations**: Support for 1D, 2D, and 3D affine transformations on vectors.
- **Polar Coordinates**: Implementation of polar coordinates in 2D space.

## Usage Example

Usage examples are provided inside the [documentation](https://cponfick.github.io/komp-geom/).

## Implemented Algorithms

Following a list of implemented algorithms.

| Algorithm    | Implementation     | Runtime Complexity | Space Complexity |
|--------------|--------------------|--------------------|------------------|
| Closest Pair | Naive              | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | O(n log n)         | O(n)             |

If you are interested in contributing algorithms, please check the [contributing guidelines](CONTRIBUTING.md).
