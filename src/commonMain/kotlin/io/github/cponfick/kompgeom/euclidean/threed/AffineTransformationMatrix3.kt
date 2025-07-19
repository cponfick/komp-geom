package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.utils.MatrixUtil
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero

/**
 * Represents a 3D affine transformation matrix.
 *
 * @property m00 The element at row 0, column 0.
 * @property m01 The element at row 0, column 1.
 * @property m02 The element at row 0, column 2.
 * @property m03 The element at row 0, column 3.
 * @property m10 The element at row 1, column 0.
 * @property m11 The element at row 1, column 1.
 * @property m12 The element at row 1, column 2.
 * @property m13 The element at row 1, column 3.
 * @property m20 The element at row 2, column 0.
 * @property m21 The element at row 2, column 1.
 * @property m22 The element at row 2, column 2.
 * @property m23 The element at row 2, column 3.
 */
public data class AffineTransformationMatrix3(
  // spotless:off
  public val m00: Double, public val m01: Double, public val m02: Double, public val m03: Double,
  public val m10: Double, public val m11: Double, public val m12: Double, public val m13: Double,
  public val m20: Double, public val m21: Double, public val m22: Double, public val m23: Double,
  // spotless:on
) : Transformer<Vec3> {

  /**
   * Get the array representation of the transformation matrix.
   *
   * @return A double array representing the matrix in row-major order.
   */
  public fun toArray(): DoubleArray =
    doubleArrayOf(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23)

  /**
   * Returns a string representation of the transformation matrix.
   *
   * @return The determinant of the transformation matrix.
   */
  public fun determinant(): Double =
    MatrixUtil.determinant(m00, m01, m02, m10, m11, m12, m20, m21, m22)

  /**
   * Tests if two affine transformation matrices are approximately equal.
   *
   * @param other The other affine transformation matrix to compare with.
   * @param equivalence The equivalence used to compare the double values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   */
  public fun eq(
    other: AffineTransformationMatrix3,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean =
    equivalence.eq(m00, other.m00) &&
      equivalence.eq(m01, other.m01) &&
      equivalence.eq(m02, other.m02) &&
      equivalence.eq(m03, other.m03) &&
      equivalence.eq(m10, other.m10) &&
      equivalence.eq(m11, other.m11) &&
      equivalence.eq(m12, other.m12) &&
      equivalence.eq(m13, other.m13) &&
      equivalence.eq(m20, other.m20) &&
      equivalence.eq(m21, other.m21) &&
      equivalence.eq(m22, other.m22) &&
      equivalence.eq(m23, other.m23)

  override fun apply(obj: Vec3): Vec3 =
    Vec3(
      m00 * obj.x + m01 * obj.y + m02 * obj.z + m03,
      m10 * obj.x + m11 * obj.y + m12 * obj.z + m13,
      m20 * obj.x + m21 * obj.y + m22 * obj.z + m23,
    )

  override fun inverse(): AffineTransformationMatrix3 {
    val invDet = 1.0 / determinant().assertIsFiniteAndNotZero()

    val c00 = invDet * MatrixUtil.determinant(m11, m12, m21, m22)
    val c01 = -invDet * MatrixUtil.determinant(m01, m02, m21, m22)
    val c02 = invDet * MatrixUtil.determinant(m01, m02, m11, m12)
    val c03 = -invDet * MatrixUtil.determinant(m01, m02, m03, m11, m12, m13, m21, m22, m23)

    val c10 = -invDet * MatrixUtil.determinant(m10, m12, m20, m22)
    val c11 = invDet * MatrixUtil.determinant(m00, m02, m20, m22)
    val c12 = -invDet * MatrixUtil.determinant(m00, m02, m10, m12)
    val c13 = invDet * MatrixUtil.determinant(m00, m02, m03, m10, m12, m13, m20, m22, m23)

    val c20 = invDet * MatrixUtil.determinant(m10, m11, m20, m21)
    val c21 = -invDet * MatrixUtil.determinant(m00, m01, m20, m21)
    val c22 = invDet * MatrixUtil.determinant(m00, m01, m10, m11)
    val c23 = -invDet * MatrixUtil.determinant(m00, m01, m03, m10, m11, m13, m20, m21, m23)

    return AffineTransformationMatrix3(
      // spotless:off
      c00, c01, c02, c03,
      c10, c11, c12, c13,
      c20, c21, c22, c23
      // spotless:on
    )
  }

  override fun preserveOrientation(): Boolean = determinant() > 0.0

  public companion object {
    /** The identity transformation matrix. */
    public val IDENTITY: AffineTransformationMatrix3 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
  }
}
