package io.github.cponfick.kompgeom.core

/**
 * This interface represents a vector in a multi-dimensional space.
 *
 * @param V The type of the vector that implements this interface.
 * @see Spatial
 * @see Distanceable
 */
public interface Vector<V : Vector<V>> : Spatial, Distanceable<V> {
  /**
   * Adds another vector to this vector.
   *
   * @param other The vector to add.
   * @return A vector representing the result of the addition.
   */
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
   * Calculate the dot product of this vector with another vector.
   *
   * @param other The other vector to calculate the dot product with.
   * @return The dot product.
   */
  public infix fun dot(other: V): Double

  /**
   * Scale this vector by a scalar value.
   *
   * @param scalar The scalar value to multiply the vector by.
   * @return A vector representing the scaled vector.
   */
  public operator fun times(scalar: Double): V

  /**
   * Negates the vector, flipping its direction.
   *
   * @return A vector that points in the opposite direction.
   */
  public operator fun unaryMinus(): V

  /**
   * Normalizes the vector.
   *
   * @return A vector that has the same direction as this vector but with a length of 1.
   */
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
   * @return A vector representing the result of the subtraction.
   */
  public operator fun minus(other: V): V

  /**
   * Get Zero vector.
   *
   * @return A zero vector of the same type as this vector.
   */
  public fun zero(): V
}

/**
 * This interface represents a vector in one-dimensional space.
 *
 * @param V The type of the vector that implements this interface.
 * @see Vector
 */
public interface Vector1<V : Vector1<V>> : Vector<V> {
  /** The x component of the vector. */
  public val x: Double

  /**
   * Creates a vector with the specified component.
   *
   * @param x The x component.
   * @return A vector with the specified component.
   */
  public fun withComponents(x: Double): V
}

/**
 * This interface represents a vector in two-dimensional space.
 *
 * @param V The type of the vector that implements this interface.
 * @see Vector
 */
public interface Vector2<V : Vector2<V>> : Vector<V> {
  /** The x component of the vector. */
  public val x: Double
  /** The y component of the vector. */
  public val y: Double

  /**
   * Creates a vector with the specified components.
   *
   * @param x The x component.
   * @param y The y component.
   * @return A vector with the specified components.
   */
  public fun withComponents(x: Double, y: Double): V
}

/**
 * This interface represents a vector in three-dimensional space.
 *
 * @param V The type of the vector that implements this interface.
 * @see Vector
 */
public interface Vector3<V : Vector3<V>> : Vector<V> {
  /** The x component of the vector. */
  public val x: Double
  /** The y component of the vector. */
  public val y: Double
  /** The z component of the vector. */
  public val z: Double

  /**
   * Creates a vector with the specified components.
   *
   * @param x The x component.
   * @param y The y component.
   * @param z The z component.
   * @return A vector with the specified components.
   */
  public fun withComponents(x: Double, y: Double, z: Double): V

  /**
   * Calculates the cross product of this vector with another vector.
   *
   * @param other The vector to cross with.
   * @return The resulting vector from the cross product.
   */
  public infix fun cross(other: Vector3<*>): V
}
