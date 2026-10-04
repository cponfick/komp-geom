package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExactIntegerExtendedTest {
  @Test
  fun `covers long limb boundaries and sparse magnitudes`() {
    val base = ExactInteger.ONE shl 30
    val sparse = (ExactInteger.ONE shl 300) + (ExactInteger.ONE shl 60) + ExactInteger.ONE
    val maximal = (base - ExactInteger.ONE) * (base - ExactInteger.ONE)

    (base - ExactInteger.ONE).bitLength() shouldBe 30
    (ExactInteger.ONE shl 60).bitLength() shouldBe 61
    sparse.trailingZeroCount() shouldBe 0
    maximal shouldBe (ExactInteger.ONE shl 60) - (ExactInteger.ONE shl 31) + ExactInteger.ONE
  }

  @Test
  fun `preserves long carry and borrow chains`() {
    val allOnes = (ExactInteger.ONE shl 300) - ExactInteger.ONE
    (allOnes + ExactInteger.ONE) shouldBe (ExactInteger.ONE shl 300)
    ((ExactInteger.ONE shl 300) - ExactInteger.ONE) shouldBe allOnes
    (allOnes - allOnes) shouldBe ExactInteger.ZERO
  }

  @Test
  fun `handles every important shift boundary`() {
    val value = (ExactInteger.ONE shl 137) + ExactInteger.ONE
    for (shift in listOf(0, 1, 29, 30, 31, 60, 137, 138, 300)) {
      val shifted = value shl shift
      shifted shr shift shouldBe value
    }
    (value shr 137) shouldBe ExactInteger.ONE
    (value shr 138) shouldBe ExactInteger.ZERO
    (value shl 30).trailingZeroCount() shouldBe 30
  }

  @Test
  fun `divides equal smaller and exact large operands`() {
    val divisor = (ExactInteger.ONE shl 256) + (ExactInteger.ONE shl 30) + ExactInteger.fromLong(7)
    val dividend = divisor * ((ExactInteger.ONE shl 190) + ExactInteger.fromLong(3))
    val (quotient, remainder) = dividend.divideAndRemainder(divisor)
    quotient shouldBe (ExactInteger.ONE shl 190) + ExactInteger.fromLong(3)
    remainder shouldBe ExactInteger.ZERO
    val (smallQuotient, smallRemainder) = ExactInteger.ONE.divideAndRemainder(divisor)
    smallQuotient shouldBe ExactInteger.ZERO
    smallRemainder shouldBe ExactInteger.ONE
  }

  @Test
  fun `gcd handles powers and coprime sparse values`() {
    val power = ExactInteger.ONE shl 512
    (power * ExactInteger.fromLong(-45)).gcd(power * ExactInteger.fromLong(75)) shouldBe
      power * ExactInteger.fromLong(15)
    ((ExactInteger.ONE shl 401) + ExactInteger.ONE).gcd(ExactInteger.ONE shl 400) shouldBe
      ExactInteger.ONE
  }

  @Test
  fun `rejects impossible division and keeps values immutable`() {
    val value = ExactInteger.fromLong(-123456789)
    val snapshot = value
    assertFailsWith<ArithmeticException> { value.divideAndRemainder(ExactInteger.ZERO) }
    (value * ExactInteger.ONE) shouldBe snapshot
    (-value).absoluteValue() shouldBe value.absoluteValue()
  }
}
