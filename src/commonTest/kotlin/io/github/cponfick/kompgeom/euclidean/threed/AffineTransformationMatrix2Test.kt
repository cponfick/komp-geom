package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.euclidean.twod.AffineTransformationMatrix2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AffineTransformationMatrix2Test {

  @Test
  fun `toArray returns expected array`() {
    AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).toArray() shouldBe
        doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
  }

  @Test
  fun `toString returns expected string representation`() {
    AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).toString() shouldBe
        "[1.0, 2.0, 3.0 | 4.0, 5.0, 6.0]"
  }

  @Test
  fun `determinant returns expected value`() {
    AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).determinant() shouldBe -3.0
  }

  private val inverseTestCases =
      listOf(
          Pair(
              AffineTransformationMatrix2(2.0, 3.0, 1.0, 2.0, 1.0, 6.0),
              AffineTransformationMatrix2(-0.25, 0.75, -4.25, 0.5, -0.5, 2.5)),
          Pair(
              AffineTransformationMatrix2(2.0, 3.0, 1.0, 2.0, 2.0, 4.0),
              AffineTransformationMatrix2(-1.00, 1.5, -5.0, 1.0, -1.0, 3.0)))

  @Test
  fun `inverse returns expected transformation`() {
    for ((matrix, expectedInverse) in inverseTestCases) {
      matrix.inverse().apply { this shouldBe expectedInverse }
    }
  }

  @Test
  fun `apply identity matrix returns same vector`() {
    val vector = Vec2(1.0, 2.0)
    AffineTransformationMatrix2.IDENTITY.apply(vector) shouldBe vector
  }

  @Test
  fun `apply transformation returns expected vector`() {
    val matrix = AffineTransformationMatrix2(2.0  , 0.5 , 10.0, 0.0,   1.5,  5.0 )
    val vector = Vec2(3.0, 4.0)
    matrix.apply(vector) shouldBe Vec2(18.0,11.0)
  }
}
