package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.Vector1

/**
 * This data class represents one-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 */
public data class Vec1(public override val x: Double) : Vector1<Vec1> {

  override operator fun plus(other: Vec1): Vec1 = Vec1(this.x + other.x)

  override operator fun times(scalar: Double): Vec1 = Vec1(this.x * scalar)

  override operator fun unaryMinus(): Vec1 = Vec1(-x)

  override operator fun minus(other: Vec1): Vec1 = Vec1(this.x - other.x)

  override fun zero(): Vec1 = ZERO

  override fun withComponents(x: Double): Vec1 = Vec1(x)

  /**
   * Creates an immutable copy of this vector.
   *
   * @return An immutable [Vec1] with the same component as this vector.
   */
  public fun toVec1(): Vec1 = Vec1(x)

  /**
   * Creates a mutable copy of this vector.
   *
   * @return A mutable [MutableVec1] with the same component as this mutable vector.
   */
  public fun toMutableVec1(): MutableVec1 = MutableVec1(x)

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
