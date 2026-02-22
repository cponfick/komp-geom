package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.Vector1

/**
 * A mutable one-dimensional vector with a double-precision component.
 *
 * @property x The x-component of the vector.
 */
public class MutableVec1(public override var x: Double) : Vector1<MutableVec1> {
  override fun withComponents(x: Double): MutableVec1 = this.apply { this.x = x }

  override fun plus(other: MutableVec1): MutableVec1 = this.apply { this.x += other.x }

  override fun times(scalar: Double): MutableVec1 = this.apply { this.x *= scalar }

  override fun unaryMinus(): MutableVec1 = this.apply { this.x = -this.x }

  override fun minus(other: MutableVec1): MutableVec1 = this.apply { this.x -= other.x }

  override fun zero(): MutableVec1 = MutableVec1(0.0)

  override fun toString(): String = "MutableVec1(x=$x)"

  public companion object {
    /**
     * Create a zero vector.
     *
     * @return A [MutableVec1] with the component set to zero.
     */
    public fun zero(): MutableVec1 = MutableVec1(0.0)

    /**
     * Create a mutable vector with the component set to positive infinity.
     *
     * @return A [MutableVec1] with the component set to positive infinity.
     */
    public fun positiveInfinity(): MutableVec1 = MutableVec1(Double.POSITIVE_INFINITY)

    /**
     * Create a mutable vector with the component set to negative infinity.
     *
     * @return A [MutableVec1] with the component set to negative infinity.
     */
    public fun negativeInfinity(): MutableVec1 = MutableVec1(Double.NEGATIVE_INFINITY)

    /**
     * Create a mutable vector with the component set to NaN.
     *
     * @return A [MutableVec1] with the component set to NaN.
     */
    public fun nan(): MutableVec1 = MutableVec1(Double.NaN)
  }
}
