package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.AngleUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class MutableVec3Test {
  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    vector.x shouldBe 1.0
    vector.y shouldBe 2.0
    vector.z shouldBe 3.0
  }

  @Test
  fun `toString returns correct format`() {
    val vector = MutableVec3(1.1, 2.2, 3.3)
    vector.toString() shouldBe "MutableVec3(x=1.1, y=2.2, z=3.3)"
  }

  @Test
  fun `minus operator returns correct vector`() {
    val actual = MutableVec3(5.0, 6.0, 7.0) - MutableVec3(2.0, 3.0, 4.0)
    actual.eq(MutableVec3(3.0, 3.0, 3.0)) shouldBe true
  }

  @Test
  fun `unary minus operator returns correct vector`() {
    val actual = -MutableVec3(1.0, 2.0, 3.0)
    actual.eq(MutableVec3(-1.0, -2.0, -3.0)) shouldBe true
  }

  @Test
  fun `times operator returns correct vector`() {
    val actual = MutableVec3(1.0, 2.0, 3.0) * 2.0
    actual.eq(MutableVec3(2.0, 4.0, 6.0)) shouldBe true
  }

  @Test
  fun `plus operator returns correct vector`() {
    val actual = MutableVec3(1.0, 2.0, 3.0) + MutableVec3(4.0, 5.0, 6.0)
    actual.eq(MutableVec3(5.0, 7.0, 9.0)) shouldBe true
  }

  @Test
  fun `eq method checks equality with same values`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(1.0, 2.0, 3.0)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq method checks equality with different values`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(4.0, 5.0, 6.0)
    vector1.eq(vector2) shouldBe false
  }

  private val approxEqTestCases =
    listOf(Pair(MutableVec3(0.3, 0.2, 0.3), MutableVec3(0.1 + 0.2, 0.2, 0.3)))

  @Test
  fun `eq method checks approximate equality`() {
    for ((a, b) in approxEqTestCases) {
      a.eq(b) shouldBe true
    }
  }

  @Test
  fun `eq method checks non-approximate equality`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(1.000000001, 2.0, 3.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `dot product returns correct value`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(4.0, 5.0, 6.0)
    val dotProduct = vector1 dot vector2
    dotProduct shouldBe 32.0
  }

  private val isFiniteTestCases =
    listOf(
      MutableVec3(1.0, 2.0, 3.0) to true,
      MutableVec3(Double.POSITIVE_INFINITY, 2.0, 3.0) to false,
      MutableVec3(Double.NaN, 2.0, 3.0) to false,
      MutableVec3(1.0, Double.POSITIVE_INFINITY, 3.0) to false,
      MutableVec3(1.0, 2.0, Double.POSITIVE_INFINITY) to false,
      MutableVec3(Double.NaN, Double.NaN, Double.NaN) to false,
    )

  @Test
  fun `isFinite method returns expected actual`() {
    for ((vector, expected) in isFiniteTestCases) {
      vector.isFinite() shouldBe expected
    }
  }

  private val isInfiniteTestCases =
    listOf(
      MutableVec3(1.0, 2.0, 3.0) to false,
      MutableVec3(Double.POSITIVE_INFINITY, 2.0, 3.0) to true,
      MutableVec3(Double.NaN, 2.0, 3.0) to false,
      MutableVec3(1.0, Double.POSITIVE_INFINITY, 3.0) to true,
      MutableVec3(1.0, 2.0, Double.POSITIVE_INFINITY) to true,
      MutableVec3(Double.NaN, Double.NaN, Double.NaN) to false,
    )

  @Test
  fun `isInfinite method returns expected actual`() {
    for ((vector, expected) in isInfiniteTestCases) {
      vector.isInfinite() shouldBe expected
    }
  }

  private val isNaNTestCases =
    listOf(
      MutableVec3(1.0, 2.0, 3.0) to false,
      MutableVec3(Double.NaN, 2.0, 3.0) to true,
      MutableVec3(1.0, Double.NaN, 3.0) to true,
      MutableVec3(1.0, 2.0, Double.NaN) to true,
      MutableVec3(Double.NaN, Double.NaN, Double.NaN) to true,
    )

  @Test
  fun `isNaN method returns expected actual`() {
    for ((vector, expected) in isNaNTestCases) {
      vector.isNaN() shouldBe expected
    }
  }

  @Test
  fun `norm returns correct value`() {
    val vector = MutableVec3(3.0, 4.0, 10.0)
    val normValue = vector.norm()
    normValue shouldBe 11.180339887498949
  }

  @Test
  fun `lerp returns correct vector`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val expected = MutableVec3(2.5, 3.5, 4.5)
    val actual = start.lerp(end, 0.5)
    actual.eq(expected) shouldBe true
  }

  @Test
  fun `lerp with t 0 returns start vector`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 0.0)
    actual shouldBe start
  }

  @Test
  fun `lerp with t 1 returns end vector`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 1.0)
    actual.eq(end) shouldBe true
  }

  @Test
  fun `lerp with t less than 0 returns extrapolated vector`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, -0.5)
    actual.eq(MutableVec3(-0.5, 0.5, 1.5)) shouldBe true
  }

  @Test
  fun `lerp with t greater than 1 returns extrapolated vector`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 1.5)
    actual.eq(MutableVec3(5.5, 6.5, 7.5)) shouldBe true
  }

  @Test
  fun `normalize returns correct vector`() {
    val vector = MutableVec3(3.0, 4.0, 5.0)
    val actual = vector.normalize()
    actual.eq(MutableVec3(0.4242640687119285, 0.565685424949238, 0.7071067811865475)) shouldBe true
  }

  @Test
  fun `normalize throws exception for zero vector`() {
    shouldThrow<ArithmeticException> { MutableVec3.zero().normalize() }
  }

  @Test
  fun `distance returns correct value`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(4.0, 5.0, 6.0)
    val distance = vector1.distance(vector2)
    distance shouldBe 5.196152422706632
  }

  @Test
  fun `project returns expected vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    val direction = MutableVec3(4.0, 5.0, 6.0)
    val actual = vector.project(direction)
    actual.eq(MutableVec3(1.6623376623376624, 2.077922077922078, 2.4935064935064934)) shouldBe true
  }

  @Test
  fun `project throws exception for zero vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.project(MutableVec3.zero()) }
  }

  @Test
  fun `project throws exception for NaN vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.project(MutableVec3.nan()) }
  }

  @Test
  fun `project throws exception for infinite vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.project(MutableVec3.positiveInfinity()) }
  }

  @Test
  fun `reject returns expected vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    val direction = MutableVec3(4.0, 5.0, 6.0)
    val actual = vector.reject(direction)
    actual.eq(MutableVec3(-0.6623376623376624, -0.07792207792207817, 0.5064935064935066)) shouldBe
      true
  }

  @Test
  fun `reject throws exception for zero vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.reject(MutableVec3.zero()) }
  }

  @Test
  fun `reject throws exception for NaN vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.reject(MutableVec3.nan()) }
  }

  @Test
  fun `reject throws exception for infinite vector`() {
    val vector = MutableVec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.reject(MutableVec3.positiveInfinity()) }
  }

  @Test
  fun `cross returns correct vector`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(4.0, 5.0, 6.0)
    val actual = vector1 cross vector2
    actual.eq(MutableVec3(-3.0, 6.0, -3.0))
  }

  @Test
  fun `a cross b equals -b cross a`() {
    val vector1 = MutableVec3(1.0, 2.0, 3.0)
    val vector2 = MutableVec3(4.0, 5.0, 6.0)
    val vector3 = MutableVec3(1.0, 2.0, 3.0)
    val vector4 = MutableVec3(4.0, 5.0, 6.0)
    val cross1 = vector1 cross vector2
    val cross2 = vector4 cross vector3
    cross1.eq(-cross2)
  }

  private val angleInRadiansTestCases =
    listOf(
      Triple(MutableVec3(1.0, 0.0, 0.0), MutableVec3(0.0, 1.0, 0.0), PI / 2),
      Triple(MutableVec3(1.0, 1.0, 0.0), MutableVec3(1.0, -1.0, 0.0), PI / 2),
      Triple(MutableVec3(1.0, 0.0, 0.0), MutableVec3(-1.0, 0.0, 0.0), PI),
      Triple(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0), 0.2257261285527342),
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
      Triple(MutableVec3(1.0, 0.0, 0.0), MutableVec3(0.0, 1.0, 0.0), 90.0),
      Triple(MutableVec3(1.0, 1.0, 0.0), MutableVec3(1.0, -1.0, 0.0), 90.0),
      Triple(MutableVec3(1.0, 0.0, 0.0), MutableVec3(-1.0, 0.0, 0.0), 180.0),
      Triple(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0), 12.933154491899135),
    )

  @Test
  fun `angle method calculates angle between two vectors in degrees`() {
    for ((vector1, vector2, expected) in angleInDegreesTestCases) {
      val actual = vector1.angle(vector2, AngleUnit.DEGREES)
      actual shouldBe expected
    }
  }

  @Test
  fun `toVec3 creates a identical copy of the vector`() {
    val original = MutableVec3(7.0, 8.0, 9.0)
    val copy = original.toVec3()
    original.eq(copy)
  }

  @Test
  fun `toMutableVec3 creates a mutable copy of the vector`() {
    val original = MutableVec3(7.0, 8.0, 9.0)
    val mutableCopy = original.toMutableVec3()
    mutableCopy.eq(original)
    (mutableCopy === original) shouldBe false
  }

  @Test
  fun `negativeInfinity returns correct vector`() {
    val negInfVector = MutableVec3.negativeInfinity()
    negInfVector.x shouldBe Double.NEGATIVE_INFINITY
    negInfVector.y shouldBe Double.NEGATIVE_INFINITY
    negInfVector.z shouldBe Double.NEGATIVE_INFINITY
  }

  @Test
  fun `zero on instance returns correct vector`() {
    val vec = MutableVec3(5.0, -3.0, 2.0)
    val zeroVector = vec.zero()
    zeroVector.x shouldBe 0.0
    zeroVector.y shouldBe 0.0
    zeroVector.z shouldBe 0.0
    (vec === zeroVector) shouldBe false
  }
}
