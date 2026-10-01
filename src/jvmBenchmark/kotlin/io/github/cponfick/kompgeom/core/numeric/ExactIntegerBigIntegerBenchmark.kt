package io.github.cponfick.kompgeom.core.numeric

import java.math.BigInteger
import kotlin.random.Random
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
  private lateinit var exactDivisor: ExactInteger
  private lateinit var exactSharedLeft: ExactInteger
  private lateinit var exactSharedRight: ExactInteger
  private lateinit var bigLeft: BigInteger
  private lateinit var bigRight: BigInteger
  private lateinit var bigDivisor: BigInteger
  private lateinit var bigSharedLeft: BigInteger
  private lateinit var bigSharedRight: BigInteger

  @Setup
  fun setup() {
    exactLeft = exactOperand(bitCount, 0x13579BDF)
    exactRight = -exactOperand((bitCount * 3) / 4, 0x2468ACE1)
    exactDivisor = exactOperand(maxOf(32, bitCount / 2), 0x31415926)
    val exactShared = exactOperand(maxOf(32, bitCount / 3), 0x55AA55AA)
    exactSharedLeft = exactShared * exactOperand(bitCount - exactShared.bitLength() + 1, 0x10203040)
    exactSharedRight =
      exactShared * exactOperand(bitCount - exactShared.bitLength() + 1, 0x50607080)

    bigLeft = bigOperand(bitCount, 0x13579BDF)
    bigRight = bigOperand((bitCount * 3) / 4, 0x2468ACE1).negate()
    bigDivisor = bigOperand(maxOf(32, bitCount / 2), 0x31415926)
    val bigShared = bigOperand(maxOf(32, bitCount / 3), 0x55AA55AA)
    bigSharedLeft = bigShared.multiply(bigOperand(bitCount - bigShared.bitLength() + 1, 0x10203040))
    bigSharedRight =
      bigShared.multiply(bigOperand(bitCount - bigShared.bitLength() + 1, 0x50607080))
  }

  @Benchmark fun exactAdd(bh: Blackhole) = bh.consume(exactLeft + exactRight)

  @Benchmark fun bigAdd(bh: Blackhole) = bh.consume(bigLeft.add(bigRight))

  @Benchmark fun exactSubtract(bh: Blackhole) = bh.consume(exactLeft - exactRight)

  @Benchmark fun bigSubtract(bh: Blackhole) = bh.consume(bigLeft.subtract(bigRight))

  @Benchmark fun exactMultiply(bh: Blackhole) = bh.consume(exactLeft * exactRight)

  @Benchmark fun bigMultiply(bh: Blackhole) = bh.consume(bigLeft.multiply(bigRight))

  @Benchmark
  fun exactDivideAndRemainder(bh: Blackhole) {
    val result = exactLeft.divideAndRemainder(exactDivisor)
    bh.consume(result.first)
    bh.consume(result.second)
  }

  @Benchmark
  fun bigDivideAndRemainder(bh: Blackhole) {
    val result = bigLeft.divideAndRemainder(bigDivisor)
    bh.consume(result[0])
    bh.consume(result[1])
  }

  @Benchmark fun exactGcd(bh: Blackhole) = bh.consume(exactSharedLeft.gcd(exactSharedRight))

  @Benchmark fun bigGcd(bh: Blackhole) = bh.consume(bigSharedLeft.gcd(bigSharedRight))

  private fun exactOperand(bits: Int, seed: Int): ExactInteger {
    if (bits <= 0) return ExactInteger.ONE
    val random = Random(seed)
    var result = ExactInteger.ZERO
    var remaining = bits
    while (remaining > 0) {
      val width = minOf(30, remaining)
      val chunk = random.nextInt(1 shl width)
      result = (result shl width) + ExactInteger.fromLong(chunk.toLong())
      remaining -= width
    }
    return result + ExactInteger.ONE
  }

  private fun bigOperand(bits: Int, seed: Int): BigInteger {
    if (bits <= 0) return BigInteger.ONE
    val random = Random(seed)
    var result = BigInteger.ZERO
    var remaining = bits
    while (remaining > 0) {
      val width = minOf(30, remaining)
      val chunk = random.nextInt(1 shl width)
      result = result.shiftLeft(width).add(BigInteger.valueOf(chunk.toLong()))
      remaining -= width
    }
    return result.add(BigInteger.ONE)
  }
}
