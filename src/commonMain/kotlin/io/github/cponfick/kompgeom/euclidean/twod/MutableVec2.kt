package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Vector2

public class MutableVec2(public override var x: Double, public override var y: Double) :
  Vector2<MutableVec2> {
  override fun withComponents(x: Double, y: Double): MutableVec2 =
    this.apply {
      this.x = x
      this.y = y
    }

  override fun plus(other: MutableVec2): MutableVec2 =
    this.apply {
      this.x += other.x
      this.y += other.y
    }

  override fun times(scalar: Double): MutableVec2 =
    this.apply {
      this.x *= scalar
      this.y *= scalar
    }

  override fun unaryMinus(): MutableVec2 =
    this.apply {
      this.x = -this.x
      this.y = -this.y
    }

  override fun minus(other: MutableVec2): MutableVec2 =
    this.apply {
      this.x -= other.x
      this.y -= other.y
    }

  override fun zero(): MutableVec2 = MutableVec2(0.0, 0.0)

  /**
   * Creates an immutable copy of this vector.
   *
   * @return An immutable [Vec2] with the same components as this vector.
   */
  public fun toVec2(): Vec2 = Vec2(x, y)

  /**
   * Creates a mutable copy of this vector.
   *
   * @return A mutable [MutableVec2] with the same components as this mutable vector.
   */
  public fun toMutableVec2(): MutableVec2 = MutableVec2(x, y)

  public override fun toString(): String = "MutableVec2(x=$x, y=$y)"

  public companion object {
    /**
     * Create a zero vector.
     *
     * @return A [MutableVec2] with both components set to zero.
     */
    public fun zero(): MutableVec2 = MutableVec2(0.0, 0.0)

    /**
     * Create a vector with all components set to positive infinity.
     *
     * @return A [MutableVec2] with both components set to positive infinity.
     */
    public fun positiveInfinity(): MutableVec2 =
      MutableVec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

    /**
     * Create a vector with all components set to negative infinity.
     *
     * @return A [MutableVec2] with both components set to negative infinity.
     */
    public fun negativeInfinity(): MutableVec2 =
      MutableVec2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

    /**
     * Create a vector with all components set to NaN.
     *
     * @return A [MutableVec2] with both components set to NaN.
     */
    public fun nan(): MutableVec2 = MutableVec2(Double.NaN, Double.NaN)
  }
}
