package io.github.cponfick.kompgeom.euclidean.internal

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.RADIANS_TO_DEGREES
import io.github.cponfick.kompgeom.core.assertIsFiniteAndNotZero
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import kotlin.math.acos
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
  internal fun norm(x: Double, y: Double): Double = sqrt(x * x + y * y)

  /**
   * Computes the inverse of the Euclidean norm (magnitude) of a vector defined by its x and y
   * components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   * @param equivalence The tolerance used to check for a zero norm. Default is
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return The inverse of the Euclidean norm of the vector.
   * @throws ArithmeticException if the vector is a zero vector.
   */
  internal fun inverseNorm(
    x: Double,
    y: Double,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Double {
    val norm = norm(x, y)
    if (equivalence.eqZero(norm)) {
      throw ArithmeticException("Cannot compute inverse norm of a zero vector.")
    }
    return 1.0 / norm
  }

  /**
   * Computes the Euclidean norm (magnitude) of a vector defined by its x, y, and z components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   * @param z The z component of the vector.
   * @return The Euclidean norm of the vector.
   */
  internal fun norm(x: Double, y: Double, z: Double): Double = sqrt(x * x + y * y + z * z)

  /**
   * Computes the inverse of the Euclidean norm (magnitude) of a vector defined by its x, y, and z
   * components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   * @param z The z component of the vector.
   * @param equivalence The tolerance used to check for a zero norm. Default is
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return The inverse of the Euclidean norm of the vector.
   * @throws ArithmeticException if the vector is a zero vector.
   */
  internal fun inverseNorm(
    x: Double,
    y: Double,
    z: Double,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Double {
    val norm = norm(x, y, z)
    if (equivalence.eqZero(norm)) {
      throw ArithmeticException("Cannot compute inverse norm of a zero vector.")
    }
    return 1.0 / norm
  }

  /**
   * Computes the linear combination a1 * b1 + a2 * b2 of two pairs of doubles.
   *
   * @param a1 The first value of the first pair.
   * @param b1 The second value of the first pair.
   * @param a2 The first value of the second pair.
   * @param b2 The second value of the second pair.
   * @return The result of the linear combination.
   */
  internal fun linearCombination(a1: Double, b1: Double, a2: Double, b2: Double): Double =
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
  internal fun linearCombination(
    a1: Double,
    b1: Double,
    a2: Double,
    b2: Double,
    a3: Double,
    b3: Double,
  ): Double = a1 * b1 + a2 * b2 + a3 * b3

  /**
   * Calculates the angle between two vectors using the dot product and their norms.
   *
   * @param dotProduct The dot product of the two vectors.
   * @param norm1 The norm (magnitude) of the first vector.
   * @param norm2 The norm (magnitude) of the second vector.
   * @param angleUnit The unit of the angle to return (radians or degrees).
   * @return The angle between the two vectors in the specified unit.
   */
  internal fun calculateAngle(
    dotProduct: Double,
    norm1: Double,
    norm2: Double,
    angleUnit: AngleUnit,
  ): Double {
    val lengthsProduct = (norm1 * norm2).assertIsFiniteAndNotZero()
    val cosAlpha = dotProduct / lengthsProduct
    val angle = acos(cosAlpha)

    return when (angleUnit) {
      AngleUnit.RADIANS -> angle
      AngleUnit.DEGREES -> angle * RADIANS_TO_DEGREES
    }
  }
}
