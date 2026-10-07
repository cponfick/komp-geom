package io.github.cponfick.kompgeom.core.numeric

/** Integer-only, round-to-nearest-ties-to-even export of exact rationals to binary64. */
internal object ExactRationalBinary64 {
  private const val MIN_SUBNORMAL_EXPONENT = -1075
  private const val MIN_NORMAL_EXPONENT = -1022
  private const val MAX_NORMAL_EXPONENT = 1023

  internal fun export(value: ExactRational): Double {
    if (value.isZero) return Double.fromBits(0L)

    val negative = value.sign < 0
    val numerator = value.numerator.abs()
    val denominator = value.denominator
    val k = numerator.bitLength() - denominator.bitLength()
    if (k > 1024) overflow()
    if (k < MIN_SUBNORMAL_EXPONENT) return zeroBits(negative)

    val comparison =
      if (k >= 0) numerator.compareTo(denominator shl k)
      else (numerator shl -k).compareTo(denominator)
    val exponent = if (comparison >= 0) k else k - 1
    if (exponent > MAX_NORMAL_EXPONENT) overflow()
    if (exponent < MIN_SUBNORMAL_EXPONENT) return zeroBits(negative)

    val magnitudeBits =
      if (exponent >= MIN_NORMAL_EXPONENT) {
        encodeNormalMagnitudeBits(numerator, denominator, exponent)
      } else {
        encodeSubnormalMagnitudeBits(numerator, denominator)
      }
    val bits = if (negative) magnitudeBits or Long.MIN_VALUE else magnitudeBits
    return Double.fromBits(bits)
  }

  /**
   * Rounds a positive rational in the normal range and encodes its binary64 exponent and fraction,
   * leaving the sign bit clear. Handles rounding carry into the next exponent and rejects overflow.
   */
  private fun encodeNormalMagnitudeBits(
    numerator: ExactInteger,
    denominator: ExactInteger,
    exponent: Int,
  ): Long {
    var significand = roundScaled(numerator, denominator, 52 - exponent)
    val twoTo53 = ExactInteger.ONE shl 53
    val roundedExponent =
      if (significand == twoTo53) {
        significand = ExactInteger.ONE shl 52
        exponent + 1
      } else {
        exponent
      }
    if (roundedExponent > MAX_NORMAL_EXPONENT) overflow()
    check(significand >= (ExactInteger.ONE shl 52) && significand < twoTo53)
    val significandBits = significand.toLongExact() - (1L shl 52)
    return ((roundedExponent + 1023).toLong() shl 52) or significandBits
  }

  /**
   * Rounds a positive rational below the normal range to a multiple of 2^-1074 and encodes its
   * binary64 magnitude, leaving the sign bit clear. Rounding can produce zero or the smallest
   * normal value as well as a subnormal value.
   */
  private fun encodeSubnormalMagnitudeBits(
    numerator: ExactInteger,
    denominator: ExactInteger,
  ): Long {
    val significand = roundScaled(numerator, denominator, 1074)
    val maxSubnormalSignificand = ExactInteger.ONE shl 52
    check(significand >= ExactInteger.ZERO && significand <= maxSubnormalSignificand)
    return significand.toLongExact()
  }

  private fun roundScaled(
    numerator: ExactInteger,
    denominator: ExactInteger,
    shift: Int,
  ): ExactInteger {
    val (scaledNumerator, scaledDenominator) =
      if (shift >= 0) numerator shl shift to denominator else numerator to (denominator shl -shift)
    val (quotient, remainder) = scaledNumerator.divideAndRemainder(scaledDenominator)
    val twiceRemainder = remainder shl 1
    val comparison = twiceRemainder.compareTo(scaledDenominator)
    return if (
      comparison > 0 ||
        (comparison == 0 && quotient != ExactInteger.ZERO && quotient.trailingZeroCount() == 0)
    ) {
      quotient + ExactInteger.ONE
    } else {
      quotient
    }
  }

  private fun zeroBits(negative: Boolean): Double =
    Double.fromBits(if (negative) Long.MIN_VALUE else 0L)

  private fun overflow(): Nothing =
    throw ArithmeticException("rational value rounds outside finite binary64 range")
}
