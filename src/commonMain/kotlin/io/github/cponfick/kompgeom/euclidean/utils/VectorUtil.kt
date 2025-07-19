package io.github.cponfick.kompgeom.euclidean.utils

import kotlin.math.sqrt

/** Utility for vector operations. */
public object VectorUtil {
  /**
   * Computes the Euclidean norm (magnitude) of a vector defined by its x and y components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   * @return The Euclidean norm of the vector.
   */
  public fun norm(x: Double, y: Double): Double = sqrt(x * x + y * y)

  /**
   * Computes the Euclidean norm (magnitude) of a vector defined by its x, y, and z components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   * @param z The z component of the vector.
   * @return The Euclidean norm of the vector.
   */
  public fun norm(x: Double, y: Double, z: Double): Double = sqrt(x * x + y * y + z * z)

  /**
   * Computes the linear combination a1 * b1 + a2 * b2 of two pairs of doubles.
   *
   * @param a1 The first value of the first pair.
   * @param b1 The second value of the first pair.
   * @param a2 The first value of the second pair.
   * @param b2 The second value of the second pair.
   * @return The result of the linear combination.
   */
  public fun linearCombination(a1: Double, b1: Double, a2: Double, b2: Double): Double =
    a1 * b1 + a2 * b2

  /**
   * Computes the linear combination a1 * b1 + a2 * b2 + a3 * b3 of three pairs of doubles.
   *
   * @param a1 The first value of the first pair.
   * @param b1 The second value of the first pair.
   * @param a2 The first value of the second pair.
   * @param b2 The second value of the second pair.
   * @param a3 The first value of the third pair.
   * @param b3 The second value of the third pair.
   */
  public fun linearCombination(
    a1: Double,
    b1: Double,
    a2: Double,
    b2: Double,
    a3: Double,
    b3: Double,
  ): Double = a1 * b1 + a2 * b2 + a3 * b3
}
