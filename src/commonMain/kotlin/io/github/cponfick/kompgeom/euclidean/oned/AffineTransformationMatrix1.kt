package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.utils.assertIsFiniteAndNotZero

/**
 * Affine transformation matrix for one-dimensional Euclidean vectors.
 *
 * @property m00 The scaling factor for the x-coordinate.
 * @property m01 The translation factor for the x-coordinate.
 */
public data class AffineTransformationMatrix1(public val m00: Double, public val m01: Double) :
  Transformer<Vec1> {

  /**
   * Computes the determinant of the affine transformation matrix. In one-dimensional space, the
   * determinant is simply the scaling factor (m00).
   *
   * @return The determinant of the matrix.
   */
  public fun determinant(): Double = m00

  /**
   * Tests if two affine transformation matrices are approximately equal.
   *
   * @param other The other affine transformation matrix to compare with.
   * @param equivalence The equivalence used to compare the double values.
   * @return True if the matrices are approximately equal, false otherwise.
   */
  public fun eq(
    other: AffineTransformationMatrix1,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean = equivalence.eq(m00, other.m00) && equivalence.eq(m01, other.m01)

  override fun apply(obj: Vec1): Vec1 = Vec1(m00 * obj.x + m01)

  override fun inverse(): AffineTransformationMatrix1 {
    val det = determinant().assertIsFiniteAndNotZero()
    val inverseDet = 1.0 / det
    return AffineTransformationMatrix1(inverseDet, -(m01 * inverseDet))
  }

  override fun preserveOrientation(): Boolean = determinant() > 0.0

  /**
   * Multiplies two affine transformation matrices. In one-dimensional space, this is a simple
   * multiplication of the scaling factors and an addition of the translation factors.
   *
   * @param other The other affine transformation matrix to multiply with.
   * @return The resulting affine transformation matrix.
   */
  public operator fun times(other: AffineTransformationMatrix1): AffineTransformationMatrix1 =
    AffineTransformationMatrix1(m00 = this.m00 * other.m00, m01 = this.m00 * other.m01 + this.m01)

  /**
   * Converts the transformation matrix to an array representation. The array is structured in
   * row-major order.
   *
   * @return A double array representing the transformation matrix.
   */
  public fun toArray(): DoubleArray = doubleArrayOf(m00, m01)

  override fun toString(): String = "[$m00, $m01 | 0.0, 1.0]"

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix1 = AffineTransformationMatrix1(1.0, 0.0)

    /**
     * Creates a scaling transformation matrix for one-dimensional space. The matrix scales the
     * x-coordinate by the given factor.
     *
     * @param factor The scaling factor.
     * @return An affine transformation matrix that scales the x-coordinate by the factor.
     */
    public fun scaling1D(factor: Double): AffineTransformationMatrix1 =
      AffineTransformationMatrix1(factor, 0.0)

    /**
     * Creates a translation transformation matrix for one-dimensional space. The matrix translates
     * the x-coordinate by the given offset.
     *
     * @param offset The translation offset.
     * @return An affine transformation matrix that translates the x-coordinate by the offset.
     */
    public fun translation1D(offset: Double): AffineTransformationMatrix1 =
      AffineTransformationMatrix1(1.0, offset)

    /**
     * Creates a reflection transformation matrix for one-dimensional space. The matrix reflects the
     * x-coordinate across the origin.
     *
     * @return An affine transformation matrix that reflects the x-coordinate.
     */
    public fun reflection1D(): AffineTransformationMatrix1 = AffineTransformationMatrix1(-1.0, 0.0)
  }
}
