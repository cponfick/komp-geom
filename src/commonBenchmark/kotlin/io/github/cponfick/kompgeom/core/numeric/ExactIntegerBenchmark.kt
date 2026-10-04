package io.github.cponfick.kompgeom.core.numeric

import kotlinx.benchmark.*

/** Benchmarks for the internal integer backend at the sizes used by exact predicates. */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class ExactIntegerBenchmark {
  @Param("64", "256", "2048", "8192") var bitCount: Int = 0

  private lateinit var left: ExactInteger
  private lateinit var right: ExactInteger
  private lateinit var cancellation: ExactInteger
  private lateinit var singleLimbDivisor: ExactInteger
  private lateinit var multiLimbDivisor: ExactInteger
  private lateinit var gcdLeft: ExactInteger
  private lateinit var gcdRight: ExactInteger

  @Setup
  fun setup() {
    val inputs = ExactIntegerBenchmarkInputs(bitCount)
    left = inputs.left.toExactInteger()
    right = inputs.right.toExactInteger()
    cancellation = left - (left shr 1)
    singleLimbDivisor = inputs.singleLimbDivisor.toExactInteger()
    multiLimbDivisor = inputs.multiLimbDivisor.toExactInteger()
    val shared = inputs.shared.toExactInteger()
    gcdLeft = shared * inputs.gcdLeftFactor.toExactInteger()
    gcdRight = shared * inputs.gcdRightFactor.toExactInteger()
  }

  @Benchmark fun add(bh: Blackhole) = bh.consume(left + right)

  @Benchmark fun subtract(bh: Blackhole) = bh.consume(left - cancellation)

  @Benchmark fun compare(bh: Blackhole) = bh.consume(left.compareTo(right))

  @Benchmark fun multiply(bh: Blackhole) = bh.consume(left * right)

  @Benchmark fun shiftLeft(bh: Blackhole) = bh.consume(left shl (bitCount / 3))

  @Benchmark fun shiftRight(bh: Blackhole) = bh.consume(left shr (bitCount / 3))

  @Benchmark
  fun quotientAndRemainder(bh: Blackhole) {
    val result = left.divideAndRemainder(right)
    bh.consume(result.first)
    bh.consume(result.second)
  }

  @Benchmark
  fun divideBySingleLimb(bh: Blackhole) {
    val (quotient, remainder) = left.divideAndRemainder(singleLimbDivisor)
    bh.consume(quotient)
    bh.consume(remainder)
  }

  @Benchmark
  fun divideByMultiLimb(bh: Blackhole) {
    val (quotient, remainder) = left.divideAndRemainder(multiLimbDivisor)
    bh.consume(quotient)
    bh.consume(remainder)
  }

  @Benchmark fun gcd(bh: Blackhole) = bh.consume(gcdLeft.gcd(gcdRight))
}
