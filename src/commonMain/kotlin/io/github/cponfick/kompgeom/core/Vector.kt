package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil.linearCombination
import io.github.cponfick.kompgeom.euclidean.oned.Vec1
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sqrt

/**
 * This interface represents a vector in a multidimensional space.
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

  /**
   * Checks if two vectors are equal within a specified tolerance.
   *
   * @param other The other vector to compare with.
   * @param equivalence The tolerance used for comparison.
   * @return True if the vectors are equal within the tolerance, false otherwise.
   */
  public fun eq(
    other: Vector1<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean = equivalence.eq(this.x, other.x)

  /**
   * Calculates the squared norm of the vector.
   *
   * @return The squared norm of the vector.
   */
  public fun normSquared(): Double = x * x

  override fun angle(other: V, angleUnit: AngleUnit): Double {
    this.x.assertIsFiniteAndNotZero()
    other.x.assertIsFiniteAndNotZero()
    return when (angleUnit) {
      AngleUnit.RADIANS -> if (this.x == other.x) 0.0 else PI
      AngleUnit.DEGREES -> if (this.x == other.x) 0.0 else 180.0
    }
  }

  /**
   * Performs linear interpolation between two vectors.
   *
   * @param other The target vector for interpolation.
   * @param t The interpolation parameter (0.0 returns this vector, 1.0 returns other vector).
   * @return The interpolated vector.
   */
  public fun lerp(other: Vector1<*>, t: Double): Vec1 = Vec1(x + (other.x - x) * t)

  override fun normalize(): V {
    if (x == 0.0) {
      throw ArithmeticException("Cannot normalize a vector with zero length.")
    }
    return this.withComponents(x / x.absoluteValue)
  }

  override fun norm(): Double = x.absoluteValue

  override fun dimensions(): Int = 1

  override fun isFinite(): Boolean = x.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite()

  override fun isNaN(): Boolean = x.isNaN()

  override fun dot(other: V): Double = this.x * other.x

  override fun distance(other: V): Double = (this.x - other.x).absoluteValue
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

  /**
   * Calculates the scale factor to project this vector onto another vector.
   *
   * @param other The vector onto which to project this vector.
   * @return The scale factor for the projection.
   */
  public fun computeScaleFactor(other: Vector2<*>): Double {
    val dotProduct = linearCombination(this.x, other.x, this.y, other.y)
    val denominator =
      linearCombination(other.x, other.x, other.y, other.y).assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  /**
   * Calculate the projection of this vector onto another vector.
   *
   * @param base The vector onto which to project this vector.
   * @return The projection of this vector onto the other vector.
   */
  public fun project(base: Vector2<*>): V {
    val scale = computeScaleFactor(base)
    return withComponents(scale * base.x, scale * base.y)
  }

  /**
   * Calculates the rejection of this vector from another vector.
   *
   * @param base The vector from which to reject this vector.
   * @return The rejection of this vector from the other vector.
   */
  public fun reject(base: Vector2<*>): V {
    val scale = computeScaleFactor(base)
    return withComponents(this.x - scale * base.x, this.y - scale * base.y)
  }

  /**
   * Tests if two vectors are approximately equal.
   *
   * @param other The other vector to compare with.
   * @param equivalence The equivalence used to compare the double values. Default is
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the vectors are approximately equal, false otherwise.
   */
  public fun eq(
    other: Vector2<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean = equivalence.eq(this.x, other.x) && equivalence.eq(this.y, other.y)

  /**
   * Performs linear interpolation between this vector and another vector.
   *
   * @param other The target vector for interpolation.
   * @param t The interpolation parameter (0.0 returns this vector, 1.0 returns other vector).
   */
  public fun lerp(other: Vector2<*>, t: Double): V =
    withComponents(x + (other.x - x) * t, y + (other.y - y) * t)

  /**
   * Computes the signed area of the parallelogram formed by this vector and another vector.
   *
   * @param other The other vector.
   * @return The signed area of the parallelogram.
   */
  public fun signedArea(other: Vector2<*>): Double = linearCombination(x, other.y, -y, other.x)

  override infix fun distance(other: V): Double =
    sqrt((this.x - other.x).let { it * it } + (this.y - other.y).let { it * it })

  override fun dimensions(): Int = 2

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN()

  override fun normalize(): V {
    val norm = VectorUtil.norm(x, y)
    val inverseNorm = 1.0 / norm
    if (norm == 0.0) {
      throw ArithmeticException("Cannot create a unit vector from a zero vector.")
    }
    return this.withComponents(x * inverseNorm, y * inverseNorm)
  }

  override fun norm(): Double = VectorUtil.norm(x, y)

  override fun angle(other: V, angleUnit: AngleUnit): Double =
    VectorUtil.calculateAngle(this dot other, this.norm(), other.norm(), angleUnit)

  override fun dot(other: V): Double = this.x * other.x + this.y * other.y
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
  public infix fun cross(other: Vector3<*>): V =
    withComponents(
      this.y * other.z - this.z * other.y,
      this.z * other.x - this.x * other.z,
      this.x * other.y - this.y * other.x,
    )

  /**
   * Calculate the projection of this vector onto another vector.
   *
   * @param base The vector onto which to project this vector.
   * @return The projection of this vector onto the other vector.
   */
  public fun project(base: Vector3<*>): V {
    val scale = computeScaleFactor(base)
    return withComponents(scale * base.x, scale * base.y, scale * base.z)
  }

  /**
   * Calculates the rejection of this vector from another vector.
   *
   * @param base The vector from which to reject this vector.
   * @return The rejection of this vector from the other vector.
   */
  public fun reject(base: Vector3<*>): V {
    val scale = computeScaleFactor(base)
    return withComponents(this.x - scale * base.x, this.y - scale * base.y, this.z - scale * base.z)
  }

  /**
   * Calculates the scale factor to project this vector onto another vector.
   *
   * @param other The vector onto which to project this vector.
   * @return The scale factor for the projection.
   */
  public fun computeScaleFactor(other: Vector3<*>): Double {
    val dotProduct = linearCombination(this.x, other.x, this.y, other.y, this.z, other.z)
    val denominator =
      linearCombination(other.x, other.x, other.y, other.y, other.z, other.z)
        .assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  /**
   * Checks if two vectors are equal within a specified tolerance.
   *
   * @param other The other vector to compare with.
   * @param equivalence The tolerance used for comparison. Default is [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the vectors are equal within the tolerance, false otherwise.
   */
  public fun eq(
    other: Vector3<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean =
    equivalence.eq(this.x, other.x) &&
      equivalence.eq(this.y, other.y) &&
      equivalence.eq(this.z, other.z)

  /**
   * Performs linear interpolation between this vector and another vector.
   *
   * @param other The target vector for interpolation.
   * @param t The interpolation parameter (0.0 returns this vector, 1.0 returns other vector).
   * @return The interpolated vector.
   */
  public fun lerp(other: Vector3<*>, t: Double): V =
    withComponents(x + (other.x - x) * t, y + (other.y - y) * t, z + (other.z - z) * t)

  override fun dimensions(): Int = 3

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite() || z.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN() || z.isNaN()

  override fun distance(other: V): Double =
    sqrt(
      (this.x - other.x).let { it * it } +
        (this.y - other.y).let { it * it } +
        (this.z - other.z).let { it * it }
    )

  override fun dot(other: V): Double =
    linearCombination(this.x, other.x, this.y, other.y, this.z, other.z)

  override fun norm(): Double = VectorUtil.norm(this.x, this.y, this.z)

  override fun angle(other: V, angleUnit: AngleUnit): Double =
    VectorUtil.calculateAngle(this dot other, this.norm(), other.norm(), angleUnit)
}
