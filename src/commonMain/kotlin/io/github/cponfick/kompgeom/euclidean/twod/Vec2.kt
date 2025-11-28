package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Vector
import io.github.cponfick.kompgeom.euclidean.utils.VectorUtil
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero
import kotlin.math.sqrt

/**
 * This class represents two-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 */
public data class Vec2(public val x: Double, public val y: Double) : Vector<Vec2> {

  /**
   * Creates a new vector by copying the coordinates from another vector.
   *
   * @param vector The vector to copy.
   */
  public constructor(vector: Vec2) : this(vector.x, vector.y)

  /**
   * Calculate the projection of this vector onto another vector.
   *
   * @param base The vector onto which to project this vector.
   * @return The projection of this vector onto the other vector.
   */
  public fun project(base: Vec2): Vec2 {
    val scale = computeScale(base)
    return Vec2(scale * base.x, scale * base.y)
  }

  /**
   * Calculates the rejection of this vector from another vector.
   *
   * @param base The vector from which to reject this vector.
   * @return The rejection of this vector from the other vector.
   */
  public fun reject(base: Vec2): Vec2 {
    val scale = computeScale(base)
    return Vec2(this.x - scale * base.x, this.y - scale * base.y)
  }

  private fun computeScale(other: Vec2): Double {
    val dotProduct = this dot other
    val denominator = (other dot other).assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  /**
   * Tests if two vectors are approximately equal.
   *
   * @param other The other vector to compare with.
   * @param equivalence The equivalence used to compare the double values. Default is
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the vectors are approximately equal, false otherwise.
   */
  public fun eq(other: Vec2, equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE): Boolean =
    equivalence.eq(this.x, other.x) && equivalence.eq(this.y, other.y)

  /**
   * Performs linear interpolation between this vector and another vector.
   *
   * @param other The target vector for interpolation.
   * @param t The interpolation parameter (0.0 returns this vector, 1.0 returns other vector).
   */
  public fun lerp(other: Vec2, t: Double): Vec2 = Vec2(x + (other.x - x) * t, y + (other.y - y) * t)

  override infix fun distance(other: Vec2): Double =
    sqrt((this.x - other.x).let { it * it } + (this.y - other.y).let { it * it })

  override fun dimensions(): Int = 2

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN()

  override operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  override infix fun dot(other: Vec2): Double = this.x * other.x + this.y * other.y

  override operator fun times(scalar: Double): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  override operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

  override fun normalize(): Vec2 {
    val norm = VectorUtil.norm(x, y)
    val inverseNorm = 1.0 / norm
    if (norm == 0.0) {
      throw ArithmeticException("Cannot create a unit vector from a zero vector.")
    }
    return Vec2(x * inverseNorm, y * inverseNorm)
  }

  override fun norm(): Double = VectorUtil.norm(x, y)

  override operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  override fun zero(): Vec2 = ZERO

  override fun angle(other: Vec2, angleUnit: AngleUnit): Double =
    VectorUtil.calculateAngle(this dot other, this.norm(), other.norm(), angleUnit)

  /**
   * Computes the signed area of the parallelogram formed by this vector and another vector.
   *
   * @param other The other vector.
   * @return The signed area of the parallelogram.
   */
  public fun signedArea(other: Vec2): Double = VectorUtil.linearCombination(x, other.y, -y, other.x)

  public companion object {
    /** The zero vector. */
    public val ZERO: Vec2 = Vec2(0.0, 0.0)
    /** Vector with all components set to positive infinity. */
    public val POSITIVE_INFINITY: Vec2 = Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    /** Vector with all components set to negative infinity. */
    public val NEGATIVE_INFINITY: Vec2 = Vec2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
    /** Vector with all components set to NaN. */
    public val NaN: Vec2 = Vec2(Double.NaN, Double.NaN)

    /**
     * Creates a unit vector from a given x and y coordinate.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     * @return A unit vector with the same direction as the coordinates.
     * @throws ArithmeticException if both x and y are zero.
     */
    public fun unit(x: Double, y: Double): Vec2 = Vec2(x, y).normalize()
  }
}
