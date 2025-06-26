package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.AffineTransformationMatrix
import io.github.cponfick.kompgeom.euclidean.assertIsFiniteAndNotZero

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

  override fun hashCode(): Int {
    var result = m00.hashCode()
    result = 31 * result + m01.hashCode()
    result = 31 * result + m02.hashCode()
    result = 31 * result + m10.hashCode()
    result = 31 * result + m11.hashCode()
    result = 31 * result + m12.hashCode()
    return result
  }

  override fun toString(): String {
    return "[$m00, $m01, $m02 | $m10, $m11, $m12]"
  }

  override fun determinant(): Double = determinant(m00, m01, m10, m11)

  override fun apply(point: Vec2): Vec2 =
      Vec2(
          m00 * point.x + m01 * point.y + m02,
          m10 * point.x + m11 * point.y + m12,
      )

  override fun inverse(): Transformer<Vec2> {
    // https://www.geeksforgeeks.org/maths/inverse-of-3x3-matrix/
    val invDet = 1.0 / determinant().assertIsFiniteAndNotZero()

    val c00 = invDet * m11
    val c01 = -invDet * m10

    val c10 = -invDet * m01
    val c11 = invDet * m00

    val c20 = invDet * determinant(m01, m02, m11, m12)
    val c21 = -invDet * determinant(m00, m02, m10, m12)

    return AffineTransformationMatrix2(c00, c10, c20, c01, c11, c21)
  }

  public companion object {
    /** Identity matrix for transformations in one dimensional space. */
    public val IDENTITY: AffineTransformationMatrix2 =
        AffineTransformationMatrix2(1.0, 0.0, 0.0, 0.0, 1.0, 0.0)
  }
}

internal fun determinant(
    m00: Double,
    m01: Double,
    m10: Double,
    m11: Double,
): Double = m00 * m11 - m01 * m10
