# Affine transformation benchmark

## Purpose

This benchmark measures the cost of applying a 3D affine transformation to collections of
vectors. It compares the immutable `Vec3` API with the mutable `MutableVec3` API across the JVM,
JavaScript, and Linux X64 targets.

The benchmark answers two practical questions:

1. How much overhead does the immutable transformation path introduce at different collection
   sizes?
2. Does the relative benefit of mutable vectors change between targets?

The benchmark measures execution cost only. It does not establish that one API is universally
preferable: immutable values provide simpler ownership semantics, while mutable values can reduce
allocation and garbage-collection pressure.

## Workload

Each benchmark creates a collection of vectors, applies the same affine transformation to every
vector, and consumes the sum of the transformed vectors. The transformation combines scaling,
rotation, and translation. The two paths are:

- **Immutable:** transforms `Vec3` values and creates new vector instances.
- **Mutable:** transforms `MutableVec3` values in place where the API permits it.

The collection sizes are 10,000, 100,000, and 1,000,000 vectors. Input vectors and the
transformation are constructed during setup rather than inside the timed operation.

Source benchmark:

```text
src/commonBenchmark/kotlin/io/github/cponfick/kompgeom/AffineTransformationBenchmark.kt
```

## Methodology

The benchmark uses kotlinx-benchmark with the following configuration:

- Mode: average time
- Warmup: 10 iterations of 500 ms
- Measurement: 10 iterations of 5 seconds
- Unit: milliseconds per benchmark invocation
- Kotlin/KMP: 2.2 for the recorded run
- JVM: Java 21 for the recorded run
- Native target: Linux X64

The reported operation includes transforming the complete collection and reducing the transformed
vectors to a sum. Results are consumed by the benchmark harness so the computation cannot be
removed as dead code.

The recorded run used Fedora 43 Workstation Edition on an Intel Core i7-9750H with 12 logical
processors and 16 GB RAM. Absolute timings are machine- and runtime-dependent; the relative
comparisons should be treated as more portable than the raw values.

## Results

The following table reports average milliseconds per invocation. The error is the benchmark's
reported error interval.

| target | implementation | 10,000 vectors | 100,000 vectors | 1,000,000 vectors |
| --- | --- | ---: | ---: | ---: |
| JVM | immutable | 0.163 +/- 0.003 | 1.947 +/- 0.014 | 46.081 +/- 1.202 |
| JVM | mutable | 0.093 +/- 0.002 | 0.873 +/- 0.013 | 17.318 +/- 1.588 |
| JavaScript | immutable | 0.643 +/- 0.027 | 17.657 +/- 0.316 | 213.981 +/- 3.269 |
| JavaScript | mutable | 0.541 +/- 0.004 | 6.872 +/- 0.054 | 82.758 +/- 1.566 |
| Linux X64 | immutable | 0.665 +/- 0.005 | 8.853 +/- 0.037 | 110.505 +/- 9.807 |
| Linux X64 | mutable | 0.479 +/- 0.002 | 5.157 +/- 0.007 | 52.539 +/- 0.255 |

### Mutable speedup

Speedup is calculated as `immutable time / mutable time`.

| target | 10,000 vectors | 100,000 vectors | 1,000,000 vectors |
| --- | ---: | ---: | ---: |
| JVM | 1.75x | 2.23x | 2.66x |
| JavaScript | 1.19x | 2.57x | 2.59x |
| Linux X64 | 1.39x | 1.72x | 2.10x |

The mutable path is faster in every recorded case. The advantage is small for the 10,000-vector
JavaScript case and grows at larger collection sizes, where allocation and memory-management costs
become more visible.

## Interpretation

The results support three observations:

- The immutable path has a measurable cost at all targets.
- The cost becomes more significant as the collection grows, especially on the JVM and JavaScript.
- The mutable path provides the largest recorded benefit for the 1,000,000-vector JVM workload,
  with a 2.66x speedup.

The results are consistent with reduced temporary-object allocation in the mutable path. They do
not isolate allocation from other implementation differences, and they should not be interpreted as
an exact garbage-collection or memory-use measurement because this benchmark does not include heap
profiling.

For small collections, the absolute difference may be less important than API safety and ownership
clarity. For large transformation workloads where mutation is acceptable, the mutable API can
provide a substantial performance benefit.

## Reproduction and limitations

The benchmark is available through the project's existing benchmark tasks. The exact task names
may vary with the Kotlin target configuration, but the configured targets are:

```text
./gradlew jvmBenchmarkBenchmark
./gradlew jsBenchmarkBenchmark
./gradlew linuxX64BenchmarkBenchmark
```

The recorded results are historical benchmark data rather than a continuously maintained
performance guarantee. Changes to the Kotlin compiler, JVM, JavaScript runtime, Native compiler,
hardware, collection implementation, or transformation code require a new run. Future benchmark
runs should additionally record allocation and peak-memory data when evaluating changes intended to
reduce object creation.
