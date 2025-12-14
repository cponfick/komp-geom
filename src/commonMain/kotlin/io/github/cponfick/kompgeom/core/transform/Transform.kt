package io.github.cponfick.kompgeom.core.transform

/**
 * Interface for geometric transformations.
 *
 * A transformation is a function that maps objects in a geometric space to other objects, possibly
 * changing their position, orientation, or scale. Examples include translations, rotations,
 * reflections, and scalings.
 *
 * @param O The type of the geometric object being transformed.
 */
public interface Transformer<O> {

  /**
   * Applies the transformation to a geometric object.
   *
   * @param T The concrete type of the object, which must be a subtype of O.
   * @param obj The geometric object to transform.
   * @return The transformed object of the same concrete type.
   */
  public fun <T : O> apply(obj: T): T

  /**
   * Get the inverse of this transformation.
   *
   * @return A new transformer that represents the inverse of this transformation.
   */
  public fun inverse(): Transformer<O>

  /**
   * Checks if the transformation preserves the orientation of points. For example: In 3D space, a
   * translation preserves orientation, while a reflection does not.
   *
   * @return True if the transformation preserves orientation, false otherwise.
   */
  public fun preserveOrientation(): Boolean
}
