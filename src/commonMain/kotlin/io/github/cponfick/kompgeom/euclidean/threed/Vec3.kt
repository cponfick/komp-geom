package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.euclidean.MultiDimensionalEuclideanVector
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero
import kotlin.math.sqrt

public open class Vec3(public val x: Double, public val y: Double, public val z: Double) :
  MultiDimensionalEuclideanVector<Vec3>() {

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

  override fun project(base: Vec3): Vec3 {
    val scale = computeScale(base)
    return Vec3(scale * base.x, scale * base.y, scale * base.z)
  }

  override fun reject(base: Vec3): Vec3 {
    val scale = computeScale(base)
    return Vec3(this.x - scale * base.x, this.y - scale * base.y, this.z - scale * base.z)
  }

  private fun computeScale(other: Vec3): Double {
    val dotProduct = this dot other
    val denominator = (other dot other).assertIsFiniteAndNotZero()
    return dotProduct / denominator
  }

  override fun eq(other: Vec3, equivalence: DoubleEquivalence): Boolean =
    equivalence.eq(this.x, other.x) &&
      equivalence.eq(this.y, other.y) &&
      equivalence.eq(this.z, other.z)

  override fun lerp(other: Vec3, t: Double): Vec3 =
    Vec3(x + (other.x - x) * t, y + (other.y - y) * t, z + (other.z - z) * t)

  override fun distance(other: Vec3): Double =
    sqrt(
      (this.x - other.x).let { it * it } +
        (this.y - other.y).let { it * it } +
        (this.z - other.z).let { it * it }
    )

  override fun dimensions(): Int = DIMENSIONS

  override fun isFinite(): Boolean = x.isFinite() && y.isFinite() && z.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite() || y.isInfinite() || z.isInfinite()

  override fun isNaN(): Boolean = x.isNaN() || y.isNaN() || z.isNaN()

  override fun plus(other: Vec3): Vec3 = Vec3(this.x + other.x, this.y + other.y, this.z + other.z)

  override fun dot(other: Vec3): Double = this.x * other.x + this.y * other.y + this.z * other.z

  override fun times(scalar: Double): Vec3 = Vec3(this.x * scalar, this.y * scalar, this.z * scalar)

  override fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

  override fun normalize(): Vec3 = Unit.from(x, y, z)

  override fun norm(): Double = sqrt(x * x + y * y + z * z)

  override fun minus(other: Vec3): Vec3 = Vec3(this.x - other.x, this.y - other.y, this.z - other.z)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Vec3) return false
    return this.x.equals(other.x) && this.y.equals(other.y) && this.z.equals(other.z)
  }

  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    result = 31 * result + z.hashCode()
    return result
  }

  override fun toString(): String = "Vec3(x=$x, y=$y, z=$z)"

  public companion object {
    private const val DIMENSIONS = 3

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

  public class Unit(x: Double, y: Double, z: Double) : Vec3(x, y, z) {
    override fun norm(): Double = 1.0

    override fun normalize(): Unit = this

    override fun unaryMinus(): Unit = Unit(-x, -y, -z)

    public companion object {
      /**
       * Creates a unit vector from the given coordinates.
       *
       * @param x The x coordinate.
       * @param y The y coordinate.
       * @param z The z coordinate.
       * @return A new unit vector with the specified coordinates.
       * @throws ArithmeticException if the input vector is a zero vector.
       */
      public fun from(x: Double, y: Double, z: Double): Unit {
        val norm = sqrt(x * x + y * y + z * z)
        val inverseNorm = 1.0 / norm
        if (norm == 0.0) {
          throw ArithmeticException("Cannot create a unit vector from a zero vector")
        }
        return Unit(x * inverseNorm, y * inverseNorm, z * inverseNorm)
      }

      /**
       * Creates a unit vector from another vector.
       *
       * @param vector The vector to convert to a unit vector.
       * @return A new unit vector with the same direction as the input vector.
       * @throws ArithmeticException if the input vector is a zero vector.
       */
      public fun from(vector: Vec3): Unit =
        when (vector) {
          is Unit -> vector
          else -> from(vector.x, vector.y, vector.z)
        }
    }
  }
}
