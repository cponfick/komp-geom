package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.*
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.github.cponfick.kompgeom.euclidean.internal.MatrixUtil
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil
import kotlin.math.cos
import kotlin.math.sin

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
) : Transformer<Vector3<*>> {

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

  override fun <T : Vector3<*>> apply(obj: T): T {
    @Suppress("UNCHECKED_CAST")
    return obj.withComponents(
      m00 * obj.x + m01 * obj.y + m02 * obj.z + m03,
      m10 * obj.x + m11 * obj.y + m12 * obj.z + m13,
      m20 * obj.x + m21 * obj.y + m22 * obj.z + m23,
    ) as T
  }

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

  /**
   * Translates the transformation matrix by the specified x, y, and z values.
   *
   * @param x The translation along the x-axis.
   * @param y The translation along the y-axis.
   * @param z The translation along the z-axis.
   * @return A new [AffineTransformationMatrix3] that represents the translation.
   */
  public fun translate(x: Double, y: Double, z: Double): AffineTransformationMatrix3 =
    AffineTransformationMatrix3(
      // spotless:off
      m00, m01, m02, m03 + x,
      m10, m11, m12, m13 + y,
      m20, m21, m22, m23 + z
      // spotless:on
    )

  /**
   * Translates the transformation matrix by the specified translation vector.
   *
   * @param translation The translation vector containing x, y, and z components.
   * @return A new [AffineTransformationMatrix3] that represents the translation.
   */
  public fun translate(translation: Vec3): AffineTransformationMatrix3 =
    translate(translation.x, translation.y, translation.z)

  public fun scale(x: Double, y: Double, z: Double): AffineTransformationMatrix3 =
    AffineTransformationMatrix3(
      // spotless:off
      m00 * x, m01 * x, m02 * x, m03 * x,
      m10 * y, m11 * y, m12 * y, m13 * y,
      m20 * z, m21 * z, m22 * z, m23 * z
      // spotless:on
    )

  /**
   * Scales the transformation matrix by the specified scale vector.
   *
   * @param scale The scale vector containing x, y, and z components.
   * @return A new [AffineTransformationMatrix3] that represents the scaling.
   */
  public fun scale(scale: Vec3): AffineTransformationMatrix3 = scale(scale.x, scale.y, scale.z)

  /**
   * Scales the transformation matrix by the specified factor.
   *
   * @param factor The scaling factor to apply uniformly along all axes.
   * @return A new [AffineTransformationMatrix3] that represents the scaling.
   */
  public fun scale(factor: Double): AffineTransformationMatrix3 = scale(factor, factor, factor)

  /**
   * Rotates the transformation matrix around the X-axis by a specified angle.
   *
   * @param angle The angle to rotate around the X-axis.
   * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
   * @return A new affine transformation matrix with the rotation applied.
   */
  public fun rotateX(
    angle: Double,
    angleUnit: AngleUnit = AngleUnit.RADIANS,
  ): AffineTransformationMatrix3 = this * createRotationX(angle, angleUnit)

  /**
   * Rotates the transformation matrix around the Y-axis by a specified angle.
   *
   * @param angle The angle to rotate around the Y-axis.
   * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
   * @return A new affine transformation matrix with the rotation applied.
   */
  public fun rotateY(
    angle: Double,
    angleUnit: AngleUnit = AngleUnit.RADIANS,
  ): AffineTransformationMatrix3 = this * createRotationY(angle, angleUnit)

  /**
   * Rotates the transformation matrix around the Z-axis by a specified angle.
   *
   * @param angle The angle to rotate around the Z-axis.
   * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
   * @return A new affine transformation matrix with the rotation applied.
   */
  public fun rotateZ(
    angle: Double,
    angleUnit: AngleUnit = AngleUnit.RADIANS,
  ): AffineTransformationMatrix3 = this * createRotationZ(angle, angleUnit)

  /**
   * Applies a combined rotation around multiple axes using the specified rotation sequence.
   *
   * @param firstAngle The first rotation angle.
   * @param secondAngle The second rotation angle.
   * @param thirdAngle The third rotation angle.
   * @param sequence The rotation sequence to use (e.g., ZYX, XYZ).
   * @param angleUnit The unit of the angles, defaults to [AngleUnit.RADIANS].
   * @return A new affine transformation matrix with the combined rotation applied.
   */
  public fun rotate(
    firstAngle: Double,
    secondAngle: Double,
    thirdAngle: Double,
    sequence: RotationSequence,
    angleUnit: AngleUnit = AngleUnit.RADIANS,
  ): AffineTransformationMatrix3 =
    this * createRotation(firstAngle, secondAngle, thirdAngle, sequence, angleUnit)

  /**
   * Multiplies this affine transformation matrix by another affine transformation matrix.
   *
   * @param other The other affine transformation matrix to multiply with.
   * @return A new [AffineTransformationMatrix3] that is the result of the multiplication.
   */
  public operator fun times(other: AffineTransformationMatrix3): AffineTransformationMatrix3 {
    val c00 =
      VectorUtil.linearCombination(this.m00, other.m00, this.m01, other.m10, this.m02, other.m20)
    val c01 =
      VectorUtil.linearCombination(this.m00, other.m01, this.m01, other.m11, this.m02, other.m21)
    val c02 =
      VectorUtil.linearCombination(this.m00, other.m02, this.m01, other.m12, this.m02, other.m22)
    val c03 =
      VectorUtil.linearCombination(this.m00, other.m03, this.m01, other.m13, this.m02, other.m23) +
        this.m03

    val c10 =
      VectorUtil.linearCombination(this.m10, other.m00, this.m11, other.m10, this.m12, other.m20)
    val c11 =
      VectorUtil.linearCombination(this.m10, other.m01, this.m11, other.m11, this.m12, other.m21)
    val c12 =
      VectorUtil.linearCombination(this.m10, other.m02, this.m11, other.m12, this.m12, other.m22)
    val c13 =
      VectorUtil.linearCombination(this.m10, other.m03, this.m11, other.m13, this.m12, other.m23) +
        this.m13

    val c20 =
      VectorUtil.linearCombination(this.m20, other.m00, this.m21, other.m10, this.m22, other.m20)
    val c21 =
      VectorUtil.linearCombination(this.m20, other.m01, this.m21, other.m11, this.m22, other.m21)
    val c22 =
      VectorUtil.linearCombination(this.m20, other.m02, this.m21, other.m12, this.m22, other.m22)
    val c23 =
      VectorUtil.linearCombination(this.m20, other.m03, this.m21, other.m13, this.m22, other.m23) +
        this.m23

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

    /**
     * Creates a translation transformation matrix.
     *
     * @param x The translation along the x-axis.
     * @param y The translation along the y-axis.
     * @param z The translation along the z-axis.
     * @return A new [AffineTransformationMatrix3] representing the translation.
     */
    public fun createTranslation(x: Double, y: Double, z: Double): AffineTransformationMatrix3 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, x,
        0.0, 1.0, 0.0, y,
        0.0, 0.0, 1.0, z
        // spotless:on
      )

    /**
     * Creates a translation transformation matrix from a vector.
     *
     * @param translation The translation vector containing x, y, and z components.
     * @return A new [AffineTransformationMatrix3] representing the translation.
     */
    public fun createTranslation(translation: Vec3): AffineTransformationMatrix3 =
      createTranslation(translation.x, translation.y, translation.z)

    /**
     * Creates a scaling transformation matrix.
     *
     * @param x The scaling factor along the x-axis.
     * @param y The scaling factor along the y-axis.
     * @param z The scaling factor along the z-axis.
     * @return A new [AffineTransformationMatrix3] representing the scaling.
     */
    public fun createScaling(x: Double, y: Double, z: Double): AffineTransformationMatrix3 =
      AffineTransformationMatrix3(
        // spotless:off
        x, 0.0, 0.0, 0.0,
        0.0, y, 0.0, 0.0,
        0.0, 0.0, z, 0.0
        // spotless:on
      )

    /**
     * Creates a scaling transformation matrix from a vector.
     *
     * @param scale The scale vector containing x, y, and z components.
     * @return A new [AffineTransformationMatrix3] representing the scaling.
     */
    public fun createScaling(scale: Vec3): AffineTransformationMatrix3 =
      createScaling(scale.x, scale.y, scale.z)

    /**
     * Creates a uniform scaling transformation matrix.
     *
     * @param factor The scaling factor to apply uniformly along all axes.
     * @return A new [AffineTransformationMatrix3] representing the uniform scaling.
     */
    public fun createScaling(factor: Double): AffineTransformationMatrix3 =
      createScaling(factor, factor, factor)

    /**
     * Creates a rotation transformation matrix around the X-axis.
     *
     * @param angle The angle to rotate around the X-axis.
     * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
     * @return A new [AffineTransformationMatrix3] representing the rotation.
     */
    public fun createRotationX(
      angle: Double,
      angleUnit: AngleUnit = AngleUnit.RADIANS,
    ): AffineTransformationMatrix3 {
      val rad =
        when (angleUnit) {
          AngleUnit.DEGREES -> angle * DEGREES_TO_RADIANS
          AngleUnit.RADIANS -> angle
        }

      val c = cos(rad)
      val s = sin(rad)

      return AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, c, -s, 0.0,
        0.0, s, c, 0.0
        // spotless:on
      )
    }

    /**
     * Creates a rotation transformation matrix around the Y-axis.
     *
     * @param angle The angle to rotate around the Y-axis.
     * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
     * @return A new [AffineTransformationMatrix3] representing the rotation.
     */
    public fun createRotationY(
      angle: Double,
      angleUnit: AngleUnit = AngleUnit.RADIANS,
    ): AffineTransformationMatrix3 {
      val rad =
        when (angleUnit) {
          AngleUnit.DEGREES -> angle * DEGREES_TO_RADIANS
          AngleUnit.RADIANS -> angle
        }

      val c = cos(rad)
      val s = sin(rad)

      return AffineTransformationMatrix3(
        // spotless:off
        c, 0.0, s, 0.0,
        0.0, 1.0, 0.0, 0.0,
        -s, 0.0, c, 0.0
        // spotless:on
      )
    }

    /**
     * Creates a rotation transformation matrix around the Z-axis.
     *
     * @param angle The angle to rotate around the Z-axis.
     * @param angleUnit The unit of the angle, defaults to [AngleUnit.RADIANS].
     * @return A new [AffineTransformationMatrix3] representing the rotation.
     */
    public fun createRotationZ(
      angle: Double,
      angleUnit: AngleUnit = AngleUnit.RADIANS,
    ): AffineTransformationMatrix3 {
      val rad =
        when (angleUnit) {
          AngleUnit.DEGREES -> angle * DEGREES_TO_RADIANS
          AngleUnit.RADIANS -> angle
        }

      val c = cos(rad)
      val s = sin(rad)

      return AffineTransformationMatrix3(
        // spotless:off
        c, -s, 0.0, 0.0,
        s, c, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    }

    /**
     * Creates a combined rotation transformation matrix using the specified rotation sequence. This
     * is more efficient than multiplying individual rotation matrices as it computes the final
     * transformation matrix directly using trigonometric identities.
     *
     * @param firstAngle The first rotation angle.
     * @param secondAngle The second rotation angle.
     * @param thirdAngle The third rotation angle.
     * @param sequence The rotation sequence to use (e.g., ZYX, XYZ).
     * @param angleUnit The unit of the angles, defaults to [AngleUnit.RADIANS].
     * @return A new [AffineTransformationMatrix3] representing the combined rotation.
     */
    public fun createRotation(
      firstAngle: Double,
      secondAngle: Double,
      thirdAngle: Double,
      sequence: RotationSequence,
      angleUnit: AngleUnit = AngleUnit.RADIANS,
    ): AffineTransformationMatrix3 {
      val first =
        if (angleUnit == AngleUnit.DEGREES) firstAngle * DEGREES_TO_RADIANS else firstAngle
      val second =
        if (angleUnit == AngleUnit.DEGREES) secondAngle * DEGREES_TO_RADIANS else secondAngle
      val third =
        if (angleUnit == AngleUnit.DEGREES) thirdAngle * DEGREES_TO_RADIANS else thirdAngle

      val c1 = cos(first)
      val s1 = sin(first)
      val c2 = cos(second)
      val s2 = sin(second)
      val c3 = cos(third)
      val s3 = sin(third)

      return when (sequence) {
        RotationSequence.XYZ ->
          AffineTransformationMatrix3(
            // spotless:off
            c2 * c3, -c2 * s3, s2, 0.0,
            c1 * s3 + c3 * s1 * s2, c1 * c3 - s1 * s2 * s3, -c2 * s1, 0.0,
            s1 * s3 - c1 * c3 * s2, c3 * s1 + c1 * s2 * s3, c1 * c2, 0.0
            // spotless:on
          )
        RotationSequence.XZY ->
          AffineTransformationMatrix3(
            // spotless:off
            c2 * c3, -s2, c2 * s3, 0.0,
            s1 * s3 + c1 * c3 * s2, c1 * c2, c1 * s2 * s3 - c3 * s1, 0.0,
            c3 * s1 * s2 - c1 * s3, c2 * s1, c1 * c3 + s1 * s2 * s3, 0.0
            // spotless:on
          )
        RotationSequence.YXZ ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c3 + s1 * s2 * s3, c3 * s1 * s2 - c1 * s3, c2 * s1, 0.0,
            c2 * s3, c2 * c3, -s2, 0.0,
            c1 * s2 * s3 - c3 * s1, c1 * c3 * s2 + s1 * s3, c1 * c2, 0.0
            // spotless:on
          )
        RotationSequence.YZX ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c2, s1 * s3 - c1 * c3 * s2, c3 * s1 + c1 * s2 * s3, 0.0,
            s2, c2 * c3, -c2 * s3, 0.0,
            -c2 * s1, c1 * s3 + c3 * s1 * s2, c1 * c3 - s1 * s2 * s3, 0.0
            // spotless:on
          )
        RotationSequence.ZXY ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c3 - s1 * s2 * s3, -c2 * s1, c1 * s3 + c3 * s1 * s2, 0.0,
            c3 * s1 + c1 * s2 * s3, c1 * c2, s1 * s3 - c1 * c3 * s2, 0.0,
            -c2 * s3, s2, c2 * c3, 0.0
            // spotless:on
          )
        RotationSequence.ZYX ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c2, c1 * s2 * s3 - c3 * s1, s1 * s3 + c1 * c3 * s2, 0.0,
            c2 * s1, c1 * c3 + s1 * s2 * s3, c3 * s1 * s2 - c1 * s3, 0.0,
            -s2, c2 * s3, c2 * c3, 0.0
            // spotless:on
          )
        RotationSequence.ZYZ ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c2 * c3 - s1 * s3, -c3 * s1 - c1 * c2 * s3, c1 * s2, 0.0,
            c1 * s3 + c2 * c3 * s1, c1 * c3 - c2 * s1 * s3, s1 * s2, 0.0,
            -c3 * s2, s2 * s3, c2, 0.0
            // spotless:on
          )
        RotationSequence.ZXZ ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c3 - c2 * s1 * s3, -c1 * s3 - c2 * c3 * s1, s1 * s2, 0.0,
            c3 * s1 + c1 * c2 * s3, c1 * c2 * c3 - s1 * s3, -c1 * s2, 0.0,
            s2 * s3, c3 * s2, c2, 0.0
            // spotless:on
          )
        RotationSequence.YZY ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c2 * c3 - s1 * s3, -c1 * s2, c3 * s1 + c1 * c2 * s3, 0.0,
            c3 * s2, c2, s2 * s3, 0.0,
            -c1 * s3 - c2 * c3 * s1, s1 * s2, c1 * c3 - c2 * s1 * s3, 0.0
            // spotless:on
          )
        RotationSequence.YXY ->
          AffineTransformationMatrix3(
            // spotless:off
            c1 * c3 - c2 * s1 * s3, s1 * s2, c1 * s3 + c2 * c3 * s1, 0.0,
            s2 * s3, c2, -c3 * s2, 0.0,
            -c3 * s1 - c1 * c2 * s3, c1 * s2, c1 * c2 * c3 - s1 * s3, 0.0
            // spotless:on
          )
        RotationSequence.XZX ->
          AffineTransformationMatrix3(
            // spotless:off
            c2, -c3 * s2, s2 * s3, 0.0,
            c1 * s2, c1 * c2 * c3 - s1 * s3, -c3 * s1 - c1 * c2 * s3, 0.0,
            s1 * s2, c1 * s3 + c2 * c3 * s1, c1 * c3 - c2 * s1 * s3, 0.0
            // spotless:on
          )
        RotationSequence.XYX ->
          AffineTransformationMatrix3(
            // spotless:off
            c2, s2 * s3, c3 * s2, 0.0,
            s1 * s2, c1 * c3 - c2 * s1 * s3, -c1 * s3 - c2 * c3 * s1, 0.0,
            -c1 * s2, c3 * s1 + c1 * c2 * s3, c1 * c2 * c3 - s1 * s3, 0.0
            // spotless:on
          )
      }
    }
  }
}

