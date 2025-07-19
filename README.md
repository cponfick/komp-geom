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

## Usage Example

Usage examples are provided inside the [documentation](https://cponfick.github.io/komp-geom/).

## Implemented Algorithms

Following a list of implemented algorithms.

| Algorithm    | Implementation     | Runtime Complexity | Space Complexity |
|--------------|--------------------|--------------------|------------------|
| Closest Pair | Naive              | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | O(n log n)         | O(n)             |

If you are interested in contributing algorithms, please check the [contributing guidelines](CONTRIBUTING.md).
