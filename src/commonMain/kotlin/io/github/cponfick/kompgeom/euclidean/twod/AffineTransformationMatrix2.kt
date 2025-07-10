package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.AffineTransformationMatrix
import io.github.cponfick.kompgeom.euclidean.utils.MatrixUtil
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero

/**
 * Represents a 2D affine transformation matrix.
 *
 * @property m00 The element at row 0, column 0.
 * @property m01 The element at row 0, column 1.
 * @property m02 The element at row 0, column 2.
 * @property m10 The element at row 1, column 0.
 * @property m11 The element at row 1, column 1.
 * @property m12 The element at row 1, column 2.
 */
public class AffineTransformationMatrix2(
  public val m00: Double,
  public val m01: Double,
  public val m02: Double,
  public val m10: Double,
  public val m11: Double,
  public val m12: Double,
) : AffineTransformationMatrix<Vec2, AffineTransformationMatrix2>() {

  /**
   * Get the array representation of the transformation matrix.
   *
   * @return A double array representing the matrix in row-major order.
   */
  public fun toArray(): DoubleArray = doubleArrayOf(m00, m01, m02, m10, m11, m12)

  override fun equals(other: Any?): Boolean {
    if (other !is AffineTransformationMatrix2) return false
    return m00 == other.m00 &&
      m01 == other.m01 &&
      m02 == other.m02 &&
      m10 == other.m10 &&
      m11 == other.m11 &&
      m12 == other.m12
  }

  override fun hashCode(): Int = toArray().hashCode()

  override fun toString(): String {
    return "[$m00, $m01, $m02 | $m10, $m11, $m12]"
  }

  override fun determinant(): Double = MatrixUtil.determinant(m00, m01, m10, m11)

  override fun eq(other: AffineTransformationMatrix2, equivalence: DoubleEquivalence): Boolean =
    equivalence.eq(m00, other.m00) &&
      equivalence.eq(m01, other.m01) &&
      equivalence.eq(m02, other.m02) &&
      equivalence.eq(m10, other.m10) &&
      equivalence.eq(m11, other.m11) &&
      equivalence.eq(m12, other.m12)

  override fun apply(point: Vec2): Vec2 =
    Vec2(m00 * point.x + m01 * point.y + m02, m10 * point.x + m11 * point.y + m12)

  /**
   * Checks if the transformation preserves orientation. For more information, see:
   * [Wikipedia](https://en.wikipedia.org/wiki/Minor_(linear_algebra)#Inverse_of_a_matrix)
   */
  override fun inverse(): Transformer<Vec2> {
    val invDet = 1.0 / determinant().assertIsFiniteAndNotZero()

    val c00 = invDet * m11
    val c01 = -invDet * m01
    val c02 = invDet * MatrixUtil.determinant(m01, m02, m11, m12)

    val c10 = -invDet * m10
    val c11 = invDet * m00
    val c12 = -invDet * MatrixUtil.determinant(m00, m02, m10, m12)

    return AffineTransformationMatrix2(c00, c01, c02, c10, c11, c12)
  }

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix2 =
      AffineTransformationMatrix2(1.0, 0.0, 0.0, 0.0, 1.0, 0.0)
  }
}
