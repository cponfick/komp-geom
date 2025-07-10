package io.github.cponfick.kompgeom.core

import kotlin.math.abs

/**
 * Represents the precision used in geometric computations.
 *
 * This value is based on the IEEE 754 standard for double-precision floating-point numbers.
 */
public var DBL_EPSILON: Double = 2.220446049250313e-16 // 2^-52

/** The default double equivalence used in geometric computations. */
public var DEFAULT_DOUBLE_EQUIVALENCE: DoubleEquivalence = DoubleEquivalence()

/**
 * A class that provides methods for comparing double values with a specified precision.
 *
 * This class is used to determine the equality and order of double values while considering
 * floating-point precision errors.
 *
 * @property epsilon The precision threshold for comparing double values. Defaults to [DBL_EPSILON].
 */
public open class DoubleEquivalence(public val epsilon: Double = DBL_EPSILON) {
  /**
   * Indicates if given double values are equal.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return `true` if the two values are equal, `false` otherwise.
   */
  public fun eq(a: Double, b: Double): Boolean = compare(a, b) == 0

  /**
   * Checks if a double value is equal to zero.
   *
   * @param a The double value to check.
   * @return `true` if `a` is equal to zero, `false` otherwise.
   */
  public fun eqZero(a: Double): Boolean = eq(a, 0.0)

  /**
   * Checks if value `a` is strictly smaller than value `b`.
   *
   * @param a The double value to check.
   * @return `true` if `a` is greater than zero, `false` otherwise.
   */
  public fun lt(a: Double, b: Double): Boolean = compare(a, b) < 0

  /**
   * Checks if value `a` is smaller than or equal to value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is less than or equal to `b`, `false` otherwise.
   */
  public fun lte(a: Double, b: Double): Boolean = compare(a, b) <= 0

  /**
   * Checks if value `a` is strictly greater than value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is greater than `b`, `false` otherwise.
   */
  public fun gt(a: Double, b: Double): Boolean = compare(a, b) > 0

  /**
   * Checks if value `a` is greater than or equal to value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is greater than or equal to `b`, `false` otherwise.
   */
  public fun gte(a: Double, b: Double): Boolean = compare(a, b) >= 0

  /**
   * Compares two double values.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return An integer indicating the comparison result: 0 for equality, -1 if `a` is less than
   *   `b`, and 1 if `a` is greater than `b`.
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
