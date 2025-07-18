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
}
