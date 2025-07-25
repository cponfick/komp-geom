package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.partitioning.Hyperplane
import io.github.cponfick.kompgeom.core.partitioning.Location
import kotlin.math.abs

/**
 * Represents a line in 2D space defined by a direction vector and an offset from the origin.
 *
 * @property direction The direction of the line, which must be a unit vector.
 * @property originOffSet The offset of the line from the origin.
 * @property precision The precision used for geometric computations, defaulting to
 *   [DEFAULT_DOUBLE_EQUIVALENCE].
 */
public data class Line2(
  public val direction: Vec2,
  public val originOffSet: Double,
  public val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Hyperplane<Vec2> {
  init {
    require(precision.compare(1.0, direction.norm()) == 0) {
      "Direction vector must be a unit vector."
    }
  }

  override fun distance(obj: Vec2): Double = abs(offset(obj))

  override fun offset(obj: Vec2): Double = originOffSet - direction.signedArea(obj)

  override fun reverse(): Line2 = Line2(-direction, -originOffSet, precision)

  override fun location(obj: Vec2): Location {
    val offset = offset(obj)
    val signum = precision.signum(offset)
    return when {
      signum > 0 -> Location.PLUS
      signum < 0 -> Location.MINUS
      else -> Location.ON
    }
  }

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
      require(!(direction.eq(Vec2.ZERO, precision))) { "Direction vector cannot be zero." }

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
