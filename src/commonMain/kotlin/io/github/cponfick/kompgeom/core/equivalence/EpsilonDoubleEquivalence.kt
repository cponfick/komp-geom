package io.github.cponfick.kompgeom.core.equivalence

import kotlin.math.abs

/**
 * An implementation of [DoubleEquivalence] that uses an epsilon value to determine equality and
 * ordering of double values.
 *
 * @property epsilon The relative tolerance used for comparisons. Defaults to [GEOMETRIC_EPSILON].
 *
 * The epsilon must be finite and non-negative. Comparisons of finite values use `epsilon * max(1,
 * abs(a), abs(b))`. Non-finite values are ordered without applying a tolerance: negative infinity
 * is less than every finite value, positive infinity is greater than every finite value, and NaN is
 * greater than every non-NaN value. Two equal infinities and two NaN values compare equal.
 */
public open class EpsilonDoubleEquivalence(public val epsilon: Double = GEOMETRIC_EPSILON) :
  DoubleEquivalence {

  init {
    require(epsilon.isFinite() && epsilon >= 0.0) {
      "epsilon must be finite and non-negative, but was $epsilon"
    }
  }

  public override fun eq(a: Double, b: Double): Boolean = compare(a, b) == 0

  public override fun eqZero(a: Double): Boolean = eq(a, 0.0)

  public override fun lt(a: Double, b: Double): Boolean = compare(a, b) < 0

  public override fun lte(a: Double, b: Double): Boolean = compare(a, b) <= 0

  public override fun gt(a: Double, b: Double): Boolean = compare(a, b) > 0

  public override fun gte(a: Double, b: Double): Boolean = compare(a, b) >= 0

  public override fun signum(a: Double): Double {
    if (a == 0.0 || a.isNaN()) {
      return a
    }

    // TODO: optimization can be done here for example by using copySign in java.
    //  This has to be done for every platform. Take a look at a later point in time.
    return if (eqZero(a)) {
      if (a < 0.0) -0.0 else 0.0
    } else {
      if (a < 0.0) -1.0 else 1.0
    }
  }

  /**
   * Compares two double values considering the defined precision.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return `0` if the values are considered equal, `-1` if `a` is less than `b`, and `1` if `a` is
   *   greater than `b`.
   */
  public open fun compare(a: Double, b: Double): Int {
    // Handle these before subtraction: inf - inf and any operation involving NaN produce NaN,
    // and an infinite tolerance would otherwise make infinity equal to every finite value.
    if (a.isNaN() || b.isNaN()) {
      return when {
        a.isNaN() && b.isNaN() -> 0
        a.isNaN() -> 1
        else -> -1
      }
    }
    if (
      a == Double.POSITIVE_INFINITY ||
        a == Double.NEGATIVE_INFINITY ||
        b == Double.POSITIVE_INFINITY ||
        b == Double.NEGATIVE_INFINITY
    ) {
      return when {
        a == b -> 0
        a == Double.NEGATIVE_INFINITY || b == Double.POSITIVE_INFINITY -> -1
        else -> 1
      }
    }

    val diff = abs(a - b)
    val tolerance = epsilon * maxOf(1.0, abs(a), abs(b))
    return when {
      diff <= tolerance -> 0
      a < b -> -1
      else -> 1
    }
  }
}
