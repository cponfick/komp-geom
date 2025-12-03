package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.Distanceable
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Spatial
import io.github.cponfick.kompgeom.core.Vector
import io.github.cponfick.kompgeom.core.assertIsFiniteAndNotZero
import kotlin.math.PI
import kotlin.math.absoluteValue

/**
 * This data class represents one-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 */
public data class Vec1(public val x: Double) : Spatial, Distanceable<Vec1>, Vector<Vec1> {
  override fun dimensions(): Int = 1

  override fun isFinite(): Boolean = x.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite()

  override fun isNaN(): Boolean = x.isNaN()

  /**
   * Checks if two vectors are equal within a specified tolerance.
   *
   * @param other The other vector to compare with.
   * @param equivalence The tolerance used for comparison.
   * @return True if the vectors are equal within the tolerance, false otherwise.
   */
  public fun eq(other: Vec1, equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE): Boolean =
    equivalence.eq(this.x, other.x)

  /**
   * Performs linear interpolation between two vectors.
   *
   * @param other The target vector for interpolation.
   * @param t The interpolation parameter (0.0 returns this vector, 1.0 returns other vector).
   * @return The interpolated vector.
   */
  public fun lerp(other: Vec1, t: Double): Vec1 = Vec1(x + (other.x - x) * t)

  override fun distance(other: Vec1): Double = (this.x - other.x).absoluteValue

  override operator fun plus(other: Vec1): Vec1 = Vec1(this.x + other.x)

  override fun angle(other: Vec1, angleUnit: AngleUnit): Double {
    this.x.assertIsFiniteAndNotZero()
    other.x.assertIsFiniteAndNotZero()
    return when (angleUnit) {
      AngleUnit.RADIANS -> if (this.x == other.x) 0.0 else PI
      AngleUnit.DEGREES -> if (this.x == other.x) 0.0 else 180.0
    }
  }

  override infix fun dot(other: Vec1): Double = this.x * other.x

  override operator fun times(scalar: Double): Vec1 = Vec1(this.x * scalar)

  override operator fun unaryMinus(): Vec1 = Vec1(-x)

  override fun normalize(): Vec1 {
    if (x == 0.0) {
      throw ArithmeticException("Cannot normalize a vector with zero length.")
    }
    return Vec1(x / x.absoluteValue)
  }

  override fun norm(): Double = x.absoluteValue

  /**
   * Calculates the squared norm of the vector.
   *
   * @return The squared norm of the vector.
   */
  public fun normSquared(): Double = x * x

  override operator fun minus(other: Vec1): Vec1 = Vec1(this.x - other.x)

  override fun zero(): Vec1 = ZERO

  /**
   * Checks if the vector is a zero vector within a specified tolerance.
   *
   * @param equivalence The tolerance used for comparison.
   * @return True if the vector is zero within the tolerance, false otherwise.
   */
  public fun isZero(equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE): Boolean =
    eq(ZERO, equivalence)

  public companion object {
    /** The zero vector. */
    public val ZERO: Vec1 = Vec1(0.0)
    /** Vector with all components set to positive infinity. */
    public val POSITIVE_INFINITY: Vec1 = Vec1(Double.POSITIVE_INFINITY)
    /** Vector with all components set to negative infinity. */
    public val NEGATIVE_INFINITY: Vec1 = Vec1(Double.NEGATIVE_INFINITY)
    /** Vector with all components set to NaN. */
    public val NaN: Vec1 = Vec1(Double.NaN)
  }
}
