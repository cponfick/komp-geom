package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.AngleUnit
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

  @Test
  fun `preserveOrientation returns true for positive determinant`() {
    AffineTransformationMatrix2.IDENTITY.preserveOrientation() shouldBe true
  }

  @Test
  fun `preserveOrientation returns false for negative determinant`() {
    val matrix = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
    matrix.preserveOrientation() shouldBe false
  }

  @Test
  fun `eq returns true for equal matrices`() {
    val matrix1 = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
    val matrix2 = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
    matrix1.eq(matrix2, testEquivalence) shouldBe true
  }

  @Test
  fun `eq returns false for different matrices`() {
    val matrix1 = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
    val matrix2 = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 7.0)
    matrix1.eq(matrix2, testEquivalence) shouldBe false
  }

  @Test
  fun `eq returns true for approximately equal matrices`() {
    val matrix1 = AffineTransformationMatrix2(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
    val matrix2 =
      AffineTransformationMatrix2(
        1.0 + 1e-13,
        2.0 + 1e-13,
        3.0 + 1e-13,
        4.0 + 1e-13,
        5.0 + 1e-13,
        6.0 + 1e-13,
      )
    matrix1.eq(matrix2, testEquivalence) shouldBe true
  }

  @Test
  fun `translate returns correct result`() {
    val expectedMatrix = AffineTransformationMatrix2(1.0, 0.0, 3.0, 0.0, 1.0, 6.0)
    val identityMatrix = AffineTransformationMatrix2.IDENTITY

    identityMatrix.translate(Vec2(3.0, 6.0)) shouldBe expectedMatrix
    identityMatrix.translate(3.0, 6.0) shouldBe expectedMatrix
  }

  @Test
  fun `scale returns correct result`() {
    val expectedMatrix = AffineTransformationMatrix2(2.0, 0.0, 0.0, 0.0, 3.0, 0.0)
    val identityMatrix = AffineTransformationMatrix2.IDENTITY

    identityMatrix.scale(Vec2(2.0, 3.0)) shouldBe expectedMatrix
    identityMatrix.scale(2.0, 3.0) shouldBe expectedMatrix
    identityMatrix.scale(4.0) shouldBe AffineTransformationMatrix2(4.0, 0.0, 0.0, 0.0, 4.0, 0.0)
  }

  @Test
  fun `rotate returns correct result`() {
    val radians = PI / 2.0
    val degrees = 90.0
    val cos = cos(radians)
    val sin = sin(radians)
    val expectedMatrix = AffineTransformationMatrix2(cos, -sin, 0.0, sin, cos, 0.0)
    val identityMatrix = AffineTransformationMatrix2.IDENTITY

    identityMatrix.rotate(radians) shouldBe expectedMatrix
    identityMatrix.rotate(degrees, AngleUnit.DEGREES) shouldBe expectedMatrix
  }

  @Test
  fun `times returns correct result`() {
    // The previous tests prove that the individual transformations work correctly.
    // Here we just check that the multiplication of two matrices gives the expected result.
    val cos = cos(PI / 2.0)
    val sin = sin(PI / 2.0)
    val expectedMatrix = AffineTransformationMatrix2(2.0 * cos, -sin, 2.0, sin, 3.0 * cos, 3.0)

    val actual =
      AffineTransformationMatrix2.IDENTITY.rotate(PI / 2.0).translate(2.0, 3.0).scale(2.0, 3.0)

    actual shouldBe expectedMatrix
  }

  @Test
  fun `shear returns correct result`() {
    val shearX = 2.0
    val shearY = 3.0
    val expectedMatrix = AffineTransformationMatrix2(1.0, shearX, 0.0, shearY, 1.0, 0.0)
    val identityMatrix = AffineTransformationMatrix2.IDENTITY

    identityMatrix.shear(shearX, shearY) shouldBe expectedMatrix
  }

  @Test
  fun `createTranslation returns expected matrix`() {
    val translation = Vec2(3.0, 4.0)
    val expectedMatrix = AffineTransformationMatrix2(1.0, 0.0, 3.0, 0.0, 1.0, 4.0)

    AffineTransformationMatrix2.createTranslation(translation) shouldBe expectedMatrix
    AffineTransformationMatrix2.createTranslation(3.0, 4.0) shouldBe expectedMatrix
  }

  @Test
  fun `createScaling returns expected matrix`() {
    val scaling = Vec2(2.0, 3.0)
    val expectedMatrix = AffineTransformationMatrix2(2.0, 0.0, 0.0, 0.0, 3.0, 0.0)

    AffineTransformationMatrix2.createScaling(scaling) shouldBe expectedMatrix
    AffineTransformationMatrix2.createScaling(2.0, 3.0) shouldBe expectedMatrix
  }

  @Test
  fun `createRotation returns expected matrix`() {
    val radians = PI / 4.0
    val cos = cos(radians)
    val sin = sin(radians)
    val expectedMatrix = AffineTransformationMatrix2(cos, -sin, 0.0, sin, cos, 0.0)

    AffineTransformationMatrix2.createRotation(radians) shouldBe expectedMatrix
    AffineTransformationMatrix2.createRotation(45.0, AngleUnit.DEGREES) shouldBe expectedMatrix
  }

  @Test
  fun `createShear returns expected matrix`() {
    val shearX = 2.0
    val shearY = 3.0
    val expectedMatrix = AffineTransformationMatrix2(1.0, shearX, 0.0, shearY, 1.0, 0.0)

    AffineTransformationMatrix2.createShear(shearX, shearY) shouldBe expectedMatrix
  }
}
