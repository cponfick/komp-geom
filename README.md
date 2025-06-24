[![Continuous Integration](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml) [![CodeQL Advanced](https://github.com/cponfick/komp-geom/actions/workflows/codeql.yml/badge.svg)](https://github.com/cponfick/komp-geom/actions/workflows/codeql.yml) [![Maven Central Version](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)](https://central.sonatype.com/artifact/io.github.cponfick/komp-geom/overview)

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

TODO: Add usage examples when the API is more stable.

## Foundation

TODO: Add documentation on the foundation of the library, including key concepts, data structures, and algorithms.

## Implemented Algorithms

| Algorithm    | Implementation     | Runtime Complexity | Space Complexity |
|--------------|--------------------|--------------------|------------------|
| Closest Pair | Naive              | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | O(n log n)         | O(n)             |
