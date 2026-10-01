package io.github.cponfick.kompgeom.core.numeric

/**
 * The exact finite value extracted from an IEEE 754 binary64 value.
 *
 * The represented value is [signedSignificand] * 2^[binaryExponent]. The significand is kept in its
 * raw binary64 form: normal values include the implicit leading one and subnormal values do not.
 * Consequently, trailing zero bits are not removed. Zero is canonicalized to a significand of zero
 * and an exponent of zero. NaNs and infinities are rejected because they have no finite exact
 * input-topology value.
 *
 * This is an internal decoded value, rather than a public floating-point or rational type. The
 * signed significand needs at most 53 magnitude bits, so it is safe to store it in a [Long]; later
 * exact arithmetic can expand it to [ExactInteger] when required.
 */
internal class Binary64
internal constructor(
  /** The signed significand in the value [signedSignificand] * 2^[binaryExponent]. */
  internal val signedSignificand: Long,
  /** The power of two multiplying [signedSignificand]. */
  internal val binaryExponent: Int,
) {
  init {
    if (signedSignificand == 0L) {
      require(binaryExponent == 0) { "zero must have exponent zero" }
    } else {
      require(signedSignificand in -SIGNIFICAND_MASK..SIGNIFICAND_MASK) {
        "signed significand must fit in 53 magnitude bits"
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Binary64) return false
    return signedSignificand == other.signedSignificand && binaryExponent == other.binaryExponent
  }

  override fun hashCode(): Int = 31 * signedSignificand.hashCode() + binaryExponent

  internal companion object {
    private const val EXPONENT_BITS: Int = 11
    private const val FRACTION_BITS: Int = 52
    private const val EXPONENT_BIAS: Int = 1023
    private const val EXPONENT_MASK: Long = (1L shl EXPONENT_BITS) - 1L
    private const val FRACTION_MASK: Long = (1L shl FRACTION_BITS) - 1L
    private const val SIGNIFICAND_MASK: Long = (1L shl (FRACTION_BITS + 1)) - 1L

    /**
     * Decodes [value] without performing floating-point arithmetic.
     *
     * The raw sign, exponent, and fraction fields are obtained from [Double.toBits]. For normal
     * values the implicit leading bit is restored; subnormals use their fraction directly. Both
     * signed zeros produce the same canonical result. Exponent field `2047` is rejected for both
     * infinities and NaNs.
     */
    internal fun decode(value: Double): Binary64 {
      val bits = value.toBits()
      val exponentField = ((bits ushr FRACTION_BITS) and EXPONENT_MASK).toInt()
      require(exponentField != EXPONENT_MASK.toInt()) {
        "binary64 NaN and infinity are not supported"
      }

      val fraction = bits and FRACTION_MASK
      if (exponentField == 0 && fraction == 0L) return Binary64(0L, 0)

      val rawSignificand = if (exponentField == 0) fraction else (1L shl FRACTION_BITS) or fraction
      val signedSignificand = if ((bits ushr 63) == 0L) rawSignificand else -rawSignificand
      val binaryExponent =
        if (exponentField == 0) -1074 else exponentField - EXPONENT_BIAS - FRACTION_BITS
      return Binary64(signedSignificand, binaryExponent)
    }
  }
}
