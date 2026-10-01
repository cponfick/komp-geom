package io.github.cponfick.kompgeom.core.numeric

/**
 * An immutable signed integer used by the exact numeric implementation.
 *
 * Values use a sign in `-1..1` and a little-endian magnitude made up of base-`2^30` limbs. Every
 * limb is in `0..2^30 - 1`; the most significant limb is nonzero for nonzero values. Zero has no
 * limbs and sign zero, which is its only representation. The 30-bit base means that a limb product,
 * one incoming carry, and one result limb can be accumulated safely in a signed `Long` when
 * multiplication is added.
 *
 * This is an internal signed-magnitude type, not a two's-complement bit-vector. In particular,
 * operations in this class do not expose or retain mutable caller-owned storage.
 */
internal class ExactInteger
private constructor(
  /** The sign of this value: `-1`, `0`, or `1`. */
  internal val sign: Int,
  private val limbs: IntArray,
) : Comparable<ExactInteger> {

  init {
    require(sign in -1..1) { "sign must be -1, 0, or 1" }
    require((sign == 0) == limbs.isEmpty()) { "zero must have an empty magnitude" }
    require(limbs.isEmpty() || limbs[limbs.lastIndex] != 0) {
      "nonzero magnitudes must not have a high zero limb"
    }
    require(limbs.all { it in 0 until LIMB_BASE }) { "limbs must be 30-bit values" }
  }

  /** Whether this integer is zero. */
  internal val isZero: Boolean
    get() = sign == 0

  /** Whether this integer is strictly negative. */
  internal val isNegative: Boolean
    get() = sign < 0

  /** Whether this integer is strictly positive. */
  internal val isPositive: Boolean
    get() = sign > 0

  /** Returns the nonnegative magnitude of this integer. */
  internal fun absoluteValue(): ExactInteger = if (sign < 0) -this else this

  /** Returns the nonnegative magnitude of this integer. */
  internal fun abs(): ExactInteger = absoluteValue()

  /** Returns the additive inverse of this integer. */
  internal operator fun unaryMinus(): ExactInteger {
    if (isZero) return this
    return ExactInteger(-sign, limbs.copyOf())
  }

  /** Adds this integer and [other]. */
  internal operator fun plus(other: ExactInteger): ExactInteger {
    if (isZero) return other
    if (other.isZero) return this
    if (sign == other.sign) {
      return fromMagnitude(sign, addMagnitudes(limbs, other.limbs))
    }

    val magnitudeComparison = compareMagnitudes(limbs, other.limbs)
    return when {
      magnitudeComparison == 0 -> ZERO
      magnitudeComparison > 0 -> fromMagnitude(sign, subtractMagnitudes(limbs, other.limbs))
      else -> fromMagnitude(other.sign, subtractMagnitudes(other.limbs, limbs))
    }
  }

  /** Subtracts [other] from this integer. */
  internal operator fun minus(other: ExactInteger): ExactInteger = this + -other

  /** Multiplies this integer by [other] using schoolbook multiplication. */
  internal operator fun times(other: ExactInteger): ExactInteger {
    if (isZero || other.isZero) return ZERO
    if (this === ONE) return other
    if (other === ONE) return this

    check(limbs.size <= Int.MAX_VALUE - other.limbs.size) { "integer result is too large" }
    val result = IntArray(limbs.size + other.limbs.size)
    for (leftIndex in limbs.indices) {
      var carry = 0L
      for (rightIndex in other.limbs.indices) {
        val resultIndex = leftIndex + rightIndex
        // (base - 1)^2 + (base - 1) + (base - 1) is below 2^60.
        val sum =
          result[resultIndex].toLong() +
            limbs[leftIndex].toLong() * other.limbs[rightIndex].toLong() +
            carry
        result[resultIndex] = (sum and LIMB_MASK).toInt()
        carry = sum ushr LIMB_BITS
      }
      var resultIndex = leftIndex + other.limbs.size
      while (carry != 0L) {
        // At most one carry limb is normally needed here. This loop also makes the invariant
        // explicit and avoids relying on an unexamined overwrite if the implementation changes.
        val sum = result[resultIndex].toLong() + carry
        result[resultIndex] = (sum and LIMB_MASK).toInt()
        carry = sum ushr LIMB_BITS
        resultIndex++
      }
    }
    return fromMagnitude(sign * other.sign, result)
  }

  /** Returns the quotient and remainder of signed truncating division by [divisor]. */
  internal fun divideAndRemainder(divisor: ExactInteger): Pair<ExactInteger, ExactInteger> {
    if (divisor.isZero) throw ArithmeticException("division by zero")
    if (isZero) return ZERO to ZERO

    val magnitudes = divideMagnitudes(limbs, divisor.limbs)
    val quotient = fromMagnitude(sign * divisor.sign, magnitudes.first)
    val remainder = fromMagnitude(sign, magnitudes.second)
    return quotient to remainder
  }

  /** Returns the nonnegative greatest common divisor of this integer and [other]. */
  internal fun gcd(other: ExactInteger): ExactInteger {
    var left = absoluteValue()
    var right = other.absoluteValue()
    if (left.isZero) return right
    if (right.isZero) return left

    val commonTrailingZeros = minOf(left.trailingZeroCount(), right.trailingZeroCount())
    left = left shr left.trailingZeroCount()
    right = right shr right.trailingZeroCount()
    while (!right.isZero) {
      if (left > right) {
        val temporary = left
        left = right
        right = temporary
      }
      right -= left
      if (!right.isZero) right = right shr right.trailingZeroCount()
    }
    return left shl commonTrailingZeros
  }

  /**
   * Shifts the magnitude left by [bits], preserving this value's sign. A negative count is
   * rejected. This is a signed-magnitude shift, rather than a two's-complement bit operation.
   */
  internal infix fun shl(bits: Int): ExactInteger {
    require(bits >= 0) { "shift count must not be negative" }
    if (isZero || bits == 0) return this
    val wholeLimbs = bits / LIMB_BITS
    check(wholeLimbs <= Int.MAX_VALUE - limbs.size - 1) { "integer result is too large" }
    val intraLimbBits = bits % LIMB_BITS
    val result = IntArray(limbs.size + wholeLimbs + if (intraLimbBits == 0) 0 else 1)
    var carry = 0L
    for (index in limbs.indices) {
      val shifted = (limbs[index].toLong() shl intraLimbBits) + carry
      result[index + wholeLimbs] = (shifted and LIMB_MASK).toInt()
      carry = shifted ushr LIMB_BITS
    }
    if (carry != 0L) result[limbs.size + wholeLimbs] = carry.toInt()
    return fromMagnitude(sign, result)
  }

  /**
   * Shifts the magnitude right by [bits], truncating discarded bits and preserving the sign of a
   * nonzero result. A negative count is rejected; shifting by at least the bit length returns zero.
   * This is not arithmetic two's-complement shifting.
   */
  internal infix fun shr(bits: Int): ExactInteger {
    require(bits >= 0) { "shift count must not be negative" }
    if (isZero || bits == 0) return this
    if (bits >= bitLength()) return ZERO
    val wholeLimbs = bits / LIMB_BITS
    val intraLimbBits = bits % LIMB_BITS
    val result = IntArray(limbs.size - wholeLimbs)
    if (intraLimbBits == 0) {
      for (index in result.indices) result[index] = limbs[index + wholeLimbs]
    } else {
      val inverseBits = LIMB_BITS - intraLimbBits
      for (index in result.indices) {
        val sourceIndex = index + wholeLimbs
        val value = limbs[sourceIndex].toLong() ushr intraLimbBits
        val upper =
          if (sourceIndex + 1 < limbs.size) {
            (limbs[sourceIndex + 1].toLong() shl inverseBits) and LIMB_MASK
          } else {
            0L
          }
        result[index] = (value or upper).toInt()
      }
    }
    return fromMagnitude(sign, result)
  }

  /** Number of significant bits in the absolute value; zero has bit length zero. */
  internal fun bitLength(): Int {
    if (isZero) return 0
    val high = limbs.last()
    var bits = 0
    var value = high
    while (value != 0) {
      bits++
      value = value ushr 1
    }
    check(limbs.size <= (Int.MAX_VALUE - bits) / LIMB_BITS) { "bit length overflow" }
    return (limbs.size - 1) * LIMB_BITS + bits
  }

  /**
   * Number of zero bits below the least significant one bit. This operation is defined only for
   * nonzero values and throws [IllegalArgumentException] for zero.
   */
  internal fun trailingZeroCount(): Int {
    require(!isZero) { "trailing-zero count is undefined for zero" }
    var count = 0
    var index = 0
    while (limbs[index] == 0) {
      count += LIMB_BITS
      index++
    }
    var value = limbs[index]
    while ((value and 1) == 0) {
      count++
      value = value ushr 1
    }
    return count
  }

  /** Compares signed values using their mathematical order. */
  override fun compareTo(other: ExactInteger): Int {
    if (sign != other.sign) return sign.compareTo(other.sign)
    if (sign == 0) return 0
    val magnitudeComparison = compareMagnitudes(limbs, other.limbs)
    return if (sign > 0) magnitudeComparison else -magnitudeComparison
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ExactInteger || sign != other.sign || limbs.size != other.limbs.size) {
      return false
    }
    return limbs.contentEquals(other.limbs)
  }

  override fun hashCode(): Int {
    var result = sign
    for (index in limbs.indices.reversed()) {
      result = 31 * result + limbs[index]
    }
    return result
  }

  internal companion object {
    private const val LIMB_BITS: Int = 30
    private const val LIMB_BASE: Int = 1 shl LIMB_BITS
    private const val LIMB_MASK: Long = LIMB_BASE.toLong() - 1L

    internal val ZERO: ExactInteger = ExactInteger(0, IntArray(0))
    internal val ONE: ExactInteger = ExactInteger(1, intArrayOf(1))

    internal fun fromLong(value: Long): ExactInteger {
      if (value == 0L) return ZERO
      val sign = if (value < 0L) -1 else 1
      // For Long.MIN_VALUE this deliberately remains the bit pattern 2^63. Unsigned shifts
      // extract its magnitude without first trying to represent 2^63 as a positive Long.
      var magnitude = if (value < 0L) 0L - value else value
      val result = IntArray((64 + LIMB_BITS - 1) / LIMB_BITS)
      var size = 0
      while (magnitude != 0L) {
        result[size++] = (magnitude and LIMB_MASK).toInt()
        magnitude = magnitude ushr LIMB_BITS
      }
      return ExactInteger(sign, result.copyOf(size))
    }

    private fun fromMagnitude(sign: Int, magnitude: IntArray): ExactInteger {
      var size = magnitude.size
      while (size > 0 && magnitude[size - 1] == 0) size--
      if (size == 0) return ZERO
      return ExactInteger(sign, magnitude.copyOf(size))
    }

    private fun compareMagnitudes(left: IntArray, right: IntArray): Int {
      if (left.size != right.size) return left.size.compareTo(right.size)
      for (index in left.lastIndex downTo 0) {
        if (left[index] != right[index]) return left[index].compareTo(right[index])
      }
      return 0
    }

    private fun divideMagnitudes(dividend: IntArray, divisor: IntArray): Pair<IntArray, IntArray> {
      require(divisor.isNotEmpty()) { "division by zero" }
      if (compareMagnitudes(dividend, divisor) < 0) {
        return IntArray(0) to dividend.copyOf()
      }
      if (divisor.size == 1) {
        val divisorLimb = divisor[0].toLong()
        val quotient = IntArray(dividend.size)
        var remainder = 0L
        for (index in dividend.lastIndex downTo 0) {
          val value = (remainder shl LIMB_BITS) + dividend[index].toLong()
          quotient[index] = (value / divisorLimb).toInt()
          remainder = value % divisorLimb
        }
        return quotient to if (remainder == 0L) IntArray(0) else intArrayOf(remainder.toInt())
      }

      // Process one dividend bit at a time. The remainder is a private scratch buffer and is
      // mutated in place, avoiding an integer allocation for every quotient bit.
      val quotient = IntArray(dividend.size)
      val remainder = IntArray(divisor.size + 1)
      val bitLength = magnitudeBitLength(dividend)
      for (bit in bitLength - 1 downTo 0) {
        var carry = (dividend[bit / LIMB_BITS] ushr (bit % LIMB_BITS)) and 1
        for (index in remainder.indices) {
          val shifted = (remainder[index].toLong() shl 1) + carry.toLong()
          remainder[index] = (shifted and LIMB_MASK).toInt()
          carry = (shifted ushr LIMB_BITS).toInt()
        }
        if (compareMagnitudesWithTrailingZeros(remainder, divisor) >= 0) {
          subtractInPlace(remainder, divisor)
          quotient[bit / LIMB_BITS] = quotient[bit / LIMB_BITS] or (1 shl (bit % LIMB_BITS))
        }
      }
      return quotient to remainder
    }

    private fun compareMagnitudesWithTrailingZeros(left: IntArray, right: IntArray): Int {
      var leftSize = left.size
      while (leftSize > 0 && left[leftSize - 1] == 0) leftSize--
      var rightSize = right.size
      while (rightSize > 0 && right[rightSize - 1] == 0) rightSize--
      if (leftSize != rightSize) return leftSize.compareTo(rightSize)
      for (index in leftSize - 1 downTo 0) {
        if (left[index] != right[index]) return left[index].compareTo(right[index])
      }
      return 0
    }

    private fun magnitudeBitLength(value: IntArray): Int {
      if (value.isEmpty()) return 0
      var high = value.last()
      var highBits = 0
      while (high != 0) {
        highBits++
        high = high ushr 1
      }
      return (value.size - 1) * LIMB_BITS + highBits
    }

    /** Subtracts [right] from [left] in place, assuming left >= right. */
    private fun subtractInPlace(left: IntArray, right: IntArray) {
      var borrow = 0L
      for (index in left.indices) {
        var difference =
          left[index].toLong() - (if (index < right.size) right[index] else 0).toLong() - borrow
        if (difference < 0L) {
          difference += LIMB_BASE.toLong()
          borrow = 1L
        } else {
          borrow = 0L
        }
        left[index] = difference.toInt()
      }
      check(borrow == 0L) { "magnitude subtraction underflow" }
    }

    private fun addMagnitudes(left: IntArray, right: IntArray): IntArray {
      val result = IntArray(maxOf(left.size, right.size) + 1)
      var carry = 0L
      for (index in 0 until result.lastIndex) {
        val sum =
          (if (index < left.size) left[index].toLong() else 0L) +
            (if (index < right.size) right[index].toLong() else 0L) +
            carry
        result[index] = (sum and LIMB_MASK).toInt()
        carry = sum ushr LIMB_BITS
      }
      result[result.lastIndex] = carry.toInt()
      return result
    }

    /** Subtracts [right] from [left], whose magnitudes satisfy left >= right. */
    private fun subtractMagnitudes(left: IntArray, right: IntArray): IntArray {
      val result = IntArray(left.size)
      var borrow = 0L
      for (index in left.indices) {
        var difference =
          left[index].toLong() - (if (index < right.size) right[index] else 0).toLong() - borrow
        if (difference < 0L) {
          difference += LIMB_BASE.toLong()
          borrow = 1L
        } else {
          borrow = 0L
        }
        result[index] = difference.toInt()
      }
      check(borrow == 0L) { "magnitude subtraction underflow" }
      return result
    }
  }
}
