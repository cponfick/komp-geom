package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

class AffineTransformationMatrix2Test {
  private val testEquivalence: DoubleEquivalence = DoubleEquivalence(1e-12)

  @Test
  fun `toArray returns expected array`() {
    AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).toArray() shouldBe
      doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
  }

  @Test
  fun `toString returns expected string representation`() {
    AffineTransformationMatrix2(1.1, 2.2, 3.3, 4.4, 5.5, 6.6).toString() shouldBe
      "[1.1, 2.2, 3.3 | 4.4, 5.5, 6.6]"
  }

  @Test
  fun `determinant returns expected value`() {
    AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).determinant() shouldBe -3.0
  }

  private val inverseTestCases =
    listOf(
      Pair(
        AffineTransformationMatrix2(2.0, 3.0, 1.0, 2.0, 1.0, 6.0),
        AffineTransformationMatrix2(-0.25, 0.75, -4.25, 0.5, -0.5, 2.5),
      ),
      Pair(
        AffineTransformationMatrix2(2.0, 3.0, 1.0, 2.0, 2.0, 4.0),
        AffineTransformationMatrix2(-1.00, 1.5, -5.0, 1.0, -1.0, 3.0),
      ),
    )

  @Test
  fun `inverse returns expected transformation`() {
    for ((matrix, expectedInverse) in inverseTestCases) {
      matrix.inverse().apply { this shouldBe expectedInverse }
    }
  }

  @Test
  fun `inverse if det is zero throws exception`() {
    val matrix = AffineTransformationMatrix2(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    shouldThrow<IllegalArgumentException> { matrix.inverse() }
  }

  @Test
  fun `apply identity matrix returns same vector`() {
    val vector = Vec2(1.0, 2.0)
    AffineTransformationMatrix2.IDENTITY.apply(vector) shouldBe vector
  }

  @Test
  fun `apply transformation returns expected vector`() {
    val matrix = AffineTransformationMatrix2(2.0, 0.5, 10.0, 0.0, 1.5, 5.0)
    val vector = Vec2(3.0, 4.0)
    matrix.apply(vector) shouldBe Vec2(18.0, 11.0)
  }

  @Test
  fun `apply transformation and inverse transformation returns the original vector`() {
    val matrix = AffineTransformationMatrix2(2.0, 0.5, 10.0, 0.0, 1.5, 5.0)
    val invMatrix = matrix.inverse()
    val expected = Vec2(3.0, 4.0)
    matrix.apply(expected).let { invMatrix.apply(it) }.eq(expected, testEquivalence) shouldBe true
  }

  @Test
  fun `apply translation returns expected vector`() {
    val translationMatrix = AffineTransformationMatrix2(1.0, 0.0, 3.0, 0.0, 1.0, 2.0)
    val vector = Vec2(4.0, 5.0)
    translationMatrix.apply(vector) shouldBe Vec2(7.0, 7.0)
  }

  @Test
  fun `apply scaling returns expected vector`() {
    val scalingMatrix = AffineTransformationMatrix2(2.0, 0.0, 0.0, 0.0, 3.0, 0.0)
    val vector = Vec2(4.0, 5.0)
    scalingMatrix.apply(vector) shouldBe Vec2(8.0, 15.0)
  }

  @Test
  fun `apply rotation returns expected vector`() {
    val angle = PI
    val cosTheta = cos(angle)
    val sinTheta = sin(angle)
    val rotationMatrix =
      AffineTransformationMatrix2(cosTheta, -sinTheta, 0.0, sinTheta, cosTheta, 0.0)
    val vector = Vec2(1.0, 0.0)
    rotationMatrix.apply(vector) shouldBe Vec2(cosTheta, sinTheta)
  }
}
