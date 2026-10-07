package io.github.cponfick.kompgeom.core.numeric

internal data class ExactRationalValueFixture(val numerator: String, val denominator: String)

internal data class ExactRationalArithmeticFixture(
  val left: ExactRationalValueFixture,
  val right: ExactRationalValueFixture,
  val sum: ExactRationalValueFixture,
  val difference: ExactRationalValueFixture,
  val product: ExactRationalValueFixture,
  val quotient: ExactRationalValueFixture,
  val comparison: Int,
)

/**
 * Expected results computed independently with Python's fractions.Fraction. For each operand pair a
 * = Fraction(int(n), int(d)), b = Fraction(int(m), int(e)), record the canonical numerator and
 * denominator of a + b, a - b, a * b, and a / b, and the comparison (a > b) - (a < b).
 */
internal val exactRationalArithmeticFixtures =
  listOf(
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("4259931581", "1544716083"),
      right = ExactRationalValueFixture("1", "3"),
      sum = ExactRationalValueFixture("1591612314", "514905361"),
      difference = ExactRationalValueFixture("3745026220", "1544716083"),
      product = ExactRationalValueFixture("4259931581", "4634148249"),
      quotient = ExactRationalValueFixture("4259931581", "514905361"),
      comparison = 1,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("2766578804", "3884337229"),
      right = ExactRationalValueFixture("2", "3"),
      sum = ExactRationalValueFixture("16068410870", "11653011687"),
      difference = ExactRationalValueFixture("531061954", "11653011687"),
      product = ExactRationalValueFixture("5533157608", "11653011687"),
      quotient = ExactRationalValueFixture("4149868206", "3884337229"),
      comparison = 1,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("2342424365", "3058784338"),
      right = ExactRationalValueFixture("1", "1"),
      sum = ExactRationalValueFixture("5401208703", "3058784338"),
      difference = ExactRationalValueFixture("-716359973", "3058784338"),
      product = ExactRationalValueFixture("2342424365", "3058784338"),
      quotient = ExactRationalValueFixture("2342424365", "3058784338"),
      comparison = -1,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("-2", "3"),
      right = ExactRationalValueFixture("4", "5"),
      sum = ExactRationalValueFixture("2", "15"),
      difference = ExactRationalValueFixture("-22", "15"),
      product = ExactRationalValueFixture("-8", "15"),
      quotient = ExactRationalValueFixture("-5", "6"),
      comparison = -1,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("-7", "9"),
      right = ExactRationalValueFixture("-11", "13"),
      sum = ExactRationalValueFixture("-190", "117"),
      difference = ExactRationalValueFixture("8", "117"),
      product = ExactRationalValueFixture("77", "117"),
      quotient = ExactRationalValueFixture("91", "99"),
      comparison = 1,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("-14", "-21"),
      right = ExactRationalValueFixture("2", "3"),
      sum = ExactRationalValueFixture("4", "3"),
      difference = ExactRationalValueFixture("0", "1"),
      product = ExactRationalValueFixture("4", "9"),
      quotient = ExactRationalValueFixture("1", "1"),
      comparison = 0,
    ),
    ExactRationalArithmeticFixture(
      left = ExactRationalValueFixture("0", "-7"),
      right = ExactRationalValueFixture("-5", "11"),
      sum = ExactRationalValueFixture("-5", "11"),
      difference = ExactRationalValueFixture("5", "11"),
      product = ExactRationalValueFixture("0", "1"),
      quotient = ExactRationalValueFixture("0", "1"),
      comparison = 1,
    ),
    ExactRationalArithmeticFixture(
      left =
        ExactRationalValueFixture("-1267650600228229401496703205377", "1208925819614629174706179"),
      right = ExactRationalValueFixture("18446744073709551617", "-1180591620717411303425"),
      sum =
        ExactRationalValueFixture(
          "-1496577698927589786772464062047582065645100847857668",
          "1427247692705959881059498437043971917791363075",
        ),
      difference =
        ExactRationalValueFixture(
          "-1496577654326099389711217776558183200654677229174782",
          "1427247692705959881059498437043971917791363075",
        ),
      product =
        ExactRationalValueFixture(
          "23384026197294446692526607923707204460065333444609",
          "1427247692705959881059498437043971917791363075",
        ),
      quotient =
        ExactRationalValueFixture(
          "1496577676626844588241840919302882633149889038516225",
          "22300745198530623142744699432495211809341443",
        ),
      comparison = -1,
    ),
  )
