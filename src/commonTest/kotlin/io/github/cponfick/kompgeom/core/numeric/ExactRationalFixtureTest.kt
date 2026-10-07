package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ExactRationalFixtureTest {
  @Test
  fun `matches checked in independent arithmetic fixtures`() {
    for (fixture in exactRationalArithmeticFixtures) {
      val left = rational(fixture.left)
      val right = rational(fixture.right)
      assertCanonical(left + right, fixture.sum)
      assertCanonical(left - right, fixture.difference)
      assertCanonical(left * right, fixture.product)
      assertCanonical(left / right, fixture.quotient)
      left.compareTo(right) shouldBe fixture.comparison
    }
  }

  private fun rational(value: ExactRationalValueFixture): ExactRational =
    ExactRational.of(parse(value.numerator), parse(value.denominator))

  private fun assertCanonical(actual: ExactRational, expected: ExactRationalValueFixture) {
    // Do not normalize the expected result through the implementation under test.
    actual.numerator shouldBe parse(expected.numerator)
    actual.denominator shouldBe parse(expected.denominator)
  }

  private fun parse(text: String): ExactInteger {
    require(text.isNotEmpty())
    val negative = text[0] == '-'
    val start = if (negative || text[0] == '+') 1 else 0
    require(start < text.length)
    var result = ExactInteger.ZERO
    for (index in start until text.length) {
      val digit = text[index] - '0'
      require(digit in 0..9)
      result = result * ExactInteger.fromLong(10) + ExactInteger.fromLong(digit.toLong())
    }
    return if (negative) -result else result
  }
}
