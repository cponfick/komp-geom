package io.github.cponfick.kompgeom.core.equivalence

public interface DoubleEquivalence {
  /**
   * Indicates if given double values are equal.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return `true` if the two values are equal, `false` otherwise.
   */
  public fun eq(a: Double, b: Double): Boolean

  /**
   * Checks if a double value is equal to zero.
   *
   * @param a The double value to check.
   * @return `true` if `a` is equal to zero, `false` otherwise.
   */
  public fun eqZero(a: Double): Boolean

  /**
   * Checks if value `a` is strictly smaller than value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is less than `b`, `false` otherwise.
   */
  public fun lt(a: Double, b: Double): Boolean

  /**
   * Checks if value `a` is smaller than or equal to value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is less than or equal to `b`, `false` otherwise.
   */
  public fun lte(a: Double, b: Double): Boolean

  /**
   * Checks if value `a` is strictly greater than value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is greater than `b`, `false` otherwise.
   */
  public fun gt(a: Double, b: Double): Boolean

  /**
   * Checks if value `a` is greater than or equal to value `b`.
   *
   * @param a The first double value to check.
   * @param b The second double value to check.
   * @return `true` if `a` is greater than or equal to `b`, `false` otherwise.
   */
  public fun gte(a: Double, b: Double): Boolean

  /**
   * Returns the sign of a double value.
   * - -0.0 if the value is considered zero and negatively signed,
   * - +0.0 if the value is considered zero and positively signed,
   * - -1.0 if the value is considered less than zero,
   * - +1.0 if the value is considered greater than zero.
   *
   * @param a The double value to check.
   * @return -1.0 if `a` is negative, 0.0 if `a` is zero, and 1.0 if `a` is positive.
   */
  public fun signum(a: Double): Double
}
