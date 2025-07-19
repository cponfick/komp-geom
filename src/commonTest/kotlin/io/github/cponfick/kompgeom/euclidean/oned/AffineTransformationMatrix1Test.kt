package io.github.cponfick.kompgeom.euclidean.oned

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AffineTransformationMatrix1Test {
  @Test
  fun `toArray returns the expected array representation`() {
    val matrix = AffineTransformationMatrix1(2.0, 3.0)
    val actual = matrix.toArray()
    actual shouldBe doubleArrayOf(2.0, 3.0)
  }

  @Test
  fun `toArray handles negative values correctly`() {
    val matrix = AffineTransformationMatrix1(-2.5, -3.7)
    val actual = matrix.toArray()
    actual shouldBe doubleArrayOf(-2.5, -3.7)
  }

  @Test
  fun `determinant returns the expected value`() {
    for ((matrix, expected) in determinantTestCases) {
      val actual = matrix.determinant()
      actual shouldBe expected
    }
  }

  @Test
  fun `inverse returns the expected transformation matrix`() {
    AffineTransformationMatrix1(2.0, 3.0).inverse() shouldBe AffineTransformationMatrix1(0.5, -1.5)
  }

  @Test
  fun `inverse of identity matrix returns identity`() {
    val identity = AffineTransformationMatrix1(1.0, 0.0)
    val inverse = identity.inverse()
    inverse shouldBe AffineTransformationMatrix1(1.0, -0.0)
  }

  @Test
  fun `apply transforms a point correctly`() {
    for ((point, transformation, expected) in applyTestCases) {
      val actual = transformation.apply(point)
      actual shouldBe expected
    }
  }

  @Test
  fun `apply with identity transformation returns original point`() {
    val point = Vec1(5.0)
    val identity = AffineTransformationMatrix1(1.0, 0.0)
    val actual = identity.apply(point)
    actual shouldBe point
  }

  @Test
  fun `apply translation only transformation`() {
    val point = Vec1(3.0)
    val translation = AffineTransformationMatrix1(1.0, 5.0)
    val actual = translation.apply(point)
    actual shouldBe Vec1(8.0)
  }

  @Test
  fun `apply scaling only transformation`() {
    val point = Vec1(4.0)
    val scaling = AffineTransformationMatrix1(2.0, 0.0)
    val actual = scaling.apply(point)
    actual shouldBe Vec1(8.0)
  }

  companion object {
    private val applyTestCases =
      listOf(
        Triple(Vec1(3.0), AffineTransformationMatrix1(2.0, 3.0), Vec1(9.0)),
        Triple(Vec1(3.0), AffineTransformationMatrix1(0.5, -2.5), Vec1(-1.0)),
        Triple(Vec1(0.0), AffineTransformationMatrix1(2.0, 3.0), Vec1(3.0)),
        Triple(Vec1(-1.0), AffineTransformationMatrix1(2.0, 3.0), Vec1(1.0)),
        Triple(Vec1(5.0), AffineTransformationMatrix1(1.0, 0.0), Vec1(5.0)),
      )

    private val determinantTestCases =
      listOf(
        Pair(AffineTransformationMatrix1(2.0, 3.0), 2.0),
        Pair(AffineTransformationMatrix1(0.5, -2.5), 0.5),
        Pair(AffineTransformationMatrix1(1.0, 0.0), 1.0),
        Pair(AffineTransformationMatrix1(-3.0, 1.5), -3.0),
      )
  }
}
