# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

KompGeom is a Kotlin Multiplatform (KMP) computational geometry library published to Maven Central (`io.github.cponfick:komp-geom`). It targets JVM, JS, WebAssembly, and Native (Linux, macOS, iOS, Windows, tvOS, watchOS, Android Native). Pre-1.0 API — breaking changes are expected.

## Build & Development Commands

```bash
./gradlew build                    # Full build (all targets)
./gradlew jvmTest                  # JVM tests only (fastest feedback loop)
./gradlew allTests                 # All platform tests
./gradlew spotlessCheck            # Check formatting
./gradlew spotlessApply            # Auto-fix formatting (ktfmt, Google style)
./gradlew koverReport              # Code coverage report
```

Run a single test class on JVM:
```bash
./gradlew jvmTest --tests "io.github.cponfick.kompgeom.path.to.TestClass"
```

JS/WASM browser tests use Karma with Chrome Headless. Native tests run directly on the host platform (e.g., `./gradlew linuxX64Test`).

## Commit Conventions

This project uses [Conventional Commits](https://www.conventionalcommits.org/) via Commitizen. Commit messages and PR titles must follow the format `type(scope): description`. Common types: `feat`, `fix`, `docs`, `ci`, `build`, `refactor`, `test`.

## Code Style

- **Formatter**: Spotless with ktfmt (Google style). Always run `spotlessApply` before committing.
- **Explicit API mode** is enabled — all public declarations need explicit visibility modifiers.
- **KDoc** is required on all public APIs.
- JDK 17 toolchain.

## Architecture

### Core type hierarchy

All geometry lives under `io.github.cponfick.kompgeom`:

- **`core/`** — Interfaces and abstractions
  - `Spatial` → `Vector<V>` → `Vector1<V>`, `Vector2<V>`, `Vector3<V>` — dimension-specific vector contracts
  - `Distanceable<P>` — distance computation
  - `equivalence/` — `DoubleEquivalence` interface with `EpsilonDoubleEquivalence` (default epsilon `1e-10`). Most geometric types accept an optional `DoubleEquivalence` parameter for precision-aware comparisons.
  - `shapes/` — `Line<V>`, `Polygon<V>`, `Segment<V>`, `Intersection`
  - `transform/` — `Transformer<T>` interface

- **`euclidean/`** — Concrete implementations per dimension
  - `oned/` — `Vec1`, `MutableVec1`, `AffineTransformationMatrix1`
  - `twod/` — `Vec2`, `MutableVec2`, `Seg2`, `MutableSeg2`, `Line2`, `Polygon2`, `AffineTransformationMatrix2`, `PolarCoordinates`
  - `threed/` — `Vec3`, `MutableVec3`, `Seg3`, `MutableSeg3`, `Line3`, `Polygon3`, `AffineTransformationMatrix3`
  - `internal/` — `VectorUtil`, `MatrixUtil` (shared math helpers)

- **`algorithms/`** — `Algorithm<Output>` fun interface + `AlgorithmInfo` companion pattern
  - `closestpair/` — `ClosestPairNaive` (O(n²)), `ClosestPairDivideAndConquer` (O(n log n))
  - `convexhull/` — `Quickhull2` (O(n log n))

### Key design patterns

- **Immutable-first**: `Vec2`, `Seg2`, etc. are data classes. `MutableVec2`, `MutableSeg2` exist for performance-critical paths (in-place operations).
- **Algorithm framework**: Each algorithm class implements `Algorithm<Output>`, and its companion object implements `AlgorithmInfo` providing group, name, time/space complexity metadata.
- **Precision system**: `DoubleEquivalence` is threaded through geometric operations. `DEFAULT_DOUBLE_EQUIVALENCE` uses relative epsilon `1e-10`.

## Testing

- Tests are in `src/commonTest/kotlin/` mirroring the source package structure.
- Framework: Kotlin Test + Kotest assertions (`shouldBe`, etc.).
- Test names use backtick-enclosed descriptive strings.
- CI tests across JVM (Java 17, 21, 25), JS, WASM, and multiple native targets.