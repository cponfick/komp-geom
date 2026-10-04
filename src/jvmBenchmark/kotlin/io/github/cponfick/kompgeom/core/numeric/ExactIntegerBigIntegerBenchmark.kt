package io.github.cponfick.kompgeom.core.numeric

import java.math.BigInteger
import kotlinx.benchmark.*

/** JVM-only performance reference for [ExactInteger] against [BigInteger]. */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class ExactIntegerBigIntegerBenchmark {
  @Param("64", "256", "2048", "8192") var bitCount: Int = 0

  private lateinit var exactLeft: ExactInteger
  private lateinit var exactRight: ExactInteger
  private lateinit var exactCancellation: ExactInteger
  private lateinit var exactSingleLimbDivisor: ExactInteger
  private lateinit var exactMultiLimbDivisor: ExactInteger
  private lateinit var exactSharedLeft: ExactInteger
  private lateinit var exactSharedRight: ExactInteger
  private lateinit var bigLeft: BigInteger
  private lateinit var bigRight: BigInteger
  private lateinit var bigCancellation: BigInteger
  private lateinit var bigSingleLimbDivisor: BigInteger
  private lateinit var bigMultiLimbDivisor: BigInteger
  private lateinit var bigSharedLeft: BigInteger
  private lateinit var bigSharedRight: BigInteger

  @Setup
  fun setup() {
    val inputs = ExactIntegerBenchmarkInputs(bitCount)
    exactLeft = inputs.left.toExactInteger()
    exactRight = inputs.right.toExactInteger()
    exactCancellation = exactLeft - (exactLeft shr 1)
    exactSingleLimbDivisor = inputs.singleLimbDivisor.toExactInteger()
    exactMultiLimbDivisor = inputs.multiLimbDivisor.toExactInteger()
    val exactShared = inputs.shared.toExactInteger()
    exactSharedLeft = exactShared * inputs.gcdLeftFactor.toExactInteger()
    exactSharedRight = exactShared * inputs.gcdRightFactor.toExactInteger()

    bigLeft = inputs.left.toBigInteger()
    bigRight = inputs.right.toBigInteger()
    bigCancellation = bigLeft.subtract(bigLeft.shiftRight(1))
    bigSingleLimbDivisor = inputs.singleLimbDivisor.toBigInteger()
    bigMultiLimbDivisor = inputs.multiLimbDivisor.toBigInteger()
    val bigShared = inputs.shared.toBigInteger()
    bigSharedLeft = bigShared.multiply(inputs.gcdLeftFactor.toBigInteger())
    bigSharedRight = bigShared.multiply(inputs.gcdRightFactor.toBigInteger())
  }

  @Benchmark fun exactAdd(bh: Blackhole) = bh.consume(exactLeft + exactRight)

  @Benchmark fun bigAdd(bh: Blackhole) = bh.consume(bigLeft.add(bigRight))

  @Benchmark fun exactSubtract(bh: Blackhole) = bh.consume(exactLeft - exactCancellation)

  @Benchmark fun bigSubtract(bh: Blackhole) = bh.consume(bigLeft.subtract(bigCancellation))

  @Benchmark fun exactMultiply(bh: Blackhole) = bh.consume(exactLeft * exactRight)

  @Benchmark fun bigMultiply(bh: Blackhole) = bh.consume(bigLeft.multiply(bigRight))

  @Benchmark
  fun exactDivideAndRemainder(bh: Blackhole) {
    val result = exactLeft.divideAndRemainder(exactRight)
    bh.consume(result.first)
    bh.consume(result.second)
  }

  @Benchmark
  fun bigDivideAndRemainder(bh: Blackhole) {
    val result = bigLeft.divideAndRemainder(bigRight)
    bh.consume(result[0])
    bh.consume(result[1])
  }

  @Benchmark fun exactGcd(bh: Blackhole) = bh.consume(exactSharedLeft.gcd(exactSharedRight))

  @Benchmark fun bigGcd(bh: Blackhole) = bh.consume(bigSharedLeft.gcd(bigSharedRight))

  @Benchmark
  fun exactDivideBySingleLimb(bh: Blackhole) {
    val (quotient, remainder) = exactLeft.divideAndRemainder(exactSingleLimbDivisor)
    bh.consume(quotient)
    bh.consume(remainder)
  }

  @Benchmark
  fun bigDivideBySingleLimb(bh: Blackhole) {
    val result = bigLeft.divideAndRemainder(bigSingleLimbDivisor)
    bh.consume(result[0])
    bh.consume(result[1])
  }

  @Benchmark
  fun exactDivideByMultiLimb(bh: Blackhole) {
    val (quotient, remainder) = exactLeft.divideAndRemainder(exactMultiLimbDivisor)
    bh.consume(quotient)
    bh.consume(remainder)
  }

  @Benchmark
  fun bigDivideByMultiLimb(bh: Blackhole) {
    val result = bigLeft.divideAndRemainder(bigMultiLimbDivisor)
    bh.consume(result[0])
    bh.consume(result[1])
  }

  private fun BenchmarkMagnitude.toBigInteger(): BigInteger =
    build(BigInteger.ZERO) { value, chunk ->
      value.shiftLeft(30).add(BigInteger.valueOf(chunk.toLong()))
    }
}