/**
 * Represents different rotation sequences for Euler and Tait-Bryan angles.
 *
 * Euler angles use the same axis twice (e.g., ZYZ), while Tait-Bryan angles use three different
 * axes (e.g., XYZ).
 */
public enum class RotationSequence {
  /** Rotate around X, then Y, then Z (Tait-Bryan angles) */
  XYZ,

  /** Rotate around X, then Z, then Y (Tait-Bryan angles) */
  XZY,

  /** Rotate around Y, then X, then Z (Tait-Bryan angles) */
  YXZ,

  /** Rotate around Y, then Z, then X (Tait-Bryan angles) */
  YZX,

  /** Rotate around Z, then X, then Y (Tait-Bryan angles) */
  ZXY,

  /** Rotate around Z, then Y, then X (Tait-Bryan angles) */
  ZYX,

  /** Rotate around Z, then Y, then Z (Euler angles) */
  ZYZ,

  /** Rotate around Z, then X, then Z (Euler angles) */
  ZXZ,

  /** Rotate around Y, then Z, then Y (Euler angles) */
  YZY,

  /** Rotate around Y, then X, then Y (Euler angles) */
  YXY,

  /** Rotate around X, then Z, then X (Euler angles) */
  XZX,

  /** Rotate around X, then Y, then X (Euler angles) */
  XYX,
}
