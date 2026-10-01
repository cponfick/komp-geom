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
    val values =
      listOf(
        0x3ff0000000000000L,
        Long.MIN_VALUE or 0x3ff8000000000000L, // -1.5
        0x0000000000000001L, // smallest subnormal
        0x7fefffffffffffffL,
      )
    for (bits in values) {
      val decoded = Binary64.decode(Double.fromBits(bits))
      val numerator = ExactInteger.fromLong(decoded.signedSignificand)
      val denominator = ExactInteger.ONE
      val (exactNumerator, exactDenominator) =
        if (decoded.binaryExponent >= 0) {
          (numerator shl decoded.binaryExponent) to denominator
        } else {
          numerator to (denominator shl -decoded.binaryExponent)
        }
      (exactNumerator * denominator) shouldBe exactNumerator
      exactDenominator.sign shouldBe 1
      exactDenominator shouldBe
        if (decoded.binaryExponent >= 0) ExactInteger.ONE
        else ExactInteger.ONE shl -decoded.binaryExponent
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
