package io.github.cponfick.kompgeom.core

/**
 * Interface for objects that can compute the distance to another point.
 *
 * @param P The type of the point to which the distance is computed.
 */
public fun interface Distanceable<P> {
  /** Compute the distance to another point. */
  public infix fun distance(other: P): Double
}
