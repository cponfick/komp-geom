package io.github.cponfick.kompgeom.euclidean

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Transformer

public abstract class AffineTransformationMatrix<
  V : EuclideanVector<V>,
  M : AffineTransformationMatrix<V, M>,
> : Transformer<V> {

  /**
   * Get the determinant of the underlying transformation matrix.
   *
   * @return The determinant of the transformation matrix.
   */
  public abstract fun determinant(): Double

  public abstract fun eq(
    other: M,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean

  override fun preserveOrientation(): Boolean = determinant() > 0.0
}
