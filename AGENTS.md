# AGENTS.md

## Project overview

`komp-geom` is a Kotlin Multiplatform computational geometry library. The project targets JVM, JavaScript, WebAssembly, Linux Native, and several Apple/Windows/Android Native platforms.

Check `build.gradle.kts` for the current version; the API is still allowed to change before `1.0.0`.

## Repository layout

- `src/commonMain/kotlin/io/github/cponfick/kompgeom/` — production code shared across platforms
  - `algorithms/` — geometry algorithms such as closest pair and convex hull
  - `core/` — shared abstractions, precision/equivalence utilities, and shape interfaces
  - `euclidean/oned/` — one-dimensional geometry
  - `euclidean/twod/` — two-dimensional geometry
  - `euclidean/threed/` — three-dimensional geometry
- `src/commonTest/kotlin/` — multiplatform unit tests, matching the production package structure
- `src/commonBenchmark/kotlin/` — benchmarks
- `docs/` — benchmarks and tracked, generated Dokka documentation under `docs/dokka/`
- `gradle/libs.versions.toml` — dependency and plugin versions
- `build.gradle.kts` — Kotlin Multiplatform targets, formatting, publishing, documentation, coverage, and benchmarks

## Local development

Use the Gradle wrapper from the repository root:

```bash
./gradlew spotlessApply   # format Kotlin, Gradle Kotlin DSL, and Markdown/YAML
./gradlew spotlessCheck   # verify formatting without changing files
./gradlew allTests        # run all tests available on this host (JS/Wasm browser tests require Chrome)
```

The project configures a Java 17 toolchain. Install/configure a JDK 17 or newer before running JVM/Gradle tasks. CI runs JVM tests on JDK 17, 21, and 25, and runs platform-specific tests on the corresponding operating systems.

Use `allTests` for verification rather than a single target-specific test task. Install Chrome (or set `CHROME_BIN` to its executable) for JS/Wasm browser tests. Gradle runs the targets available on the current host; CI covers additional hosts and targets.

## Implementation conventions

Before changing geometry behavior, inspect the relevant interface, corresponding implementations in other dimensions, comparison semantics in `core/equivalence/`, and matching `commonTest` tests. Preserve established API and edge-case behavior unless the task explicitly changes it.

- Put platform-independent functionality in `commonMain`; avoid `expect`/`actual` unless platform behavior genuinely requires it.
- Add new tests to `commonTest` whenever behavior is shared across targets.
- Preserve the existing immutable-by-default API. Mutable variants use names such as `MutableVec2` and `MutableSeg3`.
- Use `DoubleEquivalence`/`EpsilonDoubleEquivalence` for floating-point comparisons instead of direct equality in geometric calculations.
- Follow the existing package and naming patterns for 1D, 2D, and 3D types.
- Public APIs require explicit visibility because `explicitApi()` is enabled. Add KDoc for new public types and members.
- Keep algorithms deterministic and account for degenerate inputs, duplicate points, collinear points, and floating-point tolerance where applicable.

## Testing and changes

- Add or update focused tests for every behavior change, including edge cases and invalid/degenerate inputs.
- Prefer shared tests in `src/commonTest` so behavior is checked across supported platforms.
- Run `spotlessApply`, `spotlessCheck`, and `allTests` before committing.
- Do not edit generated files under `build/` or hand-edit the tracked Dokka output under `docs/dokka/`. Regenerate and commit Dokka output only when the task calls for a documentation update.
- Keep changes focused; update README/docs when changing public behavior or usage.
- Keep `docs/ci-cd.md` short and practical.

## Pull requests

Before submitting a change:

1. Run `./gradlew spotlessApply`.
2. Run `./gradlew spotlessCheck`.
3. Run `./gradlew allTests`.
4. Review the diff for accidental generated files or unrelated changes.
5. Use a clear commit/PR description and explain API or precision-related changes.

CI checks formatting, JVM tests, JavaScript/WebAssembly tests, and native platform tests. Some native targets can only be tested on their required host OS.
