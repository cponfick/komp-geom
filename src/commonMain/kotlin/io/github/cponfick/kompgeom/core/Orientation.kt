package io.github.cponfick.kompgeom.core

/**
 * Represents the orientation of three points in a 2D plane.
 *
 * The orientation can be:
 * - [COLLINEAR]: The points are collinear.
 * - [CLOCKWISE]: The points are oriented clockwise.
 * - [COUNTERCLOCKWISE]: The points are oriented counterclockwise.
 */
public enum class Orientation {
  /** The points are collinear. */
  COLLINEAR,

  /** The points are oriented clockwise. */
  CLOCKWISE,

  /** The points are oriented counterclockwise. */
  COUNTERCLOCKWISE,
}
