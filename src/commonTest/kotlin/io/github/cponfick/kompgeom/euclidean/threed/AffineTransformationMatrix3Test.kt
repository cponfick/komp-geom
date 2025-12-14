package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.DEGREES_TO_RADIANS
import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
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
    matrix1.eq(matrix2, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
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
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateX instance method applies rotation correctly`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, -1.0, 0.0,
        0.0, 1.0, 0.0, 0.0
        // spotless:on
      )
    val identityMatrix = AffineTransformationMatrix3.IDENTITY

    val actualRad = identityMatrix.rotateX(PI / 2)
    val actualDeg = identityMatrix.rotateX(90.0, AngleUnit.DEGREES)

    actualRad.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
    actualDeg.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateY instance method applies rotation correctly`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, 0.0, 1.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        -1.0, 0.0, 0.0, 0.0
        // spotless:on
      )
    val identityMatrix = AffineTransformationMatrix3.IDENTITY
    identityMatrix.rotateY(PI / 2).eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
    identityMatrix
      .rotateY(90.0, AngleUnit.DEGREES)
      .eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotateZ instance method applies rotation correctly`() {
    val expected =
      AffineTransformationMatrix3(
        // spotless:off
        0.0, -1.0, 0.0, 0.0,
        1.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        // spotless:on
      )
    val identityMatrix = AffineTransformationMatrix3.IDENTITY
    identityMatrix.rotateZ(PI / 2).eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
    identityMatrix
      .rotateZ(90.0, AngleUnit.DEGREES)
      .eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `combined rotations work correctly`() {
    val vector = Vec3(1.0, 0.0, 0.0)
    val transform =
      AffineTransformationMatrix3.IDENTITY.rotateZ(90.0, AngleUnit.DEGREES)
        .rotateX(90.0, AngleUnit.DEGREES)

    val expected = Vec3(0.0, 1.0, 0.0)
    val actual = transform.apply(vector)
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `rotation with translation works correctly`() {
    val vector = Vec3(1.0, 0.0, 0.0)
    val transform =
      AffineTransformationMatrix3.IDENTITY.translate(2.0, 3.0, 4.0).rotateZ(90.0, AngleUnit.DEGREES)

    val expected = Vec3(2.0, 4.0, 4.0)
    val actual = transform.apply(vector)
    actual.eq(expected, EpsilonDoubleEquivalence(1e-10)) shouldBe true
  }

  @Test
  fun `360 degree rotation returns identity rotation`() {
    val rotX = AffineTransformationMatrix3.createRotationX(360.0, AngleUnit.DEGREES)
    val rotY = AffineTransformationMatrix3.createRotationY(360.0, AngleUnit.DEGREES)
    val rotZ = AffineTransformationMatrix3.createRotationZ(360.0, AngleUnit.DEGREES)

    val identity = AffineTransformationMatrix3.IDENTITY
    val tolerance = EpsilonDoubleEquivalence(1e-10)

    rotX.eq(identity, tolerance) shouldBe true
    rotY.eq(identity, tolerance) shouldBe true
    rotZ.eq(identity, tolerance) shouldBe true
  }

  @Test
  fun `rotate XZX`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3

    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.XZX)
    val expected =
      AffineTransformationMatrix3.createRotationX(angle1) *
        AffineTransformationMatrix3.createRotationZ(angle2) *
        AffineTransformationMatrix3.createRotationX(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate YZY`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3

    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.YZY)
    val expected =
      AffineTransformationMatrix3.createRotationY(angle1) *
        AffineTransformationMatrix3.createRotationZ(angle2) *
        AffineTransformationMatrix3.createRotationY(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate ZYZ`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.ZYZ)
    val expected =
      AffineTransformationMatrix3.createRotationZ(angle1) *
        AffineTransformationMatrix3.createRotationY(angle2) *
        AffineTransformationMatrix3.createRotationZ(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate XYX`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3

    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.XYX)
    val expected =
      AffineTransformationMatrix3.createRotationX(angle1) *
        AffineTransformationMatrix3.createRotationY(angle2) *
        AffineTransformationMatrix3.createRotationX(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate YXY`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3

    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.YXY)
    val expected =
      AffineTransformationMatrix3.createRotationY(angle1) *
        AffineTransformationMatrix3.createRotationX(angle2) *
        AffineTransformationMatrix3.createRotationY(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate ZXZ`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3

    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.ZXZ)
    val expected =
      AffineTransformationMatrix3.createRotationZ(angle1) *
        AffineTransformationMatrix3.createRotationX(angle2) *
        AffineTransformationMatrix3.createRotationZ(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate XYZ`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.XYZ)
    val expected =
      AffineTransformationMatrix3.createRotationX(angle1) *
        AffineTransformationMatrix3.createRotationY(angle2) *
        AffineTransformationMatrix3.createRotationZ(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate XZY`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.XZY)
    val expected =
      AffineTransformationMatrix3.createRotationX(angle1) *
        AffineTransformationMatrix3.createRotationZ(angle2) *
        AffineTransformationMatrix3.createRotationY(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate YXZ`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.YXZ)
    val expected =
      AffineTransformationMatrix3.createRotationY(angle1) *
        AffineTransformationMatrix3.createRotationX(angle2) *
        AffineTransformationMatrix3.createRotationZ(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate YZX`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.YZX)
    val expected =
      AffineTransformationMatrix3.createRotationY(angle1) *
        AffineTransformationMatrix3.createRotationZ(angle2) *
        AffineTransformationMatrix3.createRotationX(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate ZXY`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.ZXY)
    val expected =
      AffineTransformationMatrix3.createRotationZ(angle1) *
        AffineTransformationMatrix3.createRotationX(angle2) *
        AffineTransformationMatrix3.createRotationY(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `rotate ZYX`() {
    val angle1 = PI / 6
    val angle2 = PI / 4
    val angle3 = PI / 3
    val actual =
      AffineTransformationMatrix3.createRotation(angle1, angle2, angle3, RotationSequence.ZYX)
    val expected =
      AffineTransformationMatrix3.createRotationZ(angle1) *
        AffineTransformationMatrix3.createRotationY(angle2) *
        AffineTransformationMatrix3.createRotationX(angle3)

    actual.eq(expected) shouldBe true
  }

  @Test
  fun `createRotation with degrees`() {
    val angle1Deg = 30.0
    val angle2Deg = 45.0
    val angle3Deg = 60.0

    val actualDeg =
      AffineTransformationMatrix3.createRotation(
        angle1Deg,
        angle2Deg,
        angle3Deg,
        RotationSequence.XYZ,
        AngleUnit.DEGREES,
      )

    val actualRad =
      AffineTransformationMatrix3.createRotation(
        angle1Deg * DEGREES_TO_RADIANS,
        angle2Deg * DEGREES_TO_RADIANS,
        angle3Deg * DEGREES_TO_RADIANS,
        RotationSequence.XYZ,
      )

    actualDeg.eq(actualRad) shouldBe true
  }

  @Test
  fun `instance rotate method with degrees`() {
    val baseMatrix = AffineTransformationMatrix3.createScaling(2.0, 2.0, 2.0)
    val angle1Deg = 30.0
    val angle2Deg = 45.0
    val angle3Deg = 60.0

    val actual =
      baseMatrix.rotate(angle1Deg, angle2Deg, angle3Deg, RotationSequence.YXZ, AngleUnit.DEGREES)
    val expected =
      baseMatrix *
        AffineTransformationMatrix3.createRotation(
          angle1Deg,
          angle2Deg,
          angle3Deg,
          RotationSequence.YXZ,
          AngleUnit.DEGREES,
        )

    actual.eq(expected) shouldBe true
  }
}
