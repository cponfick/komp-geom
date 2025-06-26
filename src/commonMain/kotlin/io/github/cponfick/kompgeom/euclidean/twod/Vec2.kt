package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.euclidean.MultiDimensionalEuclideanVector
import io.github.cponfick.kompgeom.euclidean.assertIsFiniteAndNotZero
import kotlin.math.sqrt

/**
 * This class represents two-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 */
public class Vec2(public val x: Double, public val y: Double) :
  MultiDimensionalEuclideanVector<Vec2>() {

  /**
   * Creates a new vector by copying the coordinates from another vector.
   *
   * @param vector The vector to copy.
   */
  public constructor(vector: Vec2) : this(vector.x, vector.y)

  override fun project(base: Vec2): Vec2 {
    val scale = computeScale(base)
    return Vec2(scale * base.x, scale * base.y)
  }

  override fun reject(base: Vec2): Vec2 {
    val scale = computeScale(base)
    return Vec2(this.x - scale * base.x, this.y - scale * base.y)
  }

  private fun computeScale(other: Vec2): Double {
    val dotProduct = this dot other
    val denominator = (other dot other).assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  override fun eq(other: Vec2, equivalence: DoubleEquivalence): Boolean =
    equivalence.eq(this.x, other.x) && equivalence.eq(this.y, other.y)

  override fun lerp(other: Vec2, t: Double): Vec2 =
    Vec2(x + (other.x - x) * t, y + (other.y - y) * t)

  override fun distance(other: Vec2): Double =
    sqrt((this.x - other.x).let { it * it } + (this.y - other.y).let { it * it })

  override fun dimensions(): Int = DIMENSIONS

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN()

  override fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  override fun dot(other: Vec2): Double = this.x * other.x + this.y * other.y

  override fun times(scalar: Double): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  override fun unaryMinus(): Vec2 = Vec2(-x, -y)

  override fun normalize(): Vec2 {
    val norm = norm()
    if (norm == 0.0) {
      throw ArithmeticException("Cannot normalize a vector with a magnitude of zero.")
    }
    return Vec2(x / norm, y / norm)
  }

  override fun norm(): Double = sqrt(x * x + y * y)

  override fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Vec2) return false
    return this.x.equals(other.x) && this.y.equals(other.y)
  }

  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    return result
  }

  override fun toString(): String = "Vec2(x=$x, y=$y)"

  public companion object {
    private const val DIMENSIONS = 2

    /** The zero vector. */
    public val ZERO: Vec2 = Vec2(0.0, 0.0)
    /** Vector with all components set to positive infinity. */
    public val POSITIVE_INFINITY: Vec2 = Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    /** Vector with all components set to negative infinity. */
    public val NEGATIVE_INFINITY: Vec2 = Vec2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
    /** Vector with all components set to NaN. */
    public val NaN: Vec2 = Vec2(Double.NaN, Double.NaN)
  }
}
