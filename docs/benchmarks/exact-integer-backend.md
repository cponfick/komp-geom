# ExactInteger benchmark

## Purpose

This benchmark measures the internal `ExactInteger` implementation used by the exact-input numeric
foundation. It answers two practical questions:

1. Is the implementation fast enough for binary64 decoding and small exact predicates?
2. Which operations need optimization before exact rational geometry uses larger intermediate values?

The benchmark is evidence about arithmetic cost. It is not a public API decision, a correctness
specification, or a claim that JVM `BigInteger` can be used in production `commonMain`.

## Implementations

`ExactInteger` is an immutable signed-magnitude integer stored as little-endian base-2^30 limbs.
The current implementation uses schoolbook multiplication, a single-limb fast path plus
bit-at-a-time multi-limb division, and binary GCD.

The JVM-only comparison uses `java.math.BigInteger`. It is included only as a mature performance
reference; it is not a production dependency. Both implementations receive operands generated from
the same deterministic 30-bit chunks.

Benchmark sources:

- `src/commonBenchmark/kotlin/io/github/cponfick/kompgeom/core/numeric/ExactIntegerBenchmark.kt`
- `src/jvmBenchmark/kotlin/io/github/cponfick/kompgeom/core/numeric/ExactIntegerBigIntegerBenchmark.kt`

## Methodology

Operands are constructed in `@Setup`, outside timed sections. The benchmark uses deterministic
seeds and approximately 64-, 256-, 2,048-, and 8,192-bit operands. It measures:

- Addition
- Cancellation-heavy subtraction
- Comparison
- Multiplication
- Left and right shifts
- Quotient/remainder
- GCD

The mixed-size cases use a 3:4 size ratio. GCD operands contain a deterministic shared factor.
All results are consumed with `Blackhole`; immutable result allocation is therefore included in
the measurement, while operand construction is excluded.

The benchmark configuration is:

- Mode: average time
- Warmup: 5 iterations of 500 ms
- Measurement: 5 iterations of 1 second
- Output unit: microseconds per operation
- Kotlin: 2.4.0
- kotlinx-benchmark: 0.4.17
- JVM: JDK 17

Run the JVM suite with:

```text
./gradlew jvmBenchmarkBenchmark
```

The existing project also exposes these cross-target benchmark tasks:

```text
./gradlew jsBenchmarkBenchmark
./gradlew linuxX64BenchmarkBenchmark
```

The JS and Linux Native benchmark sources compile successfully. This report contains JVM runtime
measurements only. Wasm does not currently have a benchmark compilation in the Gradle setup, so
Wasm runtime and linked-size results remain an explicit measurement gap.

## ExactInteger baseline

The following results are from the full JVM benchmark run. Values are average microseconds per
operation; the error is the reported 99.9% confidence interval.

| operation | 64 bit | 256 bit | 2,048 bit | 8,192 bit |
| --- | ---: | ---: | ---: | ---: |
| add | 0.024 +/- 0.003 | 0.034 +/- 0.001 | 0.127 +/- 0.001 | 0.479 +/- 0.026 |
| subtract | 0.030 +/- 0.002 | 0.047 +/- 0.004 | 0.157 +/- 0.005 | 0.561 +/- 0.013 |
| compare | 0.001 +/- 0.001 | 0.001 +/- 0.001 | 0.001 +/- 0.001 | 0.001 +/- 0.001 |
| multiply | 0.035 +/- 0.001 | 0.128 +/- 0.003 | 3.994 +/- 0.023 | 64.898 +/- 6.096 |
| shift left | 0.026 +/- 0.002 | 0.032 +/- 0.001 | 0.123 +/- 0.003 | 0.466 +/- 0.006 |
| shift right | 0.032 +/- 0.001 | 0.043 +/- 0.001 | 0.123 +/- 0.017 | 0.210 +/- 0.006 |
| quotient/remainder | 0.602 +/- 0.038 | 3.716 +/- 0.026 | 178.145 +/- 8.633 | 2680.630 +/- 43.086 |
| GCD | 1.854 +/- 0.008 | 10.331 +/- 0.365 | 436.151 +/- 6.511 | 5777.889 +/- 95.465 |

Comparison and baseline methods use separate benchmark classes, so small differences between their
`ExactInteger` values are expected from JVM run conditions. The comparison table below is the
appropriate source for relative conclusions.

## Comparison with BigInteger

The ratio is `ExactInteger / BigInteger`; higher values mean that `ExactInteger` is slower.

| operation | size | ExactInteger | BigInteger | ratio |
| --- | ---: | ---: | ---: | ---: |
| add | 64 | 0.022 | 0.014 | 1.6x |
| add | 2,048 | 0.127 | 0.060 | 2.1x |
| add | 8,192 | 0.600 | 0.229 | 2.6x |
| subtract | 64 | 0.033 | 0.014 | 2.4x |
| subtract | 2,048 | 0.163 | 0.065 | 2.5x |
| subtract | 8,192 | 0.560 | 0.213 | 2.6x |
| multiply | 64 | 0.044 | 0.013 | 3.4x |
| multiply | 2,048 | 4.480 | 0.795 | 5.6x |
| multiply | 8,192 | 62.368 | 15.823 | 3.9x |
| quotient/remainder | 64 | 0.061 | 0.051 | 1.2x |
| quotient/remainder | 2,048 | 151.187 | 2.612 | 57.9x |
| quotient/remainder | 8,192 | 2551.760 | 23.770 | 107.3x |
| GCD | 64 | 1.785 | 0.449 | 4.0x |
| GCD | 2,048 | 445.462 | 86.917 | 5.1x |
| GCD | 8,192 | 5706.905 | 1145.788 | 5.0x |

## Findings

### Binary64-sized values are inexpensive

Finite binary64 values contain at most 53 significant bits. The 64-bit cases are therefore the
closest representation of the primary current use case. Basic arithmetic is in the tens of
nanoseconds, and quotient/remainder is approximately 0.06 microseconds in the comparison run.
This is adequate for decoding values and small exact expressions.

### Division is the main scalability limitation

At 2,048 bits, quotient/remainder is approximately 58 times slower than `BigInteger`; at 8,192
bits, it is approximately 107 times slower. The current bit-at-a-time long-division algorithm is
the direct cause of this scaling gap.

### Multiplication and GCD are slower but less urgent

Schoolbook multiplication is approximately 4--6 times slower in the tested cases. Binary GCD is
approximately 5 times slower. These costs may matter for rational normalization, but a topology
implementation can often reduce division and GCD frequency by retaining homogeneous values or
postponing normalization.

Comparison results near 0.001 microseconds are below useful timer resolution and should not be
interpreted as precise measurements.

## Interpretation and follow-up

The current implementation is suitable as a correctness/reference backend for the binary64
foundation. It is not yet competitive with `BigInteger` for large rational arithmetic.

Before exact rational geometry uses large operands in hot paths, the next measurements should use
realistic rational workloads and record operation frequency, operand growth, and allocation
pressure. If division is confirmed as a bottleneck, investigate normalized multi-limb division
first. Then evaluate Lehmer or half-GCD and a size-thresholded Karatsuba multiplication path only
if profiling justifies their additional complexity.

No JVM, JS, Native, or Wasm runtime result should be generalized to the other targets. Cross-target
measurements, memory/allocation profiling, and linked-size comparisons remain future work.
