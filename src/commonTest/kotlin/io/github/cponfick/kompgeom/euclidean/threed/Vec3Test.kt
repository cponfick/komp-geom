package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.AngleUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class Vec3Test {

  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    vector.x shouldBe 1.0
    vector.y shouldBe 2.0
    vector.z shouldBe 3.0
  }

  @Test
  fun `copy constructor creates an identical vector`() {
    val original = Vec3(3.0, 4.0, 1.0)
    original shouldBe Vec3(original)
  }

  @Test
  fun `dimensions returns 3`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    vector.dimensions() shouldBe 3
  }

  @Test
  fun `toString returns correct format`() {
    val vector = Vec3(1.1, 2.2, 3.3)
    vector.toString() shouldBe "Vec3(x=1.1, y=2.2, z=3.3)"
  }

  @Test
  fun `minus operator returns correct vector`() {
    val actual = Vec3(5.0, 6.0, 7.0) - Vec3(2.0, 3.0, 4.0)
    actual shouldBe Vec3(3.0, 3.0, 3.0)
  }

  @Test
  fun `unary minus operator returns correct vector`() {
    val actual = -Vec3(1.0, 2.0, 3.0)
    actual shouldBe Vec3(-1.0, -2.0, -3.0)
  }

  @Test
  fun `times operator returns correct vector`() {
    val actual = Vec3(1.0, 2.0, 3.0) * 2.0
    actual shouldBe Vec3(2.0, 4.0, 6.0)
  }

  @Test
  fun `plus operator returns correct vector`() {
    val actual = Vec3(1.0, 2.0, 3.0) + Vec3(4.0, 5.0, 6.0)
    actual shouldBe Vec3(5.0, 7.0, 9.0)
  }

  @Test
  fun `eq method checks equality with same values`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(1.0, 2.0, 3.0)
    vector1.eq(vector2) shouldBe true
  }

  @Test
  fun `eq method checks equality with different values`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(4.0, 5.0, 6.0)
    vector1.eq(vector2) shouldBe false
  }

  private val approxEqTestCases = listOf(Pair(Vec3(0.3, 0.2, 0.3), Vec3(0.1 + 0.2, 0.2, 0.3)))

  @Test
  fun `eq method checks approximate equality`() {
    for ((a, b) in approxEqTestCases) {
      a.eq(b) shouldBe true
    }
  }

  @Test
  fun `eq method checks non-approximate equality`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(1.000000001, 2.0, 3.0)
    vector1.eq(vector2) shouldBe false
  }

  @Test
  fun `dot product returns correct value`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(4.0, 5.0, 6.0)
    val dotProduct = vector1 dot vector2
    dotProduct shouldBe 32.0
  }

  private val isFiniteTestCases =
    listOf(
      Vec3(1.0, 2.0, 3.0) to true,
      Vec3(Double.POSITIVE_INFINITY, 2.0, 3.0) to false,
      Vec3(Double.NaN, 2.0, 3.0) to false,
      Vec3(1.0, Double.POSITIVE_INFINITY, 3.0) to false,
      Vec3(1.0, 2.0, Double.POSITIVE_INFINITY) to false,
      Vec3(Double.NaN, Double.NaN, Double.NaN) to false,
    )

  @Test
  fun `isFinite method returns expected actual`() {
    for ((vector, expected) in isFiniteTestCases) {
      vector.isFinite() shouldBe expected
    }
  }

  private val isInfiniteTestCases =
    listOf(
      Vec3(1.0, 2.0, 3.0) to false,
      Vec3(Double.POSITIVE_INFINITY, 2.0, 3.0) to true,
      Vec3(Double.NaN, 2.0, 3.0) to false,
      Vec3(1.0, Double.POSITIVE_INFINITY, 3.0) to true,
      Vec3(1.0, 2.0, Double.POSITIVE_INFINITY) to true,
      Vec3(Double.NaN, Double.NaN, Double.NaN) to false,
    )

  @Test
  fun `isInfinite method returns expected actual`() {
    for ((vector, expected) in isInfiniteTestCases) {
      vector.isInfinite() shouldBe expected
    }
  }

  private val isNaNTestCases =
    listOf(
      Vec3(1.0, 2.0, 3.0) to false,
      Vec3(Double.NaN, 2.0, 3.0) to true,
      Vec3(1.0, Double.NaN, 3.0) to true,
      Vec3(1.0, 2.0, Double.NaN) to true,
      Vec3(Double.NaN, Double.NaN, Double.NaN) to true,
    )

  @Test
  fun `isNaN method returns expected actual`() {
    for ((vector, expected) in isNaNTestCases) {
      vector.isNaN() shouldBe expected
    }
  }

  @Test
  fun `norm returns correct value`() {
    val vector = Vec3(3.0, 4.0, 10.0)
    val normValue = vector.norm()
    normValue shouldBe 11.180339887498949
  }

  @Test
  fun `lerp returns correct vector`() {
    val start = Vec3(1.0, 2.0, 3.0)
    val end = Vec3(4.0, 5.0, 6.0)
    val expected = Vec3(2.5, 3.5, 4.5)
    val actual = start.lerp(end, 0.5)
    actual shouldBe expected
  }

  @Test
  fun `lerp with t 0 returns start vector`() {
    val start = Vec3(1.0, 2.0, 3.0)
    val end = Vec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 0.0)
    actual shouldBe start
  }

  @Test
  fun `lerp with t 1 returns end vector`() {
    val start = Vec3(1.0, 2.0, 3.0)
    val end = Vec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 1.0)
    actual shouldBe end
  }

  @Test
  fun `lerp with t less than 0 returns extrapolated vector`() {
    val start = Vec3(1.0, 2.0, 3.0)
    val end = Vec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, -0.5)
    actual shouldBe Vec3(-0.5, 0.5, 1.5)
  }

  @Test
  fun `lerp with t greater than 1 returns extrapolated vector`() {
    val start = Vec3(1.0, 2.0, 3.0)
    val end = Vec3(4.0, 5.0, 6.0)
    val actual = start.lerp(end, 1.5)
    actual shouldBe Vec3(5.5, 6.5, 7.5)
  }

  @Test
  fun `normalize returns correct vector`() {
    val vector = Vec3(3.0, 4.0, 5.0)
    val actual = vector.normalize()
    actual shouldBe Vec3(0.4242640687119285, 0.565685424949238, 0.7071067811865475)
  }

  @Test
  fun `normalize throws exception for zero vector`() {
    shouldThrow<ArithmeticException> { Vec3.ZERO.normalize() }
  }

  @Test
  fun `distance returns correct value`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(4.0, 5.0, 6.0)
    val distance = vector1.distance(vector2)
    distance shouldBe 5.196152422706632
  }

  @Test
  fun `project returns expected vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    val direction = Vec3(4.0, 5.0, 6.0)
    val actual = vector.project(direction)
    actual shouldBe Vec3(1.6623376623376624, 2.077922077922078, 2.4935064935064934)
  }

  @Test
  fun `project throws exception for zero vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.project(Vec3.ZERO) }
  }

  @Test
  fun `project throws exception for NaN vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.project(Vec3.NaN) }
  }

  @Test
  fun `project throws exception for infinite vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> {
      vector.project(
        Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
      )
    }
  }

  @Test
  fun `reject returns expected vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    val direction = Vec3(4.0, 5.0, 6.0)
    val actual = vector.reject(direction)
    actual shouldBe Vec3(-0.6623376623376624, -0.07792207792207817, 0.5064935064935066)
  }

  @Test
  fun `reject throws exception for zero vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.reject(Vec3.ZERO) }
  }

  @Test
  fun `reject throws exception for NaN vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> { vector.reject(Vec3.NaN) }
  }

  @Test
  fun `reject throws exception for infinite vector`() {
    val vector = Vec3(1.0, 2.0, 3.0)
    shouldThrow<IllegalArgumentException> {
      vector.reject(
        Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
      )
    }
  }

  @Test
  fun `cross returns correct vector`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(4.0, 5.0, 6.0)
    val actual = vector1 cross vector2
    actual shouldBe Vec3(-3.0, 6.0, -3.0)
  }

  @Test
  fun `a cross b equals -b cross a`() {
    val vector1 = Vec3(1.0, 2.0, 3.0)
    val vector2 = Vec3(4.0, 5.0, 6.0)
    val cross1 = vector1 cross vector2
    val cross2 = vector2 cross vector1
    cross1 shouldBe -cross2
  }

  private val angleInRadiansTestCases =
    listOf(
      Triple(Vec3(1.0, 0.0, 0.0), Vec3(0.0, 1.0, 0.0), PI / 2),
      Triple(Vec3(1.0, 1.0, 0.0), Vec3(1.0, -1.0, 0.0), PI / 2),
      Triple(Vec3(1.0, 0.0, 0.0), Vec3(-1.0, 0.0, 0.0), PI),
      Triple(Vec3(1.0, 2.0, 3.0), Vec3(4.0, 5.0, 6.0), 0.2257261285527342),
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
      Triple(Vec3(1.0, 0.0, 0.0), Vec3(0.0, 1.0, 0.0), 90.0),
      Triple(Vec3(1.0, 1.0, 0.0), Vec3(1.0, -1.0, 0.0), 90.0),
      Triple(Vec3(1.0, 0.0, 0.0), Vec3(-1.0, 0.0, 0.0), 180.0),
      Triple(Vec3(1.0, 2.0, 3.0), Vec3(4.0, 5.0, 6.0), 12.933154491899135),
    )

  @Test
  fun `angle method calculates angle between two vectors in degrees`() {
    for ((vector1, vector2, expected) in angleInDegreesTestCases) {
      val actual = vector1.angle(vector2, AngleUnit.DEGREES)
      actual shouldBe expected
    }
  }

  @Test
  fun `create unit vector from vec2`() {
    val vector = Vec3(10.0, 0.0, 0.0)
    val unitVector = Vec3.Unit.from(vector)
    unitVector shouldBe Vec3.Unit(1.0, 0.0, 0.0)
  }

  @Test
  fun `create unit vector from coordinates`() {
    val unitVector = Vec3.Unit.from(10.0, 0.0, 0.0)
    unitVector shouldBe Vec3.Unit(1.0, 0.0, 0.0)
  }

  @Test
  fun `create unit vector throws exception for zero vector`() {
    shouldThrow<ArithmeticException> { Vec3.Unit.from(0.0, 0.0, 0.0) }
  }

  @Test
  fun `unit vector norm is always 1`() {
    val unitVector = Vec3.Unit(1.0, 0.0, 0.0)
    unitVector.norm() shouldBe 1.0
  }

  @Test
  fun `unit vector normalize returns itself`() {
    val unitVector = Vec3.Unit(1.0, 0.0, 0.0)
    unitVector.normalize() shouldBe unitVector
  }

  @Test
  fun `unit vector unary minus returns correct vector`() {
    val unitVector = Vec3.Unit(1.0, 0.0, 0.0)
    val negatedVector = -unitVector
    negatedVector.x shouldBe -1.0
    negatedVector.y shouldBe -0.0
    negatedVector.z shouldBe -0.0
  }
}
