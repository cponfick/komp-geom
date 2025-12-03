package io.github.cponfick.kompgeom.core

/**
 * Interface representing a spatial object in the KompGeom library.
 *
 * A spatial object can be a point, line, plane, or any other geometric entity that exists in a
 * multidimensional space.
 */
public interface Spatial {
  /**
   * Returns the number of dimensions of the spatial object.
   *
   * @return the number of dimensions, e.g., 1 for a line, 2 for a plane, 3 for space.
   */
  public fun dimensions(): Int

  /**
   * Returns true if all values in the spatial are finite.
   *
   * @return true if all values are finite, false if any value is infinite or NaN.
   */
  public fun isFinite(): Boolean

  /**
   * Returns true if any value in the spatial is infinite.
   *
   * @return true if any value is infinite, false if all values are finite or NaN.
   */
  public fun isInfinite(): Boolean

  /**
   * Returns true if any value in the spatial is NaN (Not a Number).
   *
   * @return true if any value is NaN, false if all values are finite or infinite.
   */
  public fun isNaN(): Boolean
}
