package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test

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
  fun `negation and absolute value preserve immutability`() {
    val value = ExactInteger.fromLong(-123456789)

    (-value).sign shouldBe 1
    value.sign shouldBe -1
    value.abs() shouldBe ExactInteger.fromLong(123456789)
    ExactInteger.ZERO.absoluteValue() shouldBe ExactInteger.ZERO
  }
}
