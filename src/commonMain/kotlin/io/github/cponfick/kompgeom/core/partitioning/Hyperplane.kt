package io.github.cponfick.kompgeom.core.partitioning

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Point

/**
 * The base class representing a [hyperplane](https://en.wikipedia.org/wiki/Hyperplane) in a
 * geometric space.
 *
 * @param P The type of point that this hyperplane operates on, which must extend [Point].
 * @property precision The precision used for geometric computations, defaulting to
 *   [DEFAULT_DOUBLE_EQUIVALENCE].
 * @constructor Creates a new hyperplane with the specified precision.
 */
public abstract class Hyperplane<P : Point<P>>(
  public val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE
) {
  /**
   * Get the distance of a point with respect to this hyperplane. If the distance is zero, the point
   * is on the hyperplane.
   *
   * @param point The point to measure the distance from.
   * @return The oriented distance of the point from the hyperplane.
   */
  public abstract fun distance(point: P): Double

  /**
   * Get the offset of a point with respect to this hyperplane.
   *
   * The offset is a signed distance from the hyperplane, where positive values indicate the point
   * is on the positive side of the hyperplane, negative values indicate it is on the negative side,
   * and zero indicates it is on the hyperplane.
   *
   * @param point The point to measure the offset from.
   * @return The signed distance (offset) of the point from the hyperplane.
   */
  public abstract fun offset(point: P): Double

  /**
   * Get the location of a point with respect to this hyperplane.
   *
   * @param point The point to check the location of.
   * @return The location of the point relative to the hyperplane.
   */
  public fun location(point: P): Location {
    val distance = distance(point)
    val signum = precision.signum(distance)
    return when {
      signum > 0 -> Location.PLUS
      signum < 0 -> Location.MINUS
      else -> Location.ON
    }
  }

  /**
   * Check if the hyperplane contains a point.
   *
   * @param point The point to check.
   * @return True if the point is on the hyperplane, false otherwise.
   */
  public operator fun contains(point: P): Boolean = location(point) == Location.ON

  /**
   * Get a hyperplane that is the reverse of this one.
   *
   * @return A new hyperplane with the opposite orientation.
   */
  public abstract fun reverse(): Hyperplane<P>
}

/**
 * Represents the relative location of a point with respect to a hyperplane.
 * - `PLUS`: The point is on the positive side of the hyperplane.
 * - `MINUS`: The point is on the negative side of the hyperplane.
 * - `ON`: The point is exactly on the hyperplane.
 */
public enum class Location {
  PLUS,
  MINUS,
  ON,
}
