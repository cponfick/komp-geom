package io.github.cponfick.kompgeom.euclidean.utils

/** Utility for matrix operations. */
public object MatrixUtil {

  /**
   * Computes the determinant of a 2x2 matrix defined by its elements.
   *
   * @param m00 The element at row 0, column 0.
   * @param m01 The element at row 0, column 1.
   * @param m10 The element at row 1, column 0.
   * @param m11 The element at row 1, column 1.
   * @return The determinant of the matrix.
   */
  public fun determinant(m00: Double, m01: Double, m10: Double, m11: Double): Double =
    m00 * m11 - m01 * m10

  /**
   * Computes the determinant of a 3x3 matrix.*
   *
   * @param m00 The element at row 0, column 0.
   * @param m01 The element at row 0, column 1.
   * @param m02 The element at row 0, column 2.
   * @param m10 The element at row 1, column 0.
   * @param m11 The element at row 1, column 1.
   * @param m12 The element at row 1, column 2.
   * @param m20 The element at row 2, column 0.
   * @param m21 The element at row 2, column 1.
   * @param m22 The element at row 2, column 2.
   */
  public fun determinant(
    m00: Double,
    m01: Double,
    m02: Double,
    m10: Double,
    m11: Double,
    m12: Double,
    m20: Double,
    m21: Double,
    m22: Double,
  ): Double =
    ((m00 * m11 * m22) + (m01 * m12 * m20) + (m02 * m10 * m21)) -
      ((m00 * m12 * m21) + (m01 * m10 * m22) + (m02 * m11 * m20))
}
