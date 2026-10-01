package io.github.cponfick.kompgeom.core.numeric

import java.math.BigInteger
import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/** Differential checks against the JDK oracle; this source set is deliberately JVM-only. */
class ExactIntegerJvmDifferentialTest {
  @Test
  fun `matches BigInteger for arithmetic division shifts and gcd`() {
    val random = Random(0x1885eedL)
    repeat(120) {
      val leftOracle = randomInteger(random, 1 + random.nextInt(1800))
      val rightOracle = randomInteger(random, 1 + random.nextInt(1800))
      val left = fromBigInteger(leftOracle)
      val right = fromBigInteger(rightOracle)

      assertOracle(left + right, leftOracle + rightOracle)
      assertOracle(left - right, leftOracle - rightOracle)
      assertOracle(left * right, leftOracle * rightOracle)
      assertOracle(left.gcd(right), leftOracle.gcd(rightOracle))

      val divisorOracle = if (rightOracle == BigInteger.ZERO) BigInteger.ONE else rightOracle
      val (quotient, remainder) = left.divideAndRemainder(fromBigInteger(divisorOracle))
      val expected = leftOracle.divideAndRemainder(divisorOracle)
      assertOracle(quotient, expected[0])
      assertOracle(remainder, expected[1])

      val shift = random.nextInt(1900)
      assertOracle(
        left shl shift,
        leftOracle
          .abs()
          .shiftLeft(shift)
          .multiply(if (leftOracle.signum() < 0) BigInteger.ONE.negate() else BigInteger.ONE),
      )
      val rightShiftExpected =
        leftOracle
          .abs()
          .shiftRight(shift)
          .multiply(if (leftOracle.signum() < 0) BigInteger.ONE.negate() else BigInteger.ONE)
      assertOracle(left shr shift, rightShiftExpected)
    }
  }

  @Test
  fun `matches BigInteger on deterministic small signed values`() {
    for (value in -100L..100L) {
      for (other in -100L..100L) {
        val left = ExactInteger.fromLong(value)
        val right = ExactInteger.fromLong(other)
        assertEquals(
          BigInteger.valueOf(value).compareTo(BigInteger.valueOf(other)).coerceIn(-1, 1),
          left.compareTo(right),
        )
        assertOracle(left.gcd(right), BigInteger.valueOf(value).gcd(BigInteger.valueOf(other)))
      }
    }
  }

  private fun randomInteger(random: Random, bits: Int): BigInteger {
    val bytes = ByteArray((bits + 7) / 8)
    random.nextBytes(bytes)
    bytes[0] = (bytes[0].toInt() and 0x7f).toByte()
    var result = BigInteger(bytes)
    if (random.nextBoolean()) result = result.negate()
    return result
  }

  private fun assertOracle(actual: ExactInteger, expected: BigInteger) {
    assertEquals(expected.signum(), actual.sign)
    assertEquals(expected, toBigInteger(actual))
  }

  // Builds the value from independently inspected BigInteger bits; production storage remains
  // private.
  private fun fromBigInteger(value: BigInteger): ExactInteger {
    var result = ExactInteger.ZERO
    for (bit in value.abs().bitLength() - 1 downTo 0) {
      result = result shl 1
      if (value.abs().testBit(bit)) result += ExactInteger.ONE
    }
    return if (value.signum() < 0) -result else result
  }

  // The oracle conversion uses the public mathematical result, not a production formatter.
  private fun toBigInteger(value: ExactInteger): BigInteger {
    var result = BigInteger.ZERO
    var power = BigInteger.ONE
    var remaining = value.absoluteValue()
    while (!remaining.isZero) {
      val (quotient, remainder) = remaining.divideAndRemainder(ExactInteger.fromLong(2))
      if (!remainder.isZero) result += power
      power = power.shiftLeft(1)
      remaining = quotient
    }
    return if (value.sign < 0) result.negate() else result
  }
}
