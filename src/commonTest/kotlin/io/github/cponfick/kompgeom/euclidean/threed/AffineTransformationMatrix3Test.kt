package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class AffineTransformationMatrix3Test {

  @Test
  fun `test toArray returns correct array representation`() {
    val matrix =
      AffineTransformationMatrix3(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0)
    val expected = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0)
    matrix.toArray() shouldBe expected
  }

  @Test
  fun `inverse with determinant zero throws exception`() {
    val matrix =
      AffineTransformationMatrix3(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    shouldThrow<IllegalArgumentException> { matrix.inverse() }
  }

  @Test
  fun `inverse returns expected transformation matrix`() {
    // spotless:off
    val matrix =
      AffineTransformationMatrix3(
        2.0, 3.0, 1.0, 2.0,
        1.0, 6.0, 2.0, 4.0,
        1.0, 1.0, 1.0, 1.0
      )
    val expectedInverse =
      AffineTransformationMatrix3(
        2.0 / 3, -1.0 / 3, 0.0, -0.0,
        1.0 / 6, 1.0 / 6, -0.5, -0.5,
        -5.0 / 6, 1.0 / 6, 3.0 / 2.0, -0.5
      )
    // spotless:on
    matrix.inverse().eq(expectedInverse)
  }

  @Test
  fun `apply translation returns expected vector`() {
    val matrix =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 2.0,
        0.0, 1.0, 0.0, 3.0,
        0.0, 0.0, 1.0, 4.0
        // spotless:on
      )
    val vector = Vec3(1.0, 1.0, 1.0)
    val expected = Vec3(3.0, 4.0, 5.0)
    matrix.apply(vector) shouldBe expected
  }

  @Test
  fun `apply scale returns expected vector`() {
    val matrix =
      AffineTransformationMatrix3(
        // spotless:off
        2.0, 0.0, 0.0, 0.0,
        0.0, 3.0, 0.0, 0.0,
        0.0, 0.0, 4.0, 0.0
        // spotless:on
      )
    val vector = Vec3(1.0, 1.0, 1.0)
    val expected = Vec3(2.0, 3.0, 4.0)
    matrix.apply(vector) shouldBe expected
  }

  @Test
  fun `apply rotation returns expected vector`() {
    val matrix =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, -1.0, 0.0, 0.0,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    val vector = Vec3(1.0, 2.0, 3.0)
    val expected = Vec3(-2.0, 1.0, 3.0)
    matrix.apply(vector) shouldBe expected
  }

  @Test
  fun `preserveOrientation returns true for identity matrix`() {
    val identityMatrix = AffineTransformationMatrix3.IDENTITY
    identityMatrix.preserveOrientation() shouldBe true
  }

  @Test
  fun `preserveOrientation returns false for non-identity matrix`() {
    val matrix =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, -1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    matrix.preserveOrientation() shouldBe false
  }

  @Test
  fun `eq returns true for equal matrices`() {
    val matrix1 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 2.0, 3.0, 4.0,
        5.0, 6.0, 7.0, 8.0,
        9.0, 10.0, 11.0, 12.0
        // spotless:on
      )
    val matrix2 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 2.0, 3.0, 4.0,
        5.0, 6.0, 7.0, 8.0,
        9.0, 10.0, 11.0, 12.0
        // spotless:on
      )
    matrix1.eq(matrix2) shouldBe true
  }

  @Test
  fun `eq returns false for different matrices`() {
    val matrix1 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 2.0, 3.0, 4.0,
        5.0, 6.0, 7.0, 8.0,
        9.0, 10.0, 11.0, 12.0
        // spotless:on
      )
    val matrix2 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 2.0, 3.0, 4.1,
        5.0, 6.0, 7.0, 8.0,
        9.0, 10.0, 11.0, 12.0
        // spotless:on
      )
    matrix1.eq(matrix2) shouldBe false
  }

  @Test
  fun `eq returns true for approximately equal matrices`() {
    val matrix1 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 2.0, 3.0, 4.0,
        5.0, 6.0, 7.0, 8.0,
        9.0, 10.0, 11.0, 12.0
        // spotless:on
      )
    val matrix2 =
      AffineTransformationMatrix3(
        // spotless:off
        1.0 + 1e-13, 2.0 + 1e-13, 3.0 + 1e-13, 4.0 + 1e-13,
        5.0 + 1e-13, 6.0 + 1e-13, 7.0 + 1e-13, 8.0 + 1e-13,
        9.0 + 1e-13, 10.0 + 1e-13, 11.0 + 1e-13, 12.0 + 1e-13
        // spotless:on
      )
    matrix1.eq(matrix2, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `translate returns expected translation matrix`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 2.0,
        0.0, 1.0, 0.0, 3.0,
        0.0, 0.0, 1.0, 4.0
        // spotless:on
      )
    val identityMatrix = AffineTransformationMatrix3.IDENTITY

    identityMatrix.translate(2.0, 3.0, 4.0) shouldBe expected
    identityMatrix.translate(Vec3(2.0, 3.0, 4.0)) shouldBe expected
  }

  @Test
  fun `scale returns expected scaling matrix`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        2.0, 0.0, 0.0, 0.0,
        0.0, 3.0, 0.0, 0.0,
        0.0, 0.0, 4.0, 0.0
        // spotless:on
      )
    val identityMatrix = AffineTransformationMatrix3.IDENTITY

    identityMatrix.scale(2.0, 3.0, 4.0) shouldBe expected
    identityMatrix.scale(Vec3(2.0, 3.0, 4.0)) shouldBe expected
  }

  @Test
  fun `createTranslation returns expected translation matrix`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 2.0,
        0.0, 1.0, 0.0, 3.0,
        0.0, 0.0, 1.0, 4.0
        // spotless:on
      )

    AffineTransformationMatrix3.createTranslation(2.0, 3.0, 4.0) shouldBe expected
    AffineTransformationMatrix3.createTranslation(Vec3(2.0, 3.0, 4.0)) shouldBe expected
  }

  @Test
  fun `createScaling returns expected scaling matrix`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        2.0, 0.0, 0.0, 0.0,
        0.0, 3.0, 0.0, 0.0,
        0.0, 0.0, 4.0, 0.0
        // spotless:on
      )

    AffineTransformationMatrix3.createScaling(2.0, 3.0, 4.0) shouldBe expected
    AffineTransformationMatrix3.createScaling(Vec3(2.0, 3.0, 4.0)) shouldBe expected
    AffineTransformationMatrix3.createScaling(3.0) shouldBe
      AffineTransformationMatrix3.IDENTITY.scale(3.0)
  }

  @Test
  fun `times returns expected matrix multiplication result`() {
    val expectedMatrix =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 4.0,
        0.0, 2.0, 0.0, 5.0,
        0.0, 0.0, 3.0, 6.0
        // spotless:on
      )
    val matrix1 = AffineTransformationMatrix3.IDENTITY.translate(4.0, 5.0, 6.0)
    val matrix2 = AffineTransformationMatrix3.IDENTITY.scale(1.0, 2.0, 3.0)

    matrix1 * matrix2 shouldBe expectedMatrix
  }

  @Test
  fun `createRotationX returns expected rotation matrix for 90 degrees`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, -1.0, 0.0,
        0.0, 1.0, 0.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationX(90.0, AngleUnit.DEGREES)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `createRotationX returns expected rotation matrix for PI_2 radians`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, -1.0, 0.0,
        0.0, 1.0, 0.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationX(PI / 2)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `createRotationY returns expected rotation matrix for 90 degrees`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, 0.0, 1.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        -1.0, 0.0, 0.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationY(90.0, AngleUnit.DEGREES)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `createRotationY returns expected rotation matrix for PI_2 radians`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, 0.0, 1.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        -1.0, 0.0, 0.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationY(PI / 2)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `createRotationZ returns expected rotation matrix for 90 degrees`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, -1.0, 0.0, 0.0,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationZ(90.0, AngleUnit.DEGREES)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `createRotationZ returns expected rotation matrix for PI_2 radians`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, -1.0, 0.0, 0.0,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    val actual = AffineTransformationMatrix3.createRotationZ(PI / 2)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateX instance method applies rotation correctly`() {
    val matrix = AffineTransformationMatrix3.IDENTITY
    val rotated = matrix.rotateX(90.0, AngleUnit.DEGREES)
    val expected = AffineTransformationMatrix3.createRotationX(90.0, AngleUnit.DEGREES)
    rotated.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateY instance method applies rotation correctly`() {
    val matrix = AffineTransformationMatrix3.IDENTITY
    val rotated = matrix.rotateY(PI / 2)
    val expected = AffineTransformationMatrix3.createRotationY(PI / 2)
    rotated.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateZ instance method applies rotation correctly`() {
    val matrix = AffineTransformationMatrix3.IDENTITY
    val rotated = matrix.rotateZ(45.0, AngleUnit.DEGREES)
    val expected = AffineTransformationMatrix3.createRotationZ(45.0, AngleUnit.DEGREES)
    rotated.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `combined rotations work correctly`() {
    val vector = Vec3(1.0, 0.0, 0.0)
    val transform =
      AffineTransformationMatrix3.IDENTITY.rotateZ(90.0, AngleUnit.DEGREES)
        .rotateX(90.0, AngleUnit.DEGREES)

    val expected = Vec3(0.0, 1.0, 0.0)
    val actual = transform.apply(vector)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotation with translation works correctly`() {
    val vector = Vec3(1.0, 0.0, 0.0)
    val transform =
      AffineTransformationMatrix3.IDENTITY.translate(2.0, 3.0, 4.0).rotateZ(90.0, AngleUnit.DEGREES)

    val expected = Vec3(2.0, 4.0, 4.0)
    val actual = transform.apply(vector)
    actual.eq(expected, DoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `360 degree rotation returns identity rotation`() {
    val rotX = AffineTransformationMatrix3.createRotationX(360.0, AngleUnit.DEGREES)
    val rotY = AffineTransformationMatrix3.createRotationY(360.0, AngleUnit.DEGREES)
    val rotZ = AffineTransformationMatrix3.createRotationZ(360.0, AngleUnit.DEGREES)

    val identity = AffineTransformationMatrix3.IDENTITY
    val tolerance = DoubleEquivalence(1e-10)

    rotX.eq(identity, tolerance) shouldBe true
    rotY.eq(identity, tolerance) shouldBe true
    rotZ.eq(identity, tolerance) shouldBe true
  }
}
