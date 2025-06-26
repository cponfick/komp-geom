package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.AngleUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class Vec2Test {

  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = Vec2(1.0, 2.0)
    vector.x shouldBe 1.0
    vector.y shouldBe 2.0
  }

  @Test
  fun `copy constructor creates an identical vector`() {
    val original = Vec2(3.0, 4.0)
    val copy = Vec2(original)
    original shouldBe copy
  }

  @Test
  fun `dimensions returns 2`() {
    val vector = Vec2(1.0, 2.0)
    vector.dimensions() shouldBe 2
  }

  private val angleInRadiansTestCases =
    listOf(
      Triple(Vec2(1.0, 0.0), Vec2(0.0, 1.0), PI / 2),
      Triple(Vec2(1.0, 1.0), Vec2(1.0, -1.0), PI / 2),
      Triple(Vec2(1.0, 0.0), Vec2(-1.0, 0.0), PI),
    )

  @Test
  fun `angle method calculates angle between two vectors in radians`() {
    for ((vector1, vector2, expected) in angleInRadiansTestCases) {
      val actual = vector1.angle(vector2, AngleUnit.RADIANS)
      actual shouldBe expected
    }
  }

  private val angleInDegreesTestCases =
    listOf(
      Triple(Vec2(1.0, 0.0), Vec2(0.0, 1.0), 90.0),
      Triple(Vec2(1.0, 1.0), Vec2(1.0, -1.0), 90.0),
      Triple(Vec2(1.0, 0.0), Vec2(-1.0, 0.0), 180.0),
    )

  @Test
  fun `angle method calculates angle between two vectors in degrees`() {
    for ((vector1, vector2, expected) in angleInDegreesTestCases) {
      val actual = vector1.angle(vector2, AngleUnit.DEGREES)
      actual shouldBe expected
    }
  }

  @Test
  fun `dot operator calculates dot product of two vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(3.0, 4.0)
    val actual = vector1 dot vector2
    actual shouldBe 11.0F
  }

  @Test
  fun `norm calculates correct magnitude of vector`() {
    val vector = Vec2(3.0, 4.0)
    val actual = vector.norm()
    actual shouldBe 5.0F
  }

  @Test
  fun `times operator multiplies vector by scalar`() {
    val vector = Vec2(1.0, 2.0)
    val scalar = 3.0
    val actual = vector * scalar
    actual shouldBe Vec2(3.0, 6.0)
  }

  @Test
  fun `minus operator subtracts two vectors`() {
    val vector1 = Vec2(5.0, 6.0)
    val vector2 = Vec2(3.0, 4.0)
    val actual = vector1 - vector2
    actual shouldBe Vec2(2.0, 2.0)
  }

  @Test
  fun `normalize method normalizes vector`() {
    val vector = Vec2(3.0, 4.0)
    val actual = vector.normalize()
    actual shouldBe Vec2(0.6, 0.8)
  }

  @Test
  fun `normalize method throws exception for zero vector`() {
    shouldThrow<ArithmeticException> { Vec2.ZERO.normalize() }
  }

  @Test
  fun `negate operator negates the vector`() {
    val vector = Vec2(1.0, 2.0)
    val actual = -vector
    actual shouldBe Vec2(-1.0, -2.0)
  }

  @Test
  fun `plus operator adds two vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(3.0, 4.0)
    val actual = vector1 + vector2
    actual shouldBe Vec2(4.0, 6.0)
  }

  private val isNaNTestCases =
    listOf(
      Pair(Vec2(Double.NaN, 1.0), true),
      Pair(Vec2(1.0, Double.NaN), true),
      Pair(Vec2(Double.NaN, Double.NaN), true),
      Pair(Vec2(1.0, 1.0), false),
    )

  @Test
  fun `isNaN returns expected result`() {
    for ((vector, expected) in isNaNTestCases) {
      vector.isNaN() shouldBe expected
    }
  }

  private val isInfiniteTestCases =
    listOf(
      Pair(Vec2(Double.POSITIVE_INFINITY, 1.0), true),
      Pair(Vec2(1.0, Double.NEGATIVE_INFINITY), true),
      Pair(Vec2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), true),
      Pair(Vec2(1.0, 1.0), false),
    )

  @Test
  fun `isInfinite returns expected result`() {
    for ((vector, expected) in isInfiniteTestCases) {
      vector.isInfinite() shouldBe expected
    }
  }

  @Test
  fun `project returns expected projection of vector onto another vector`() {
    val vector1 = Vec2(3.0, 4.0)
    val vector2 = Vec2(1.0, 0.0)
    val actual = vector1.project(vector2)
    actual shouldBe Vec2(3.0, 0.0)
  }

  @Test
  fun `project throws exception for zero vector`() {
    val vector1 = Vec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.project(Vec2.ZERO) }
  }

  @Test
  fun `project throws exception for NaN vector`() {
    val vector1 = Vec2(3.0, 4.0)
    val nanVector = Vec2(Double.NaN, Double.NaN)
    shouldThrow<IllegalArgumentException> { vector1.project(nanVector) }
  }

  @Test
  fun `project throws exception for infinite vector`() {
    val vector1 = Vec2(3.0, 4.0)
    val infiniteVector = Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    shouldThrow<IllegalArgumentException> { vector1.project(infiniteVector) }
  }

  @Test
  fun `reject returns expected rejection of vector from another vector`() {
    val vector1 = Vec2(3.0, 4.0)
    val vector2 = Vec2(1.0, 0.0)
    val actual = vector1.reject(vector2)
    actual shouldBe Vec2(0.0, 4.0)
  }

  @Test
  fun `reject throws exception for zero vector`() {
    val vector1 = Vec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.reject(Vec2.ZERO) }
  }

  @Test
  fun `reject throws exception for NaN vector`() {
    val vector1 = Vec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.reject(Vec2.NaN) }
  }

  @Test
  fun `reject throws exception for infinite vector`() {
    val vector1 = Vec2(3.0, 4.0)
    val infiniteVector = Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    shouldThrow<IllegalArgumentException> { vector1.reject(infiniteVector) }
  }

  @Test
  fun `eq returns true for equal vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(1.0, 2.0)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq returns false for different vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(2.0, 3.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `eq returns true for approximately equal vectors`() {
    val vector1 = Vec2(0.3, 0.2)
    val vector2 = Vec2(0.1 + 0.2, 0.2)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq returns false for non-approximately equal vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(1.000000001, 2.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `compute the correct lerp between two vectors`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(3.0, 4.0)
    val expected = Vec2(2.0, 3.0)
    val actual = vector1.lerp(vector2, 0.5)
    actual shouldBe expected
  }

  @Test
  fun `lerp returns the same vector when t is 0`() {
    val vector1 = Vec2(1.0, 2.0)
    val actual = vector1.lerp(vector1, 0.0)
    actual shouldBe vector1
  }

  @Test
  fun `lerp returns the other vector when t is 1`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(3.0, 4.0)
    val actual = vector1.lerp(vector2, 1.0)
    actual shouldBe vector2
  }

  @Test
  fun `lerp with t greater than 1 returns extrapolated vector`() {
    val vector1 = Vec2(1.0, 2.0)
    val vector2 = Vec2(3.0, 4.0)
    val expected = Vec2(5.0, 6.0)
    val actual = vector1.lerp(vector2, 2.0)
    actual shouldBe expected
  }

  @Test
  fun `toString returns expected format`() {
    val vector = Vec2(1.1, 2.1)
    vector.toString() shouldBe "Vec2(x=1.1, y=2.1)"
  }
}
