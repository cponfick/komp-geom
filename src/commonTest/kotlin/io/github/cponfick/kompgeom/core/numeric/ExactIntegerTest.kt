package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExactIntegerTest {
  @Test
  fun `constructs zero and constants canonically`() {
    val zero = ExactInteger.fromLong(0)

    zero shouldBe ExactInteger.ZERO
    zero.isZero shouldBe true
    zero.sign shouldBe 0
    ExactInteger.ONE.sign shouldBe 1
    ExactInteger.ONE shouldBe ExactInteger.fromLong(1)
  }

  @Test
  fun `constructs long boundaries including minimum`() {
    val minimum = ExactInteger.fromLong(Long.MIN_VALUE)
    val maximum = ExactInteger.fromLong(Long.MAX_VALUE)

    minimum.sign shouldBe -1
    maximum.sign shouldBe 1
    minimum.absoluteValue() shouldBe maximum + ExactInteger.ONE
    (-minimum) shouldBe maximum + ExactInteger.ONE
  }

  @Test
  fun `adds across limb boundaries`() {
    val limb = 1L shl 30
    val left = ExactInteger.fromLong(limb - 1)
    val right = ExactInteger.fromLong(1)

    left + right shouldBe ExactInteger.fromLong(limb)
    (ExactInteger.fromLong((1L shl 60) - 1) + right) shouldBe ExactInteger.fromLong(1L shl 60)
  }

  @Test
  fun `subtracts with signed dispatch and canonical cancellation`() {
    val value = ExactInteger.fromLong(1L shl 60)

    value - value shouldBe ExactInteger.ZERO
    value - ExactInteger.fromLong(1) shouldBe ExactInteger.fromLong((1L shl 60) - 1)
    ExactInteger.fromLong(-7) + ExactInteger.fromLong(12) shouldBe ExactInteger.fromLong(5)
    ExactInteger.fromLong(7) - ExactInteger.fromLong(12) shouldBe ExactInteger.fromLong(-5)
  }

  @Test
  fun `compares signed and unequal magnitudes`() {
    val values = listOf(-100L, -1L, 0L, 1L, 100L)
    val integers = values.map(ExactInteger::fromLong)

    for (index in integers.indices) {
      for (otherIndex in integers.indices) {
        integers[index].compareTo(integers[otherIndex]) shouldBe
          values[index].compareTo(values[otherIndex])
      }
    }
  }

  @Test
  fun `equality and hash code use normalized value`() {
    val a = ExactInteger.fromLong(1L shl 30) + ExactInteger.fromLong(3)
    val b = ExactInteger.fromLong((1L shl 30) + 3)

    a shouldBe b
    a.hashCode() shouldBe b.hashCode()
    a shouldBe (a + ExactInteger.ZERO)
  }

  @Test
  fun `multiplies with signs and long carry chains`() {
    val base = ExactInteger.fromLong(1L shl 30)
    val value = (base + ExactInteger.fromLong(1)) * (base - ExactInteger.ONE)

    value shouldBe ExactInteger.fromLong((1L shl 60) - 1)
    (-value) * ExactInteger.fromLong(-3) shouldBe ExactInteger.fromLong(3) * value
    ExactInteger.ZERO * value shouldBe ExactInteger.ZERO
    value * ExactInteger.ONE shouldBe value
  }

  @Test
  fun `shifts signed magnitudes across limb boundaries`() {
    val value = -(ExactInteger.ONE shl 31)

    value shl 29 shouldBe -(ExactInteger.ONE shl 60)
    (value shl 30) shr 30 shouldBe value
    (value shr 1) shouldBe -(ExactInteger.ONE shl 30)
    (value shr 31) shouldBe -ExactInteger.ONE
    (value shr 32) shouldBe ExactInteger.ZERO
    value.bitLength() shouldBe 32
    (value shl 30).bitLength() shouldBe 62
    value.trailingZeroCount() shouldBe 31
  }

  @Test
  fun `rejects negative shifts and zero trailing-bit queries`() {
    assertFailsWith<IllegalArgumentException> { ExactInteger.ONE shl -1 }
    assertFailsWith<IllegalArgumentException> { ExactInteger.ONE shr -1 }
    assertFailsWith<IllegalArgumentException> { ExactInteger.ZERO.trailingZeroCount() }
  }

  @Test
  fun `divides with truncation toward zero and signed remainder`() {
    val cases = listOf(17L to 5L, -17L to 5L, 17L to -5L, -17L to -5L, 3L to 5L, 15L to 5L)

    for ((dividendValue, divisorValue) in cases) {
      val dividend = ExactInteger.fromLong(dividendValue)
      val divisor = ExactInteger.fromLong(divisorValue)
      val (quotient, remainder) = dividend.divideAndRemainder(divisor)

      quotient shouldBe ExactInteger.fromLong(dividendValue / divisorValue)
      remainder shouldBe ExactInteger.fromLong(dividendValue % divisorValue)
      dividend shouldBe quotient * divisor + remainder
      (remainder.absoluteValue() < divisor.absoluteValue()) shouldBe true
    }
    assertFailsWith<ArithmeticException> { ExactInteger.ONE.divideAndRemainder(ExactInteger.ZERO) }
  }

  @Test
  fun `divides multi-limb magnitudes`() {
    val divisor = (ExactInteger.ONE shl 61) + ExactInteger.fromLong(12345)
    val quotient = (ExactInteger.ONE shl 130) + (ExactInteger.ONE shl 37) + ExactInteger.ONE
    val remainder = ExactInteger.fromLong(987654321)
    val (actualQuotient, actualRemainder) =
      (quotient * divisor + remainder).divideAndRemainder(divisor)

    actualQuotient shouldBe quotient
    actualRemainder shouldBe remainder
  }

  @Test
  fun `gcd is nonnegative and handles zeros and shared factors`() {
    ExactInteger.ZERO.gcd(ExactInteger.ZERO) shouldBe ExactInteger.ZERO
    ExactInteger.fromLong(-84).gcd(ExactInteger.ZERO) shouldBe ExactInteger.fromLong(84)
    ExactInteger.ZERO.gcd(ExactInteger.fromLong(-84)) shouldBe ExactInteger.fromLong(84)
    ExactInteger.fromLong(-84).gcd(ExactInteger.fromLong(30)) shouldBe ExactInteger.fromLong(6)
    ExactInteger.fromLong(35).gcd(ExactInteger.fromLong(64)) shouldBe ExactInteger.ONE

    val factor = ExactInteger.ONE shl 100
    (factor * ExactInteger.fromLong(21)).gcd(factor * ExactInteger.fromLong(35)) shouldBe
      factor * ExactInteger.fromLong(7)
  }

  @Test
  fun `negation and absolute value preserve immutability`() {
    val value = ExactInteger.fromLong(-123456789)

    (-value).sign shouldBe 1
    value.sign shouldBe -1
    value.abs() shouldBe ExactInteger.fromLong(123456789)
    ExactInteger.ZERO.absoluteValue() shouldBe ExactInteger.ZERO
  }
}
