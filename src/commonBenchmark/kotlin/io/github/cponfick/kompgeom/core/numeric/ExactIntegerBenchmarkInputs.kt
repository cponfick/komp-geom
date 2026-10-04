package io.github.cponfick.kompgeom.core.numeric

import kotlin.random.Random

/** Shared operand specifications for common benchmarks and the JVM BigInteger comparison. */
internal class ExactIntegerBenchmarkInputs(bitCount: Int) {
  internal val left: BenchmarkMagnitude = BenchmarkMagnitude.random(bitCount, 0x13579BDF)
  internal val right: BenchmarkMagnitude = BenchmarkMagnitude.random((bitCount * 3) / 4, 0x2468ACE1)
  internal val shared: BenchmarkMagnitude =
    BenchmarkMagnitude.random(maxOf(32, bitCount / 3), 0x55AA55AA)
  internal val gcdLeftFactor: BenchmarkMagnitude =
    BenchmarkMagnitude.random(bitCount - maxOf(32, bitCount / 3) + 1, 0x10203040)
  internal val gcdRightFactor: BenchmarkMagnitude =
    BenchmarkMagnitude.random(bitCount - maxOf(32, bitCount / 3) + 1, 0x50607080)
  internal val singleLimbDivisor: BenchmarkMagnitude = BenchmarkMagnitude.singleLimbDivisor()
  internal val multiLimbDivisor: BenchmarkMagnitude = BenchmarkMagnitude.multiLimbDivisor()
}

/** An immutable positive magnitude specified as big-endian base-2^30 chunks. */
internal class BenchmarkMagnitude private constructor(private val chunks: IntArray) {
  /** Builds either backend from the same chunks, outside the timed operation. */
  internal fun <T> build(zero: T, appendChunk: (T, Int) -> T): T {
    var result = zero
    for (chunk in chunks) result = appendChunk(result, chunk)
    return result
  }

  internal fun toExactInteger(): ExactInteger =
    build(ExactInteger.ZERO) { value, chunk ->
      (value shl 30) + ExactInteger.fromLong(chunk.toLong())
    }

  internal companion object {
    /** Generates exactly [bits] significant bits, avoiding seed-dependent size/path changes. */
    internal fun random(bits: Int, seed: Int): BenchmarkMagnitude {
      require(bits > 0) { "operand bit count must be positive" }
      val random = Random(seed)
      val chunks = IntArray((bits + 29) / 30)
      val highWidth = (bits - 1) % 30 + 1
      chunks[0] = random.nextInt(1 shl highWidth) or (1 shl (highWidth - 1))
      for (index in 1 until chunks.size) chunks[index] = random.nextInt(1 shl 30)
      return BenchmarkMagnitude(chunks)
    }

    internal fun singleLimbDivisor(): BenchmarkMagnitude =
      BenchmarkMagnitude(intArrayOf((1 shl 30) - 1))

    internal fun multiLimbDivisor(): BenchmarkMagnitude = BenchmarkMagnitude(intArrayOf(1, 0))
  }
}
