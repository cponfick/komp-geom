package io.github.cponfick.kompgeom.core.numeric

import kotlin.random.Random
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
  private lateinit var gcdLeft: ExactInteger
  private lateinit var gcdRight: ExactInteger

  @Setup
  fun setup() {
    // Construction is deliberately outside timed methods. A fixed seed makes shapes identical on
    // every target while avoiding decimal parsing or a platform-specific random implementation.
    left = operand(bitCount, 0x13579BDF)
    right = operand((bitCount * 3) / 4, 0x2468ACE1)
    cancellation = left - (left shr 1)
    val shared = operand(maxOf(32, bitCount / 3), 0x55AA55AA)
    gcdLeft = shared * operand(bitCount - shared.bitLength() + 1, 0x10203040)
    gcdRight = shared * operand(bitCount - shared.bitLength() + 1, 0x50607080)
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

  @Benchmark fun gcd(bh: Blackhole) = bh.consume(gcdLeft.gcd(gcdRight))

  private fun operand(bits: Int, seed: Int): ExactInteger {
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
}
