package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.Vector1
import io.github.cponfick.kompgeom.core.assertIsFiniteAndNotZero
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Affine transformation matrix for one-dimensional Euclidean vectors.
 *
 * @property m00 The scaling factor for the x-coordinate.
 * @property m01 The translation factor for the x-coordinate.
 */
public data class AffineTransformationMatrix1(public val m00: Double, public val m01: Double) :
  Transformer<Vector1<*>> {

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

  override fun <T : Vector1<*>> apply(obj: T): T {
    @Suppress("UNCHECKED_CAST")
    return Vec1(m00 * obj.x + m01) as T
  }

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

  /**
   * Translates the transformation matrix by a given distance.
   *
   * @param translation The distance to translate the matrix.
   * @return A new affine transformation matrix with the translation applied.
   */
  public fun translate(translation: Double): AffineTransformationMatrix1 =
    AffineTransformationMatrix1(m00, m01 + translation)

  /**
   * Translates the transformation matrix by a given vector.
   *
   * @param translation The vector to translate the matrix.
   * @return A new affine transformation matrix with the translation applied.
   */
  public fun translate(translation: Vec1): AffineTransformationMatrix1 = translate(translation.x)

  /**
   * Scales the transformation matrix by a given factor.
   *
   * @param factor The scaling factor.
   * @return A new affine transformation matrix with the scaling applied.
   */
  public fun scale(factor: Double): AffineTransformationMatrix1 =
    AffineTransformationMatrix1(m00 * factor, m01)

  /**
   * Scales the transformation matrix by a given vector.
   *
   * @param factor The vector containing the scaling factor for the x-coordinate.
   * @return A new affine transformation matrix with the scaling applied.
   */
  public fun scale(factor: Vec1): AffineTransformationMatrix1 = scale(factor.x)

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix1 = AffineTransformationMatrix1(1.0, 0.0)

    /**
     * Creates a translation transformation matrix.
     *
     * @param translation The distance to translate the matrix.
     * @return A new affine transformation matrix representing the translation.
     */
    public fun createTranslation(translation: Double): AffineTransformationMatrix1 =
      AffineTransformationMatrix1(1.0, translation)

    /** Create a scaling transformation matrix. */
    public fun createScaling(factor: Double): AffineTransformationMatrix1 =
      AffineTransformationMatrix1(factor, 0.0)
  }
}
