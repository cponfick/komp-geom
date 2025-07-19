package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.partitioning.Hyperplane
import kotlin.math.abs

public class Line2(
  public val direction: Vec2.Unit,
  public val originOffSet: Double,
  precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Hyperplane<Vec2>(precision) {

  override fun distance(point: Vec2): Double = abs(offset(point))

  override fun offset(point: Vec2): Double = originOffSet - direction.signedArea(point)

  override fun reverse(): Hyperplane<Vec2> = Line2(-direction, -originOffSet, precision)

  override fun hashCode(): Int {
    var result = direction.hashCode()
    result = 31 * result + originOffSet.hashCode()
    result = 31 * result + precision.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Line2) return false

    return direction == other.direction &&
      originOffSet == other.originOffSet &&
      precision == other.precision
  }

  override fun toString(): String = "Line2(direction=$direction, originOffSet=$originOffSet)"

  public companion object {
    /**
     * Creates a line from a point and a direction vector.
     *
     * @param point The point through which the line passes.
     * @param direction The direction vector of the line.
     * @param precision The precision used for geometric computations, defaulting to
     *   [DEFAULT_DOUBLE_EQUIVALENCE].
     * @return A new [Line2] instance representing the line.
     * @throws IllegalArgumentException if the direction vector is zero.
     */
    public fun fromPointAndDirection(
      point: Vec2,
      direction: Vec2,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Line2 {
      if (direction.isZero(precision)) {
        throw IllegalArgumentException("Direction vector cannot be zero.")
      }

      val unitDirection = direction.normalize()
      val originOffset = unitDirection.signedArea(point)

      return Line2(unitDirection, originOffset, precision)
    }

    /**
     * Creates a line from two points.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @param precision The precision used for geometric computations, defaulting to
     *   [DEFAULT_DOUBLE_EQUIVALENCE].
     * @return A new [Line2] instance representing the line through the two points.
     * @throws IllegalArgumentException if the two points are the same (resulting in a zero
     *   direction vector).
     */
    public fun fromPoints(
      p1: Vec2,
      p2: Vec2,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Line2 = fromPointAndDirection(p1, p2 - p1, precision)
  }
}
