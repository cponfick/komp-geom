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
./gradlew jvmTest         # run JVM tests
./gradlew jsTest          # run JavaScript tests; requires Chrome for Karma
./gradlew wasmJsTest      # run WebAssembly tests; requires Chrome for Karma
./gradlew linuxX64Test    # run Linux Native tests
```

The project configures a Java 17 toolchain. Install/configure a JDK 17 or newer before running JVM/Gradle tasks. CI runs JVM tests on JDK 17, 21, and 25, and runs platform-specific tests on the corresponding operating systems.

There is no generic root `test` task; use target-specific tasks such as `jvmTest`.

## Implementation conventions

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
- Run `spotlessApply` before committing and then run the relevant target tests.
- Do not edit generated files under `build/` or hand-edit the tracked Dokka output under `docs/dokka/`. Regenerate and commit Dokka output only when the task calls for a documentation update.
- Keep changes focused; update README/docs when changing public behavior or usage.

## Pull requests

Before submitting a change:

1. Run `./gradlew spotlessApply`.
2. Run `./gradlew spotlessCheck`.
3. Run the relevant test task(s), at minimum `./gradlew jvmTest` when a JDK 17 toolchain is available.
4. Review the diff for accidental generated files or unrelated changes.
5. Use a clear commit/PR description and explain API or precision-related changes.

CI checks formatting, JVM tests, JavaScript/WebAssembly tests, and native platform tests. Some native targets can only be tested on their required host OS.
