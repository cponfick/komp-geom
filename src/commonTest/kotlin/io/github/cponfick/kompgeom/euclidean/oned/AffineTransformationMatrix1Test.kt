package io.github.cponfick.kompgeom.euclidean.oned

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AffineTransformationMatrix1Test {

  @Test
  fun `toArray returns the expected array representation`() {
    AffineTransformationMatrix1(2.0, 3.0).toArray() shouldBe doubleArrayOf(2.0, 3.0)
  }

  @Test
  fun `determinant returns the expected value`() {
    AffineTransformationMatrix1(2.0, 3.0).determinant() shouldBe 2.0
  }

  @Test
  fun `inverse returns the expected transformation matrix`() {
    AffineTransformationMatrix1(2.0, 3.0).inverse() shouldBe AffineTransformationMatrix1(0.5, -1.5)
  }

  private val applyTestCases =
    listOf(
      Triple(Vec1(3.0), AffineTransformationMatrix1(2.0, 3.0), Vec1(9.0)),
      Triple(Vec1(3.0), AffineTransformationMatrix1(0.5, -2.5), Vec1(-1.0)),
    )

  @Test
  fun `apply transforms a point correctly`() {
    for ((point, transformation, expected) in applyTestCases) {
      transformation.apply(point) shouldBe expected
    }
  }
}
