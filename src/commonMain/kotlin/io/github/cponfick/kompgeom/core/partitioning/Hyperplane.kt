package io.github.cponfick.kompgeom.core.partitioning

import io.github.cponfick.kompgeom.core.Distanceable

/**
 * The base class representing a [hyperplane](https://en.wikipedia.org/wiki/Hyperplane) in a
 * geometric space.
 *
 * @param O The type of object that this hyperplane operates on, which must extend [Distanceable].
 * @constructor Creates a new hyperplane with the specified precision.
 */
public interface Hyperplane<O> {
  /**
   * Get the distance of a point with respect to this hyperplane. If the distance is zero, the point
   * is on the hyperplane.
   *
   * @param obj The point to measure the distance from.
   * @return The oriented distance of the point from the hyperplane.
   */
  public fun distance(obj: O): Double

  /**
   * Get the offset of a point with respect to this hyperplane.
   *
   * The offset is a signed distance from the hyperplane, where positive values indicate the point
   * is on the positive side of the hyperplane, negative values indicate it is on the negative side,
   * and zero indicates it is on the hyperplane.
   *
   * @param obj The point to measure the offset from.
   * @return The signed distance (offset) of the point from the hyperplane.
   */
  public fun offset(obj: O): Double

  /**
   * Get the location of a point with respect to this hyperplane.
   *
   * @param obj The point to check the location of.
   * @return The location of the point relative to the hyperplane.
   */
  public fun location(obj: O): Location

  /**
   * Check if the hyperplane contains a point.
   *
   * @param obj The point to check.
   * @return True if the point is on the hyperplane, false otherwise.
   */
  public operator fun contains(obj: O): Boolean = location(obj) == Location.ON

  /**
   * Get a hyperplane that is the reverse of this one.
   *
   * @return A new hyperplane with the opposite orientation.
   */
  public abstract fun reverse(): Hyperplane<O>
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
