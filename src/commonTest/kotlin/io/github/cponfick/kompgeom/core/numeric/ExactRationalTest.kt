package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExactRationalTest {
  private fun r(numerator: Long, denominator: Long = 1L) =
    ExactRational.of(ExactInteger.fromLong(numerator), ExactInteger.fromLong(denominator))

  @Test
  fun `normalizes signs zero and large factors`() {
    r(2, 4) shouldBe r(1, 2)
    r(-2, -4) shouldBe r(1, 2)
    r(2, -4) shouldBe r(-1, 2)
    ExactRational.of(ExactInteger.ZERO, ExactInteger.fromLong(-7)) shouldBe ExactRational.ZERO
    assertFailsWith<ArithmeticException> { r(0, 0) }
    val factor = ExactInteger.ONE shl 300
    ExactRational.of(factor * ExactInteger.fromLong(6), factor * ExactInteger.fromLong(15)) shouldBe
      r(2, 5)
  }

  @Test
  fun `performs exact arithmetic and rejects zero division`() {
    r(1, 6) + r(1, 3) shouldBe r(1, 2)
    r(1, 6) - r(1, 3) shouldBe r(-1, 6)
    r(-2, 3) * r(9, 4) shouldBe r(-3, 2)
    r(-2, 3) / r(4, 5) shouldBe r(-5, 6)
    r(7, 9) + -r(7, 9) shouldBe ExactRational.ZERO
    r(-7, 9).absoluteValue() shouldBe r(7, 9)
    r(7, 9).absoluteValue() shouldBe r(7, 9)
    r(0) * r(7, 3) shouldBe ExactRational.ZERO
    assertFailsWith<ArithmeticException> { r(0) / ExactRational.ZERO }
    assertFailsWith<ArithmeticException> { ExactRational.ZERO / ExactRational.ZERO }
  }

  @Test
  fun `imports finite binary values exactly`() {
    ExactRational.fromDouble(0.0) shouldBe ExactRational.ZERO
    ExactRational.fromDouble(-0.0) shouldBe ExactRational.ZERO
    ExactRational.fromDouble(1.5) shouldBe r(3, 2)
    ExactRational.fromDouble(0.1) shouldBe
      ExactRational.of(ExactInteger.fromLong(3602879701896397L), ExactInteger.ONE shl 55)
    ExactRational.fromDouble(Double.MIN_VALUE) shouldBe
      ExactRational.of(ExactInteger.ONE, ExactInteger.ONE shl 1074)
    assertFailsWith<IllegalArgumentException> { ExactRational.fromDouble(Double.POSITIVE_INFINITY) }
    assertFailsWith<IllegalArgumentException> { ExactRational.fromDouble(Double.NaN) }
    assertFailsWith<IllegalArgumentException> {
      ExactRational.fromBinary64(Binary64(1L, Int.MIN_VALUE))
    }
  }

  @Test
  fun `comparison equality and hashing use canonical values`() {
    val first = r(2, 3)
    val second = r(4, 6)
    first shouldBe second
    first.hashCode() shouldBe second.hashCode()
    (r(-2, 3) < r(-1, 2)) shouldBe true
    (r(1, 3).compareTo(r(2, 6))) shouldBe 0
    first.equals(null) shouldBe false
    first.equals("2/3") shouldBe false
  }
}
