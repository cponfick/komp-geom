package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.core.toImmutable
import io.github.cponfick.kompgeom.core.toMutable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class MutableVec2Test {
  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = MutableVec2(1.0, 2.0)
    vector.x shouldBe 1.0
    vector.y shouldBe 2.0
  }

  @Test
  fun `toString returns correct format`() {
    val vector = MutableVec2(1.1, 2.2)
    vector.toString() shouldBe "MutableVec2(x=1.1, y=2.2)"
  }

  private val angleInRadiansTestCases =
    listOf(
      Triple(MutableVec2(1.0, 0.0), MutableVec2(0.0, 1.0), PI / 2),
      Triple(MutableVec2(1.0, 1.0), MutableVec2(1.0, -1.0), PI / 2),
      Triple(MutableVec2(1.0, 0.0), MutableVec2(-1.0, 0.0), PI),
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
      Triple(MutableVec2(1.0, 0.0), MutableVec2(0.0, 1.0), 90.0),
      Triple(MutableVec2(1.0, 1.0), MutableVec2(1.0, -1.0), 90.0),
      Triple(MutableVec2(1.0, 0.0), MutableVec2(-1.0, 0.0), 180.0),
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
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val actual = vector1 dot vector2
    actual shouldBe 11.0F
  }

  @Test
  fun `norm calculates correct magnitude of vector`() {
    val vector = MutableVec2(3.0, 4.0)
    val actual = vector.norm()
    actual shouldBe 5.0F
  }

  @Test
  fun `times operator multiplies vector by scalar`() {
    val vector = MutableVec2(1.0, 2.0)
    val scalar = 3.0
    val actual = vector * scalar
    actual.eq(MutableVec2(3.0, 6.0)) shouldBe true
  }

  @Test
  fun `minus operator subtracts two vectors`() {
    val vector1 = MutableVec2(5.0, 6.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val actual = vector1 - vector2
    actual.eq(MutableVec2(2.0, 2.0)) shouldBe true
  }

  @Test
  fun `normalize method normalizes vector`() {
    val vector = MutableVec2(0.0, 10.0)
    val actual = vector.normalize()
    actual.eq(MutableVec2(0.0, 1.0)) shouldBe true
  }

  @Test
  fun `normalize method throws exception for zero vector`() {
    shouldThrow<ArithmeticException> { MutableVec2.zero().normalize() }
  }

  @Test
  fun `negate operator negates the vector`() {
    val vector = MutableVec2(1.0, 2.0)
    val actual = -vector
    actual.eq(MutableVec2(-1.0, -2.0)) shouldBe true
  }

  @Test
  fun `plus operator adds two vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val actual = vector1 + vector2
    actual.eq(MutableVec2(4.0, 6.0)) shouldBe true
  }

  private val isNaNTestCases =
    listOf(
      Pair(MutableVec2(Double.NaN, 1.0), true),
      Pair(MutableVec2(1.0, Double.NaN), true),
      Pair(MutableVec2(Double.NaN, Double.NaN), true),
      Pair(MutableVec2(1.0, 1.0), false),
    )

  @Test
  fun `isNaN returns expected result`() {
    for ((vector, expected) in isNaNTestCases) {
      vector.isNaN() shouldBe expected
    }
  }

  private val isInfiniteTestCases =
    listOf(
      Pair(MutableVec2(Double.POSITIVE_INFINITY, 1.0), true),
      Pair(MutableVec2(1.0, Double.NEGATIVE_INFINITY), true),
      Pair(MutableVec2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), true),
      Pair(MutableVec2(1.0, 1.0), false),
    )

  @Test
  fun `isInfinite returns expected result`() {
    for ((vector, expected) in isInfiniteTestCases) {
      vector.isInfinite() shouldBe expected
    }
  }

  @Test
  fun `project returns expected projection of vector onto another vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    val vector2 = MutableVec2(1.0, 0.0)
    val actual = vector1.project(vector2)
    actual.eq(MutableVec2(3.0, 0.0))
  }

  @Test
  fun `project throws exception for zero vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.project(MutableVec2.zero()) }
  }

  @Test
  fun `project throws exception for NaN vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    val nanVector = MutableVec2(Double.NaN, Double.NaN)
    shouldThrow<IllegalArgumentException> { vector1.project(nanVector) }
  }

  @Test
  fun `project throws exception for infinite vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    val infiniteVector = MutableVec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    shouldThrow<IllegalArgumentException> { vector1.project(infiniteVector) }
  }

  @Test
  fun `reject returns expected rejection of vector from another vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    val vector2 = MutableVec2(1.0, 0.0)
    val actual = vector1.reject(vector2)
    actual.eq(MutableVec2(0.0, 4.0)) shouldBe true
  }

  @Test
  fun `reject throws exception for zero vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.reject(MutableVec2.zero()) }
  }

  @Test
  fun `reject throws exception for NaN vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    shouldThrow<IllegalArgumentException> { vector1.reject(MutableVec2.nan()) }
  }

  @Test
  fun `reject throws exception for infinite vector`() {
    val vector1 = MutableVec2(3.0, 4.0)
    val infiniteVector = MutableVec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    shouldThrow<IllegalArgumentException> { vector1.reject(infiniteVector) }
  }

  @Test
  fun `eq returns true for equal vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(1.0, 2.0)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq returns false for different vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(2.0, 3.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `eq returns true for approximately equal vectors`() {
    val vector1 = MutableVec2(0.3, 0.2)
    val vector2 = MutableVec2(0.1 + 0.2, 0.2)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq returns false for non-approximately equal vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(1.000000001, 2.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `compute the correct lerp between two vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val expected = MutableVec2(2.0, 3.0)
    val actual = vector1.lerp(vector2, 0.5)
    actual.eq(expected) shouldBe true
  }

  @Test
  fun `lerp returns the same vector when t is 0`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val actual = vector1.lerp(vector1, 0.0)
    actual shouldBe vector1
  }

  @Test
  fun `lerp returns the other vector when t is 1`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val actual = vector1.lerp(vector2, 1.0)
    actual.eq(vector2) shouldBe true
  }

  @Test
  fun `lerp with t greater than 1 returns extrapolated vector`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(3.0, 4.0)
    val expected = MutableVec2(5.0, 6.0)
    val actual = vector1.lerp(vector2, 2.0)
    actual.eq(expected) shouldBe true
  }

  @Test
  fun `signedArea returns expected value for two vectors`() {
    val vector1 = MutableVec2(1.0, 0.0)
    val vector2 = MutableVec2(0.0, 1.0)

    vector1.signedArea(vector2) shouldBe 1.0
    vector2.signedArea(vector1) shouldBe -1.0
  }

  @Test
  fun `signedArea returns zero for collinear vectors`() {
    val vector1 = MutableVec2(1.0, 2.0)
    val vector2 = MutableVec2(2.0, 4.0)

    vector1.signedArea(vector2) shouldBe 0.0
    vector2.signedArea(vector1) shouldBe 0.0
  }

  @Test
  fun `dimensions returns the correct value`() {
    val vector = MutableVec2(1.0, 2.0)
    vector.dimensions() shouldBe 2
  }

  @Test
  fun `isFinite returns true for finite vector`() {
    val vector = MutableVec2(1.0, 2.0)
    vector.isFinite() shouldBe true
  }

  @Test
  fun `isFinite returns false for vector with NaN or Infinite values`() {
    val vectorNaN = MutableVec2(Double.NaN, 2.0)
    val vectorInfinite = MutableVec2(Double.POSITIVE_INFINITY, 2.0)
    val vectorMixed = MutableVec2(1.0, Double.NEGATIVE_INFINITY)

    vectorNaN.isFinite() shouldBe false
    vectorInfinite.isFinite() shouldBe false
    vectorMixed.isFinite() shouldBe false
  }

  @Test
  fun `zero returns the zero vector`() {
    MutableVec2.zero().eq(MutableVec2(0.0, 0.0)) shouldBe true
  }

  @Test
  fun `toImmutable creates an immutable copy of the vector`() {
    val original = MutableVec2(7.0, 8.0)
    val copy = original.toImmutable()
    original.eq(copy)
  }

  @Test
  fun `toMutable creates a mutable copy of the vector`() {
    val original = MutableVec2(7.0, 8.0)
    val mutableCopy = original.toMutable()
    mutableCopy.eq(original) shouldBe true
    (mutableCopy === original) shouldBe false
  }

  @Test
  fun `positiveInfinity returns the correct vector`() {
    val infVector = MutableVec2.positiveInfinity()
    infVector.x shouldBe Double.POSITIVE_INFINITY
    infVector.y shouldBe Double.POSITIVE_INFINITY
  }

  @Test
  fun `negativeInfinity returns the correct vector`() {
    val negInfVector = MutableVec2.negativeInfinity()
    negInfVector.x shouldBe Double.NEGATIVE_INFINITY
    negInfVector.y shouldBe Double.NEGATIVE_INFINITY
  }

  @Test
  fun `zero vector properties are correct`() {
    val zeroVector = MutableVec2.zero()
    zeroVector.x shouldBe 0.0
    zeroVector.y shouldBe 0.0

    // second zero creates a new instance
    (zeroVector === MutableVec2.zero()) shouldBe false
  }

  @Test
  fun `zero on instance returns zero vector`() {
    val vec = MutableVec2(3.0, 4.0)
    val zeroVec = vec.zero()
    zeroVec.eq(MutableVec2(0.0, 0.0)) shouldBe true
    (vec === zeroVec) shouldBe false
  }
}
