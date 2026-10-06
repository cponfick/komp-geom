/** Exact rational arithmetic used by the internal topology foundation. */
package io.github.cponfick.kompgeom.core.numeric

/**
 * An immutable canonical rational number.
 *
 * The denominator is strictly positive, numerator and denominator are reduced by their GCD, and
 * zero is always represented as `0/1`. Arithmetic deliberately reduces every result eagerly; this
 * makes the reference path auditable, at the cost of product and GCD growth for large operands.
 */
internal class ExactRational
private constructor(internal val numerator: ExactInteger, internal val denominator: ExactInteger) :
  Comparable<ExactRational> {
  init {
    check(denominator.isPositive) { "rational denominator must be positive" }
    check(!numerator.isZero || denominator == ExactInteger.ONE) {
      "rational zero must be canonical"
    }
  }

  internal val sign: Int
    get() = numerator.sign

  internal val isZero: Boolean
    get() = numerator.isZero

  internal operator fun unaryMinus(): ExactRational =
    if (isZero) this else of(-numerator, denominator)

  internal operator fun plus(other: ExactRational): ExactRational =
    of(
      numerator * other.denominator + other.numerator * denominator,
      denominator * other.denominator,
    )

  internal operator fun minus(other: ExactRational): ExactRational = this + -other

  internal operator fun times(other: ExactRational): ExactRational =
    of(numerator * other.numerator, denominator * other.denominator)

  internal operator fun div(other: ExactRational): ExactRational {
    if (other.isZero) throw ArithmeticException("division by zero")
    return of(numerator * other.denominator, denominator * other.numerator)
  }

  internal fun absoluteValue(): ExactRational = if (sign < 0) -this else this

  internal fun toDoubleNearestEven(): Double = ExactRationalBinary64.export(this)

  override fun compareTo(other: ExactRational): Int {
    if (sign != other.sign) return sign.compareTo(other.sign)
    if (isZero) return 0
    return (numerator * other.denominator).compareTo(other.numerator * denominator)
  }

  override fun equals(other: Any?): Boolean =
    this === other ||
      (other is ExactRational && numerator == other.numerator && denominator == other.denominator)

  override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

  internal companion object {
    internal val ZERO: ExactRational = ExactRational(ExactInteger.ZERO, ExactInteger.ONE)
    internal val ONE: ExactRational = ExactRational(ExactInteger.ONE, ExactInteger.ONE)

    internal fun of(numerator: ExactInteger, denominator: ExactInteger): ExactRational {
      if (denominator.isZero) throw ArithmeticException("rational denominator must not be zero")
      if (numerator.isZero) return ZERO
      var normalizedNumerator = numerator
      var normalizedDenominator = denominator
      if (normalizedDenominator.isNegative) {
        normalizedNumerator = -normalizedNumerator
        normalizedDenominator = -normalizedDenominator
      }
      val gcd = normalizedNumerator.abs().gcd(normalizedDenominator)
      val reducedNumerator = divideExact(normalizedNumerator, gcd)
      val reducedDenominator = divideExact(normalizedDenominator, gcd)
      if (reducedNumerator == ExactInteger.ONE && reducedDenominator == ExactInteger.ONE) return ONE
      return ExactRational(reducedNumerator, reducedDenominator)
    }

    internal fun fromLong(value: Long): ExactRational =
      of(ExactInteger.fromLong(value), ExactInteger.ONE)

    internal fun fromBinary64(decoded: Binary64): ExactRational {
      if (decoded.binaryExponent == Int.MIN_VALUE) {
        throw IllegalArgumentException("binary exponent is too small")
      }
      val significand = ExactInteger.fromLong(decoded.signedSignificand)
      if (significand.isZero) return ZERO
      return if (decoded.binaryExponent >= 0) {
        of(significand shl decoded.binaryExponent, ExactInteger.ONE)
      } else {
        of(significand, ExactInteger.ONE shl -decoded.binaryExponent)
      }
    }

    internal fun fromDouble(value: Double): ExactRational = fromBinary64(Binary64.decode(value))

    private fun divideExact(value: ExactInteger, divisor: ExactInteger): ExactInteger {
      val (quotient, remainder) = value.divideAndRemainder(divisor)
      check(remainder.isZero) { "rational normalization produced a non-exact division" }
      return quotient
    }
  }
}
