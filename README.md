[![Continuous Integration](https://github.com/cponfick/komp-geom/actions/workflows/ci.yaml/badge.svg?branch=main)](https://github.com/cponfick/komp-geom/actions/workflows/ci.yml) ![Maven Central Version](https://img.shields.io/maven-central/v/io.github.cponfick/komp-geom)
# Computational Geometry in Kotlin 

This repository contains a collection of algorithms and data structures for computational geometry implemented in KMP (
Kotlin Multiplatform). The focus is on providing efficient solutions to common geometric problems on multiple platforms.

Currently, following platforms are supported:

- JVM
- JS

## Implemented Algorithms

| Algorithm    | Implementation     | Runtime Complexity | Space Complexity |
|--------------|--------------------|--------------------|------------------|
| Closest Pair | Naive              | O(n^2)             | O(1)             |
| Closest Pair | Divide and Conquer | O(n log n)         | O(n)             |