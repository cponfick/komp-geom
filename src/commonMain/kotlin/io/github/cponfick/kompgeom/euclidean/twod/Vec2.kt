package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Vector2

/**
 * This class represents two-dimensional vectors and points in a Euclidean space.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 */
public data class Vec2(public override val x: Double, public override val y: Double) :
  Vector2<Vec2> {

  override operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  override operator fun times(scalar: Double): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  override operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

  override operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  override fun zero(): Vec2 = ZERO

  override fun withComponents(x: Double, y: Double): Vec2 = Vec2(x, y)

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
