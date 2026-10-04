package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class Binary64FixturesTest {
  @Test
  fun `decodes independently specified binary64 fixtures`() {
    val fixtures =
      listOf(
        0x0010000000000000L to ((1L shl 52) to -1074), // smallest normal
        0x000fffffffffffffL to (((1L shl 52) - 1L) to -1074), // largest subnormal
        0x3ff0000000000000L to ((1L shl 52) to -52),
        0x3ff0000000000001L to (((1L shl 52) + 1L) to -52),
        0x4000000000000000L to ((1L shl 52) to -51),
        0x3fefffffffffffffL to (((1L shl 53) - 1L) to -53),
        0x7fefffffffffffffL to (0x1fffffffffffffL to 971),
        Long.MIN_VALUE or 0x0010000000000000L to (-(1L shl 52) to -1074),
      )

    for ((bits, expected) in fixtures) {
      val decoded = Binary64.decode(Double.fromBits(bits))
      decoded.signedSignificand shouldBe expected.first
      decoded.binaryExponent shouldBe expected.second
    }
  }

  @Test
  fun `materializes exact numerator and denominator fixtures`() {
    // Expected fractions are specified independently of the decoded fields. Cross multiplication
    // allows the decoder to retain its unreduced significand without weakening the value check.
    val one = ExactInteger.ONE
    val fixtures =
      listOf(
        0x3ff0000000000000L to (one to one),
        (Long.MIN_VALUE or 0x3ff8000000000000L) to
          (ExactInteger.fromLong(-3) to ExactInteger.fromLong(2)),
        0x0000000000000001L to (one to (one shl 1074)),
        0x7fefffffffffffffL to ((((one shl 53) - one) shl 971) to one),
        0x3ff0000000000001L to (((one shl 52) + one) to (one shl 52)),
      )
    for ((bits, expected) in fixtures) {
      val decoded = Binary64.decode(Double.fromBits(bits))
      val numerator = ExactInteger.fromLong(decoded.signedSignificand)
      val denominator = ExactInteger.ONE
      val (exactNumerator, exactDenominator) =
        if (decoded.binaryExponent >= 0) {
          (numerator shl decoded.binaryExponent) to denominator
        } else {
          numerator to (denominator shl -decoded.binaryExponent)
        }
      exactDenominator.sign shouldBe 1
      (exactNumerator * expected.second) shouldBe (expected.first * exactDenominator)
    }
  }

  @Test
  fun `rejects all nonfinite exponent patterns`() {
    val patterns =
      listOf(
        0x7ff0000000000000L,
        0x7ff0000000000001L,
        0x7fffffffffffffffL,
        Long.MIN_VALUE or 0x7ff0000000000000L,
      )
    for (bits in patterns) {
      assertFailsWith<IllegalArgumentException> { Binary64.decode(Double.fromBits(bits)) }
    }
  }
}
