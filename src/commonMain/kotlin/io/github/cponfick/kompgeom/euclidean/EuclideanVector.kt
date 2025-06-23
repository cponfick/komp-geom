package io.github.cponfick.kompgeom.euclidean

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Point
import io.github.cponfick.kompgeom.core.Spatial
import io.github.cponfick.kompgeom.core.Vector

public abstract class EuclideanVector<V : EuclideanVector<V>> : Point<V>, Spatial, Vector<V> {
  /**
   * Calculate the equality of this vector to another vector using the provided equivalence.
   *
   * @param other The other vector to compare with.
   * @param equivalence The equivalence to use for comparison (default is
   *   [DEFAULT_DOUBLE_EQUIVALENCE]).
   */
  public abstract fun eq(
    other: V,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean

  /**
   * Calculate a vector by interpolating between this vector and another vector.
   *
   * @param other The other vector to interpolate with.
   * @param t The interpolation factor, where 0.0 is this vector and 1.0 is the other vector.
   */
  public abstract fun lerp(other: V, t: Double): V
}
