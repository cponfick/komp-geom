package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.euclidean.EuclideanVector
import io.github.cponfick.kompgeom.euclidean.assertIsFiniteAndNotZero
import kotlin.math.PI
import kotlin.math.absoluteValue

/**
 * This class represents one-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 */
public class Vec1(public val x: Double = 0.0) : EuclideanVector<Vec1>() {
  /**
   * Creates a new vector by copying the coordinates from another vector.
   *
   * @param vector The vector to copy.
   */
  public constructor(vector: Vec1) : this(vector.x)

  override fun eq(other: Vec1, equivalence: DoubleEquivalence): Boolean =
      equivalence.eq(this.x, other.x)

  override fun lerp(other: Vec1, t: Double): Vec1 {
    return Vec1(x + (other.x - x) * t)
  }

  override fun distance(other: Vec1): Double = (this.x - other.x).absoluteValue

  override fun dimensions(): Int = DIMENSIONS

  override fun isFinite(): Boolean = x.isFinite()

  override fun isInfinite(): Boolean = x.isInfinite()

  override fun isNaN(): Boolean = x.isNaN()

  override fun plus(other: Vec1): Vec1 = Vec1(this.x + other.x)

  override fun angle(other: Vec1, angleUnit: AngleUnit): Double {
    this.x.assertIsFiniteAndNotZero()
    other.x.assertIsFiniteAndNotZero()
    return when (angleUnit) {
      AngleUnit.RADIANS -> if (this.x == other.x) 0.0 else PI
      AngleUnit.DEGREES -> if (this.x == other.x) 0.0 else 180.0
    }
  }

  override fun dot(other: Vec1): Double = this.x * other.x

  override fun times(scalar: Double): Vec1 = Vec1(this.x * scalar)

  override fun unaryMinus(): Vec1 = Vec1(-x)

  override fun normalize(): Vec1 {
    if (x == 0.0) {
      throw ArithmeticException("Cannot normalize a vector with zero length.")
    }
    return Vec1(x / x.absoluteValue)
  }

  override fun norm(): Double = x * x

  override fun minus(other: Vec1): Vec1 {
    return Vec1(this.x - other.x)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Vec1) return false
    return this.x.equals(other.x)
  }

  override fun hashCode(): Int {
    return x.hashCode()
  }

  override fun toString(): String {
    return "Vec1(x=$x)"
  }

  public companion object {
    private const val DIMENSIONS = 1
  }
}

/**
 * Multiplies a scalar by a Vec1 vector.
 *
 * @param vector The Vec1 vector to multiply.
 * @return A new Vec1 vector that is the result of the multiplication.
 */
public operator fun Double.times(vector: Vec1): Vec1 {
  return Vec1(this * vector.x)
}
