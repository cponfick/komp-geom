package io.github.cponfick.kompgeom.core

/** Interface representing a point in a multi-dimensional space. */
public interface Point<P : Point<P>> : Spatial {
  /** Compute the distance to another point. */
  public infix fun distance(other: P): Double
}
