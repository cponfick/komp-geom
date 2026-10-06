package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExactRationalBinary64Test {
  private fun p(exponent: Int): ExactInteger = ExactInteger.ONE shl exponent

  private fun r(numerator: ExactInteger, denominator: ExactInteger = ExactInteger.ONE) =
    ExactRational.of(numerator, denominator)

  private fun bits(value: ExactRational): Long = value.toDoubleNearestEven().toBits()

  @Test
  fun `rounds normal ties and carries to even`() {
    bits(ExactRational.ZERO) shouldBe 0x0000000000000000L
    bits(r(ExactInteger.ONE, p(0))) shouldBe 0x3ff0000000000000L
    bits(r(ExactInteger.ONE, ExactInteger.fromLong(3))) shouldBe 0x3fd5555555555555L
    bits(r(p(53) + p(0), p(53))) shouldBe 0x3ff0000000000000L
    bits(r(p(53) + ExactInteger.fromLong(3), p(53))) shouldBe 0x3ff0000000000002L
    bits(r(p(53) * ExactInteger.fromLong(2) - ExactInteger.ONE, p(53))) shouldBe 0x4000000000000000L
  }

  @Test
  fun `rounds subnormal boundaries and signed underflow`() {
    bits(r(ExactInteger.ONE, p(1076))) shouldBe 0L
    bits(r(ExactInteger.ONE, p(1075))) shouldBe 0L
    bits(r(ExactInteger.fromLong(3), p(1076))) shouldBe 1L
    bits(r(ExactInteger.ONE, p(1074))) shouldBe 1L
    bits(r(ExactInteger.fromLong(3), p(1075))) shouldBe 2L
    bits(r(ExactInteger.fromLong(-1), p(5000))) shouldBe Long.MIN_VALUE
    bits(r(ExactInteger.ONE, p(5000))) shouldBe 0L
  }

  @Test
  fun `preserves finite extremes and rejects overflow`() {
    val max = (p(53) - ExactInteger.ONE) shl 971
    bits(r(max, ExactInteger.ONE)) shouldBe 0x7fefffffffffffffL
    bits(r(max + p(969), ExactInteger.ONE)) shouldBe 0x7fefffffffffffffL
    val threshold = p(1024) - p(970)
    bits(r(threshold - ExactInteger.ONE, ExactInteger.ONE)) shouldBe 0x7fefffffffffffffL
    assertFailsWith<ArithmeticException> { r(threshold, ExactInteger.ONE).toDoubleNearestEven() }
    assertFailsWith<ArithmeticException> { r(p(1024), ExactInteger.ONE).toDoubleNearestEven() }
  }

  @Test
  fun `exact zero is positive and finite doubles round trip`() {
    ExactRational.fromDouble(-0.0).toDoubleNearestEven().toBits() shouldBe 0L
    val patterns =
      listOf(1L, 0x000fffffffffffffL, 0x0010000000000000L, 0x3ff0000000000000L, 0x7fefffffffffffffL)
    for (pattern in patterns) {
      ExactRational.fromDouble(Double.fromBits(pattern)).toDoubleNearestEven().toBits() shouldBe
        pattern
      ExactRational.fromDouble(Double.fromBits(pattern or Long.MIN_VALUE))
        .toDoubleNearestEven()
        .toBits() shouldBe (pattern or Long.MIN_VALUE)
    }
  }
}
