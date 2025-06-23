package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.AngleUnit
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class Vec1Test {

  @Test
  fun `default constructor initializes to zero`() {
    val vector = Vec2()
    vector.x shouldBe 0.0F
    vector.y shouldBe 0.0F
  }

  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = Vec2(1.0, 2.0)
    vector.x shouldBe 1.0F
    vector.y shouldBe 2.0F
  }

  @Test
  fun `copy constructor creates an identical vector`() {
    val original = Vec2(3.0, 4.0)
    val copy = Vec2(original)
    original shouldBe copy
  }

  @Test
  fun `dimensions is one`() {
    val vec1 = Vec1(3.0)

    val dimensions = vec1.dimensions()

    dimensions shouldBe 1
  }

  private val plusTestCases =
      listOf(
          Triple(Vec1(1.0), Vec1(2.0), Vec1(3.0)),
          Triple(Vec1(-1.0), Vec1(-2.0), Vec1(-3.0)),
          Triple(Vec1(0.0), Vec1(0.0), Vec1(0.0)),
          Triple(Vec1(1.5), Vec1(2.5), Vec1(4.0)),
          Triple(Vec1(-1.5), Vec1(2.5), Vec1(1.0)),
      )

  @Test
  fun `vector a plus vector b`() {
    for ((a, b, expected) in plusTestCases) {
      val actual = a + b
      actual shouldBe expected
    }
  }

  @Test
  fun `vector is NaN when x is NaN`() {
    val vec1 = Vec1(Double.NaN)
    vec1.isNaN() shouldBe true
  }

  @Test
  fun `vector is not NaN when x is a number`() {
    val vec1 = Vec1(3.0)
    vec1.isNaN() shouldBe false
  }

  @Test
  fun `vector is finite when x is finite`() {
    val vec1 = Vec1(3.0)
    vec1.isFinite() shouldBe true
  }

  @Test
  fun `vector is not finite when x is infinite`() {
    val vec1 = Vec1(Double.POSITIVE_INFINITY)
    vec1.isFinite() shouldBe false
  }

  @Test
  fun `vector is infinite when x is infinite`() {
    val vec1 = Vec1(Double.POSITIVE_INFINITY)
    vec1.isInfinite() shouldBe true
  }

  @Test
  fun `vector is not infinite when x is finite`() {
    val vec1 = Vec1(3.0)
    vec1.isInfinite() shouldBe false
  }

  private val distanceTestCases =
      listOf(
          Triple(Vec1(0.0), Vec1(0.0), 0.0),
          Triple(Vec1(1.0), Vec1(2.0), 1.0),
          Triple(Vec1(-1.0), Vec1(1.0), 2.0),
          Triple(Vec1(3.0), Vec1(3.0), 0.0),
          Triple(Vec1(5.0), Vec1(-5.0), 10.0),
      )

  @Test
  fun `distance between two Vec1`() {
    for ((a, b, expected) in distanceTestCases) {
      val actual = a.distance(b)
      actual shouldBe expected
    }
  }

  private val minusTestCases =
      listOf(
          Triple(Vec1(3.0), Vec1(2.0), Vec1(1.0)),
          Triple(Vec1(2.0), Vec1(3.0), Vec1(-1.0)),
          Triple(Vec1(0.0), Vec1(0.0), Vec1(0.0)),
          Triple(Vec1(-2.0), Vec1(-3.0), Vec1(1.0)),
          Triple(Vec1(-3.0), Vec1(-2.0), Vec1(-1.0)),
      )

  @Test
  fun `vector a minus vector b`() {
    for ((a, b, expected) in minusTestCases) {
      val actual = a - b
      actual shouldBe expected
    }
  }

  @Test
  fun `hash code of NaN should be consistent`() {
    val vec1 = Vec1(Double.NaN)
    val hashCode1 = vec1.hashCode()
    val hashCode2 = vec1.hashCode()
    hashCode1 shouldBe hashCode2
  }

  @Test
  fun `normalize of positive x value`() {
    val vec1 = Vec1(3.0)
    val normalized = vec1.normalize()
    normalized shouldBe Vec1(1.0)
  }

  @Test
  fun `normalize of negative x value`() {
    val vec1 = Vec1(-3.0)
    val normalized = vec1.normalize()
    normalized shouldBe Vec1(-1.0)
  }

  @Test
  fun `normalize of zero x value throws exception`() {
    val vec1 = Vec1(0.0)
    shouldThrow<ArithmeticException> { vec1.normalize() }
  }

  @Test
  fun `negate positive x value`() {
    val vec1 = Vec1(3.0)
    val actual = -vec1
    actual shouldBe Vec1(-3.0)
  }

  @Test
  fun `negate negative x value`() {
    val vec1 = Vec1(-3.0)
    val actual = -vec1
    actual shouldBe Vec1(3.0)
  }

  @Test
  fun `negate NaN x value`() {
    val vec1 = Vec1(Double.NaN)
    val actual = -vec1
    actual shouldBe Vec1(Double.NaN)
  }

  private val dotProductTestCases =
      listOf(
          Triple(Vec1(2.0), Vec1(3.0), 6.0),
          Triple(Vec1(-2.0), Vec1(3.0), -6.0),
          Triple(Vec1(0.0), Vec1(3.0), 0.0),
          Triple(Vec1(2.5), Vec1(4.0), 10.0),
          Triple(Vec1(-2.5), Vec1(-4.0), 10.0),
      )

  @Test
  fun `dot product of two Vec1`() {
    for ((a, b, expected) in dotProductTestCases) {
      val actual = a dot b
      actual shouldBe expected
    }
  }

  private val timesTestCases =
      listOf(
          Triple(Vec1(2.0), 3.0, Vec1(6.0)),
          Triple(Vec1(-2.0), 3.0, Vec1(-6.0)),
          Triple(Vec1(0.0), 3.0, Vec1(0.0)),
          Triple(Vec1(2.5), 4.0, Vec1(10.0)),
          Triple(Vec1(-2.5), -4.0, Vec1(10.0)),
      )

  @Test
  fun `vector times scalar`() {
    for ((vec, scalar, expected) in timesTestCases) {
      val actual = vec * scalar
      actual shouldBe expected
    }
  }

  @Test
  fun `scalar times vector`() {
    for ((vec, scalar, expected) in timesTestCases) {
      val actual = scalar * vec
      actual shouldBe expected
    }
  }

  @Test
  fun `eq returns true for equal Vec1`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(1.0)
    vec1.eq(vec2) shouldBe true
  }

  @Test
  fun `eq returns false for different Vec1`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(2.0)
    vec1.eq(vec2) shouldBe false
  }

  @Test
  fun `eq returns true for approximate equality`() {
    val vec1 = Vec1(0.3)
    val vec2 = Vec1(0.1 + 0.2)
    vec1.eq(vec2) shouldBe true
  }

  @Test
  fun `eq returns false for non-approximate equality`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(1.000000001)
    vec1.eq(vec2) shouldBe false
  }

  @Test
  fun `angle between two vectors`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(2.0)

    vec1.angle(vec2, AngleUnit.RADIANS) shouldBe PI
    vec1.angle(vec2, AngleUnit.DEGREES) shouldBe 180.0
  }

  @Test
  fun `angle throws exception for NaN values`() {
    val vec1 = Vec1(Double.NaN)
    val vec2 = Vec1(2.0)

    shouldThrow<IllegalArgumentException> { vec1.angle(vec2, AngleUnit.RADIANS) }
  }

  @Test
  fun `norm of Vec1`() {
    val vec1 = Vec1(3.0)
    val actual = vec1.norm()
    actual shouldBe 9.0
  }

  @Test
  fun `compute the correct lerp`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 0.5

    val actual = vec1.lerp(vec2, t)

    actual shouldBe Vec1(2.0)
  }

  @Test
  fun `lerp with t=0 returns the first vector`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 0.0

    val actual = vec1.lerp(vec2, t)

    actual shouldBe vec1
  }

  @Test
  fun `lerp with t=1 returns the second vector`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 1.0

    val actual = vec1.lerp(vec2, t)

    actual shouldBe vec2
  }

  @Test
  fun `toString returns correct format`() {
    val vec1 = Vec1(3.1)
    val actual = vec1.toString()
    actual shouldBe "Vec1(x=3.1)"
  }
}
