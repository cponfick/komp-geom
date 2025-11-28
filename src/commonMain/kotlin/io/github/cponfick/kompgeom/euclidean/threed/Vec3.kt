package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.*
import io.github.cponfick.kompgeom.euclidean.utils.VectorUtil
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero
import kotlin.math.sqrt

public data class Vec3(public val x: Double, public val y: Double, public val z: Double) :
  Spatial, Distanceable<Vec3> {

  /**
   * Creates a new vector by copying the coordinates from another vector.
   *
   * @param vector The vector to copy.
   */
  public constructor(vector: Vec3) : this(vector.x, vector.y, vector.z)

  /**
   * Calculates the cross product of this vector with another vector.
   *
   * @param other The vector to cross with.
   * @return The resulting vector from the cross product.
   */
  public infix fun cross(other: Vec3): Vec3 =
    Vec3(
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
  public fun project(base: Vec3): Vec3 {
    val scale = computeScale(base)
    return Vec3(scale * base.x, scale * base.y, scale * base.z)
  }

  /**
   * Calculates the rejection of this vector from another vector.
   *
   * @param base The vector from which to reject this vector.
   * @return The rejection of this vector from the other vector.
   */
  public fun reject(base: Vec3): Vec3 {
    val scale = computeScale(base)
    return Vec3(this.x - scale * base.x, this.y - scale * base.y, this.z - scale * base.z)
  }

  private fun computeScale(other: Vec3): Double {
    val dotProduct = this dot other
    val denominator = (other dot other).assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  /**
   * Checks if two vectors are equal within a specified tolerance.
   *
   * @param other The other vector to compare with.
   * @param equivalence The tolerance used for comparison. Default is [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the vectors are equal within the tolerance, false otherwise.
   */
  public fun eq(other: Vec3, equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE): Boolean =
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
  public fun lerp(other: Vec3, t: Double): Vec3 =
    Vec3(x + (other.x - x) * t, y + (other.y - y) * t, z + (other.z - z) * t)

  override fun distance(other: Vec3): Double =
    sqrt(
      (this.x - other.x).let { it * it } +
        (this.y - other.y).let { it * it } +
        (this.z - other.z).let { it * it }
    )

  override fun dimensions(): Int = 3

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite() || z.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN() || z.isNaN()

  /**
   * Adds two vectors component-wise.
   *
   * @param other The vector to add.
   * @return The sum of the vectors.
   */
  public operator fun plus(other: Vec3): Vec3 =
    Vec3(this.x + other.x, this.y + other.y, this.z + other.z)

  /**
   * Calculates the dot product of this vector with another vector.
   *
   * @param other The vector to dot with.
   * @return The dot product of the two vectors.
   */
  public infix fun dot(other: Vec3): Double = this.x * other.x + this.y * other.y + this.z * other.z

  /**
   * Multiplies this vector by a scalar.
   *
   * @param scalar The scalar to multiply with.
   * @return The resulting vector after multiplication.
   */
  public operator fun times(scalar: Double): Vec3 =
    Vec3(this.x * scalar, this.y * scalar, this.z * scalar)

  /**
   * Negates the vector, i.e., multiplies each component by -1.
   *
   * @return A new vector with each component negated.
   * @return The negated vector.
   */
  public operator fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

  /**
   * Normalizes the vector to a unit vector.
   *
   * @return A new vector with the same direction but a magnitude of 1.
   * @throws ArithmeticException if the vector is a zero vector.
   */
  public fun normalize(): Vec3 {
    val norm = sqrt(x * x + y * y + z * z)
    val inverseNorm = 1.0 / norm
    if (norm == 0.0) {
      throw ArithmeticException("Cannot create a unit vector from a zero vector")
    }
    return Vec3(x * inverseNorm, y * inverseNorm, z * inverseNorm)
  }

  /**
   * Calculates the Euclidean norm (magnitude) of the vector.
   *
   * @return The Euclidean norm of the vector.
   */
  public fun norm(): Double = sqrt(x * x + y * y + z * z)

  /**
   * Returns the number of dimensions of the vector.
   *
   * @param other The other vector.
   * @return The number of dimensions (3 for Vec3).
   */
  public operator fun minus(other: Vec3): Vec3 =
    Vec3(this.x - other.x, this.y - other.y, this.z - other.z)

  public fun angle(other: Vec3, angleUnit: AngleUnit): Double =
    VectorUtil.calculateAngle(this dot other, this.norm(), other.norm(), angleUnit)

  public companion object {
    /** The zero vector. */
    public val ZERO: Vec3 = Vec3(0.0, 0.0, 0.0)
    /** Vector with all components set to positive infinity. */
    public val POSITIVE_INFINITY: Vec3 =
      Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    /** Vector with all components set to negative infinity. */
    public val NEGATIVE_INFINITY: Vec3 =
      Vec3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
    /** Vector with all components set to NaN. */
    public val NaN: Vec3 = Vec3(Double.NaN, Double.NaN, Double.NaN)
  }
}
