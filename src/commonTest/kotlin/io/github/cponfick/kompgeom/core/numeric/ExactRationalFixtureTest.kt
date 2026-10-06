package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ExactRationalFixtureTest {
  @Test
  fun `matches checked in independent arithmetic fixtures`() {
    for ((
      leftNumerator,
      leftDenominator,
      rightNumerator,
      rightDenominator,
      sumNumerator,
      sumDenominator,
      comparison) in exactRationalArithmeticFixtures) {
      val left = rational(leftNumerator, leftDenominator)
      val right = rational(rightNumerator, rightDenominator)
      val expected = rational(sumNumerator, sumDenominator)
      (left + right) shouldBe expected
      left.compareTo(right) shouldBe comparison
    }
  }

  private fun rational(numerator: String, denominator: String): ExactRational =
    ExactRational.of(parse(numerator), parse(denominator))

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
