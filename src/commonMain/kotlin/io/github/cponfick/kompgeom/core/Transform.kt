package io.github.cponfick.kompgeom.core

/** Transform interface for geometric transformations. */
public interface Transformer<P> where P : Point<P> {

  /**
   * Applies the transformation to a point.
   *
   * @param point The point to transform.
   * @return The transformed point.
   */
  public fun apply(point: P): P

  /**
   * Get the inverse of this transformation.
   *
   * @return A new transformer that represents the inverse of this transformation.
   */
  public fun inverse(): Transformer<P>

  /**
   * Checks if the transformation preserves the orientation of points. For example: In 3D space, a
   * translation preserves orientation, while a reflection does not.
   *
   * @return True if the transformation preserves orientation, false otherwise.
   */
  public fun preserveOrientation(): Boolean
}
