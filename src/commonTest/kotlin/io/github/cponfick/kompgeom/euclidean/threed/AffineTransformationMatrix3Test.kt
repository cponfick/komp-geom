package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
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
            2.0/3, -1.0/3, 0.0, -0.0,
            1.0/6, 1.0/6, -0.5, -0.5,
            -5.0/6, 1.0/6, 3.0/2.0, -0.5
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
}
