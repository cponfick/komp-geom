package io.github.cponfick.kompgeom.core.numeric

internal data class ExactRationalArithmeticFixture(
  val leftNumerator: String,
  val leftDenominator: String,
  val rightNumerator: String,
  val rightDenominator: String,
  val sumNumerator: String,
  val sumDenominator: String,
  val comparison: Int,
)

internal val exactRationalArithmeticFixtures =
  listOf(
    ExactRationalArithmeticFixture(
      "4259931581",
      "1544716083",
      "1",
      "3",
      "1591612314",
      "514905361",
      1,
    ),
    ExactRationalArithmeticFixture(
      "2766578804",
      "3884337229",
      "2",
      "3",
      "16068410870",
      "11653011687",
      1,
    ),
    ExactRationalArithmeticFixture(
      "2342424365",
      "3058784338",
      "1",
      "1",
      "5401208703",
      "3058784338",
      -1,
    ),
  )
