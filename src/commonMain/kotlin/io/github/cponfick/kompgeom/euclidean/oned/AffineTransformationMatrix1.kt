package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.AffineTransformationMatrix
import io.github.cponfick.kompgeom.euclidean.assertIsFiniteAndNotZero

/**
 * Affine transformation matrix for one-dimensional Euclidean vectors.
 *
 * @property scalingFactor The scaling factor for the x-coordinate.
 * @property translationFactor The translation factor for the x-coordinate.
 */
public class AffineTransformationMatrix1(
    public val scalingFactor: Double,
    public val translationFactor: Double
) : AffineTransformationMatrix<Vec1, AffineTransformationMatrix1>() {
  override fun determinant(): Double = scalingFactor

  override fun apply(point: Vec1): Vec1 = Vec1(scalingFactor * point.x + translationFactor)

  override fun inverse(): Transformer<Vec1> {
    val det = determinant().assertIsFiniteAndNotZero()
    val inverseDet = 1.0 / det
    return AffineTransformationMatrix1(inverseDet, -(translationFactor * inverseDet))
  }

  /**
   * Converts the transformation matrix to an array representation. The array is structured in
   * row-major order.
   */
  public fun toArray(): DoubleArray {
    return doubleArrayOf(scalingFactor, translationFactor, 0.0, 1.0)
  }

  override fun equals(other: Any?): Boolean {
    if (other !is AffineTransformationMatrix1) return false
    return scalingFactor == other.scalingFactor && translationFactor == other.translationFactor
  }

  override fun hashCode(): Int {
    var result = scalingFactor.hashCode()
    result = 31 * result + translationFactor.hashCode()
    return result
  }

  override fun toString(): String {
    return "[$scalingFactor, $translationFactor | 0.0, 1.0]"
  }

  public companion object {
    /** Identity matrix for one-dimensional vectors. */
    public val IDENTITY: AffineTransformationMatrix1 = AffineTransformationMatrix1(1.0, 0.0)
  }
}
