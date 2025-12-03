package io.github.cponfick.kompgeom.core.shapes

import io.github.cponfick.kompgeom.core.Distanceable
import io.github.cponfick.kompgeom.core.Vector

/**
 * Represents a line in a vector space.
 *
 * A line is defined by its distance and offset from points in the space, and it can be used to
 * determine the relative position of points with respect to the line.
 *
 * @param V The type of vector that defines the space in which the line exists.
 */
public interface Line<V : Vector<V>> : Distanceable<V> {

  /**
   * Get the offset of a point from this line.
   *
   * The offset is the signed distance from the point to the line, which can be positive, negative,
   * or zero depending on whether the point is on the positive side, negative side, or exactly on
   * the line.
   *
   * @param vec The point to measure the offset from.
   * @return The signed distance from the point to the line.
   */
  public fun offset(vec: V): Double

  /**
   * Determine the location of a point with respect to this line.
   *
   * The location can be:
   * - `Location.PLUS`: The point is on the positive side of the line.
   * - `Location.MINUS`: The point is on the negative side of the line.
   * - `Location.ON`: The point is exactly on the line.
   *
   * @param vec The point to check.
   * @return The location of the point with respect to the line.
   */
  public fun location(vec: V): Location

  /**
   * Check if a point is on this line.
   *
   * This is a convenience operator function that checks if the location of the point is
   * `Location.ON`.
   *
   * @param vec The point to check.
   * @return `true` if the point is on the line, `false` otherwise.
   */
  public operator fun contains(vec: V): Boolean = location(vec) == Location.ON

  /**
   * Reverse the direction of this line.
   *
   * This creates a new line that is the reverse of the current line, effectively flipping its
   * orientation.
   *
   * @return A new [Line] instance that is the reverse of this line.
   */
  public fun reverse(): Line<V>
}

/**
 * Represents the relative location of a point with respect to a line.
 * - `PLUS`: The point is on the positive side of the line.
 * - `MINUS`: The point is on the negative side of the line.
 * - `ON`: The point is exactly on the line.
 */
public enum class Location {
  PLUS,
  MINUS,
  ON,
}
