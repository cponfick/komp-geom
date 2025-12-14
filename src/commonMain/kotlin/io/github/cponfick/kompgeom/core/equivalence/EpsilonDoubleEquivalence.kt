package io.github.cponfick.kompgeom.core.equivalence

import kotlin.math.abs

/**
 * An implementation of [DoubleEquivalence] that uses an epsilon value to determine equality and
 * ordering of double values.
 *
 * @property epsilon The relative tolerance used for comparisons. Defaults to [GEOMETRIC_EPSILON].
 */
public open class EpsilonDoubleEquivalence(public val epsilon: Double = GEOMETRIC_EPSILON) :
  DoubleEquivalence {

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
    val diff = abs(a - b)
    val tolerance = epsilon * maxOf(1.0, abs(a), abs(b))
    return when {
      diff <= tolerance -> 0
      a < b -> -1
      else -> 1
    }
  }
}
