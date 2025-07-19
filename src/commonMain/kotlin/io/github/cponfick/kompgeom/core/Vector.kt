package io.github.cponfick.kompgeom.core

/** This interface represents a vector in a multi-dimensional space. */
public interface Vector<V : Vector<V>> : Spatial {
  /** Add a vector to this vector. */
  public operator fun plus(other: V): V

  /**
   * Calculate the angle between this vector and another vector.
   *
   * @param other The other vector to calculate the angle with.
   * @param angleUnit The unit of the angle (default is radians).
   * @return The angle in the specified unit.
   */
  public fun angle(other: V, angleUnit: AngleUnit = AngleUnit.RADIANS): Double

  /**
   * Calculate the distance between this vector and another vector.
   *
   * @param other The other vector to calculate the distance to.
   * @return The distance between the two vectors.
   */
  public infix fun distance(other: V): Double

  /** Calculate the dot product of this vector with another vector. */
  public infix fun dot(other: V): Double

  /**
   * Scale this vector by a scalar value.
   *
   * @param scalar The scalar value to multiply the vector by.
   */
  public operator fun times(scalar: Double): V

  /** Negates the vector, flipping its direction. */
  public operator fun unaryMinus(): V

  /** Normalizes the vector. */
  public fun normalize(): V

  /**
   * Calculates the norm (magnitude) of the vector.
   *
   * @return The norm of the vector, which is the square root of the sum of the squares of its
   *   components.
   */
  public fun norm(): Double

  /**
   * Subtracts another vector from this vector.
   *
   * @param other The vector to subtract.
   * @return A new vector representing the result of the subtraction.
   */
  public operator fun minus(other: V): V

  /**
   * Get Zero vector.
   *
   * @return A zero vector of the same type as this vector.
   */
  public fun zero(): V
}
