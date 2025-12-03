package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DEGREES_TO_RADIANS
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.assertIsFiniteAndNotZero
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.github.cponfick.kompgeom.euclidean.internal.MatrixUtil
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil
import kotlin.math.cos
import kotlin.math.sin

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
public data class AffineTransformationMatrix2(
  public val m00: Double,
  public val m01: Double,
  public val m02: Double,
  public val m10: Double,
  public val m11: Double,
  public val m12: Double,
) : Transformer<Vec2> {

  /**
   * Get the array representation of the transformation matrix.
   *
   * @return A double array representing the matrix in row-major order.
   */
  public fun toArray(): DoubleArray = doubleArrayOf(m00, m01, m02, m10, m11, m12)

  /**
   * Computes the determinant of the affine transformation matrix.
   *
   * The determinant is calculated as: `det = m00 * m11 - m01 * m10`
   *
   * @return The determinant of the matrix.
   */
  public fun determinant(): Double = MatrixUtil.determinant(m00, m01, m10, m11)

  /**
   * Tests if two affine transformation matrices are approximately equal.
   *
   * @param other The other affine transformation matrix to compare with.
   * @param equivalence The equivalence used to compare the double values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the matrices are approximately equal, false otherwise.
   */
  public fun eq(
    other: AffineTransformationMatrix2,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean =
    equivalence.eq(m00, other.m00) &&
      equivalence.eq(m01, other.m01) &&
      equivalence.eq(m02, other.m02) &&
      equivalence.eq(m10, other.m10) &&
      equivalence.eq(m11, other.m11) &&
      equivalence.eq(m12, other.m12)

  override fun apply(obj: Vec2): Vec2 =
    Vec2(m00 * obj.x + m01 * obj.y + m02, m10 * obj.x + m11 * obj.y + m12)

  override fun inverse(): AffineTransformationMatrix2 {
    val invDet = 1.0 / determinant().assertIsFiniteAndNotZero()

    val c00 = invDet * m11
    val c01 = -invDet * m01
    val c02 = invDet * MatrixUtil.determinant(m01, m02, m11, m12)

    val c10 = -invDet * m10
    val c11 = invDet * m00
    val c12 = -invDet * MatrixUtil.determinant(m00, m02, m10, m12)

    return AffineTransformationMatrix2(c00, c01, c02, c10, c11, c12)
  }

  /**
   * Translates the transformation matrix by a given vector. The x and y components of the vector
   * represent the translation in the respective directions.
   *
   * @param translation The vector to translate the matrix.
   * @return A new affine transformation matrix with the translation applied.
   */
  public fun translate(translation: Vec2): AffineTransformationMatrix2 =
    translate(translation.x, translation.y)

  /**
   * Translates the transformation matrix by a given distance in the x and y directions.
   *
   * @param tx The translation in the x-direction.
   * @param ty The translation in the y-direction.
   * @return A new affine transformation matrix with the translation applied.
   */
  public fun translate(tx: Double, ty: Double): AffineTransformationMatrix2 =
    AffineTransformationMatrix2(m00, m01, m02 + tx, m10, m11, m12 + ty)

  /**
   * Scales the transformation matrix by a given factor in both x and y directions.
   *
   * @param factor The scaling factor to apply to both x and y dimensions.
   * @return A new affine transformation matrix with the scaling applied.
   */
  public fun scale(factor: Double): AffineTransformationMatrix2 = scale(factor, factor)

  /**
   * Scales the transformation matrix by a given vector. The x and y components of the vector
   * represent the scaling factors in the respective directions.
   *
   * @param vector The vector to scale the matrix.
   * @return A new affine transformation matrix with the scaling applied.
   */
  public fun scale(vector: Vec2): AffineTransformationMatrix2 = scale(vector.x, vector.y)

  /**
   * Scales the transformation matrix by given factors in the x and y directions.
   *
   * @param x The scaling factor for the x-direction.
   * @param y The scaling factor for the y-direction.
   * @return A new affine transformation matrix with the scaling applied.
   */
  public fun scale(x: Double, y: Double): AffineTransformationMatrix2 =
    AffineTransformationMatrix2(
      // spotless:off
      m00 * x, m01, m02,
      m10, m11 * y, m12
      // spotless:on
    )

  /**
   * Rotates the transformation matrix by a specified angle. The angle can be specified in either
   * radians or degrees.
   *
   * @param angle The angle to rotate the matrix.
   * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
   * @return A new affine transformation matrix with the rotation applied.
   */
  public fun rotate(
    angle: Double,
    angleUnit: AngleUnit = AngleUnit.RADIANS,
  ): AffineTransformationMatrix2 = this * createRotation(angle, angleUnit)

  /**
   * Shears the transformation matrix by specified factors in the x and y directions.
   *
   * @param shearX The shear factor in the x-direction.
   * @param shearY The shear factor in the y-direction.
   * @return A new affine transformation matrix with the shear applied.
   */
  public fun shear(shearX: Double, shearY: Double): AffineTransformationMatrix2 =
    this * createShear(shearX, shearY)

  /**
   * Multiplies two affine transformation matrices.
   *
   * @param other The other affine transformation matrix to multiply with.
   * @return The resulting affine transformation matrix after multiplication.
   */
  public operator fun times(other: AffineTransformationMatrix2): AffineTransformationMatrix2 {
    val c00 = VectorUtil.linearCombination(this.m00, other.m00, this.m01, other.m10)
    val c01 = VectorUtil.linearCombination(this.m00, other.m01, this.m01, other.m11)
    val c02 = VectorUtil.linearCombination(this.m00, other.m02, this.m01, other.m12) + this.m02

    val c10 = VectorUtil.linearCombination(this.m10, other.m00, this.m11, other.m10)
    val c11 = VectorUtil.linearCombination(this.m10, other.m01, this.m11, other.m11)
    val c12 = VectorUtil.linearCombination(this.m10, other.m02, this.m11, other.m12) + this.m12

    return AffineTransformationMatrix2(c00, c01, c02, c10, c11, c12)
  }

  override fun preserveOrientation(): Boolean = determinant() > 0.0

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix2 =
      AffineTransformationMatrix2(1.0, 0.0, 0.0, 0.0, 1.0, 0.0)

    /**
     * Creates a translation transformation matrix.
     *
     * @param translation The vector representing the translation in the x and y directions.
     * @return A new affine transformation matrix representing the translation.
     */
    public fun createTranslation(translation: Vec2): AffineTransformationMatrix2 =
      createTranslation(translation.x, translation.y)

    /**
     * Creates a translation transformation matrix.
     *
     * @param x The translation in the x-direction.
     * @param y The translation in the y-direction.
     * @return A new affine transformation matrix representing the translation.
     */
    public fun createTranslation(x: Double, y: Double): AffineTransformationMatrix2 =
      AffineTransformationMatrix2(
        // spotless:off
      1.0, 0.0, x,
      0.0, 1.0, y
      // spotless:on
      )

    /**
     * Creates a scaling transformation matrix.
     *
     * @param scaling The vector representing the scaling factors in the x and y directions.
     * @return A new affine transformation matrix representing the scaling.
     */
    public fun createScaling(scaling: Vec2): AffineTransformationMatrix2 =
      createScaling(scaling.x, scaling.y)

    public fun createScaling(x: Double, y: Double): AffineTransformationMatrix2 =
      AffineTransformationMatrix2(
        // spotless:off
        x, 0.0, 0.0,
        0.0, y, 0.0
        // spotless:on
      )

    /**
     * Creates a rotation transformation matrix.
     *
     * @param angle The angle to rotate the matrix.
     * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
     * @return A new affine transformation matrix representing the rotation.
     */
    public fun createRotation(
      angle: Double,
      angleUnit: AngleUnit = AngleUnit.RADIANS,
    ): AffineTransformationMatrix2 {
      val radians =
        when (angleUnit) {
          AngleUnit.RADIANS -> angle
          AngleUnit.DEGREES -> angle * DEGREES_TO_RADIANS
        }
      return AffineTransformationMatrix2(
        // spotless:off
        cos(radians), -sin(radians), 0.0,
        sin(radians), cos(radians), 0.0
        // spotless:on
      )
    }

    /**
     * Creates a shear transformation matrix.
     *
     * @param shearX The shear factor in the x-direction.
     * @param shearY The shear factor in the y-direction.
     * @return A new affine transformation matrix representing the shear.
     */
    public fun createShear(shearX: Double, shearY: Double): AffineTransformationMatrix2 =
      AffineTransformationMatrix2(
        // spotless:off
        1.0, shearX, 0.0,
        shearY, 1.0, 0.0
        // spotless:on
      )
  }
}
