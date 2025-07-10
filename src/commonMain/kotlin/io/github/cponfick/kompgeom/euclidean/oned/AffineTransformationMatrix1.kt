package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.AffineTransformationMatrix
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero

/**
 * Affine transformation matrix for one-dimensional Euclidean vectors.
 *
 * @property m00 The scaling factor for the x-coordinate.
 * @property m01 The translation factor for the x-coordinate.
 */
public class AffineTransformationMatrix1(public val m00: Double, public val m01: Double) :
  AffineTransformationMatrix<Vec1, AffineTransformationMatrix1>() {
  override fun determinant(): Double = m00

  override fun eq(other: AffineTransformationMatrix1, equivalence: DoubleEquivalence): Boolean =
    equivalence.eq(m00, other.m00) && equivalence.eq(m01, other.m01)

  override fun apply(point: Vec1): Vec1 = Vec1(m00 * point.x + m01)

  override fun inverse(): Transformer<Vec1> {
    val det = determinant().assertIsFiniteAndNotZero()
    val inverseDet = 1.0 / det
    return AffineTransformationMatrix1(inverseDet, -(m01 * inverseDet))
  }

  /**
   * Converts the transformation matrix to an array representation. The array is structured in
   * row-major order.
   */
  public fun toArray(): DoubleArray {
    return doubleArrayOf(m00, m01)
  }

  override fun equals(other: Any?): Boolean {
    if (other !is AffineTransformationMatrix1) return false
    return m00 == other.m00 && m01 == other.m01
  }

  override fun hashCode(): Int = toArray().hashCode()

  override fun toString(): String {
    return "[$m00, $m01 | 0.0, 1.0]"
  }

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix1 = AffineTransformationMatrix1(1.0, 0.0)
  }
}
