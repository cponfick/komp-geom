package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.AngleUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class Vec1Test {

  @Test
  fun `constructor with parameter initializes correctly`() {
    val vector = Vec1(5.0)
    vector.x shouldBe 5.0
  }

  @Test
  fun `addition of two vectors`() {
    for ((a, b, expected) in plusTestCases) {
      val actual = a + b
      actual shouldBe expected
    }
  }

  @Test
  fun `subtraction of two vectors`() {
    for ((a, b, expected) in minusTestCases) {
      val actual = a - b
      actual shouldBe expected
    }
  }

  @Test
  fun `scalar multiplication`() {
    for ((vec, scalar, expected) in scalarMultiplicationTestCases) {
      val actual = vec * scalar
      actual shouldBe expected
    }
  }

  @Test
  fun `dot product of two vectors`() {
    for ((a, b, expected) in dotProductTestCases) {
      val actual = a dot b
      actual shouldBe expected
    }
  }

  @Test
  fun `negation of vector`() {
    val positiveVec = Vec1(3.0)
    val negativeVec = Vec1(-3.0)

    -positiveVec shouldBe Vec1(-3.0)
    -negativeVec shouldBe Vec1(3.0)
  }

  @Test
  fun `isNaN returns true when x is NaN`() {
    val vec = Vec1(Double.NaN)
    vec.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns false when x is a number`() {
    val vec = Vec1(3.0)
    vec.isNaN() shouldBe false
  }

  @Test
  fun `isFinite returns true when x is finite`() {
    val vec = Vec1(3.0)
    vec.isFinite() shouldBe true
  }

  @Test
  fun `isFinite returns false when x is infinite`() {
    val vec = Vec1(Double.POSITIVE_INFINITY)
    vec.isFinite() shouldBe false
  }

  @Test
  fun `isInfinite returns true when x is infinite`() {
    val vec = Vec1(Double.POSITIVE_INFINITY)
    vec.isInfinite() shouldBe true
  }

  @Test
  fun `isInfinite returns false when x is finite`() {
    val vec = Vec1(3.0)
    vec.isInfinite() shouldBe false
  }

  @Test
  fun `distance between two vectors`() {
    for ((a, b, expected) in distanceTestCases) {
      val actual = a.distance(b)
      actual shouldBe expected
    }
  }

  @Test
  fun `norm calculation`() {
    val vec = Vec1(3.0)
    val actual = vec.norm()
    actual shouldBe 3.0
  }

  @Test
  fun `normalize positive value`() {
    val vec = Vec1(3.0)
    val normalized = vec.normalize()
    normalized shouldBe Vec1(1.0)
  }

  @Test
  fun `normalize negative value`() {
    val vec = Vec1(-3.0)
    val normalized = vec.normalize()
    normalized shouldBe Vec1(-1.0)
  }

  @Test
  fun `normalize zero value throws exception`() {
    val vec = Vec1(0.0)
    shouldThrow<ArithmeticException> { vec.normalize() }
  }

  @Test
  fun `eq returns true for equal vectors`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(1.0)
    vec1.eq(vec2) shouldBe true
  }

  @Test
  fun `eq returns false for different vectors`() {
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
  fun `hashCode consistency for NaN values`() {
    val vec = Vec1(Double.NaN)
    val hashCode1 = vec.hashCode()
    val hashCode2 = vec.hashCode()
    hashCode1 shouldBe hashCode2
  }

  @Test
  fun `angle between two vectors in radians and degrees`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(2.0)

    vec1.angle(vec2, AngleUnit.RADIANS) shouldBe PI
    vec1.angle(vec2, AngleUnit.DEGREES) shouldBe 180.0
  }

  @Test
  fun `angle calculation throws exception for NaN values`() {
    val vec1 = Vec1(Double.NaN)
    val vec2 = Vec1(2.0)

    shouldThrow<IllegalArgumentException> { vec1.angle(vec2, AngleUnit.RADIANS) }
  }

  @Test
  fun `lerp computes correct interpolation`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 0.5

    val actual = vec1.lerp(vec2, t)
    actual shouldBe Vec1(2.0)
  }

  @Test
  fun `lerp with t=0 returns first vector`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 0.0

    val actual = vec1.lerp(vec2, t)
    actual shouldBe vec1
  }

  @Test
  fun `lerp with t=1 returns second vector`() {
    val vec1 = Vec1(1.0)
    val vec2 = Vec1(3.0)
    val t = 1.0

    val actual = vec1.lerp(vec2, t)
    actual shouldBe vec2
  }

  @Test
  fun `toString returns correct format`() {
    val vec = Vec1(3.1)
    val actual = vec.toString()
    actual shouldBe "Vec1(x=3.1)"
  }

  companion object {
    private val plusTestCases =
      listOf(
        Triple(Vec1(1.0), Vec1(2.0), Vec1(3.0)),
        Triple(Vec1(-1.0), Vec1(-2.0), Vec1(-3.0)),
        Triple(Vec1(0.0), Vec1(0.0), Vec1(0.0)),
        Triple(Vec1(1.5), Vec1(2.5), Vec1(4.0)),
        Triple(Vec1(-1.5), Vec1(2.5), Vec1(1.0)),
      )

    private val minusTestCases =
      listOf(
        Triple(Vec1(3.0), Vec1(2.0), Vec1(1.0)),
        Triple(Vec1(2.0), Vec1(3.0), Vec1(-1.0)),
        Triple(Vec1(0.0), Vec1(0.0), Vec1(0.0)),
        Triple(Vec1(-2.0), Vec1(-3.0), Vec1(1.0)),
        Triple(Vec1(-3.0), Vec1(-2.0), Vec1(-1.0)),
      )

    private val distanceTestCases =
      listOf(
        Triple(Vec1(0.0), Vec1(0.0), 0.0),
        Triple(Vec1(1.0), Vec1(2.0), 1.0),
        Triple(Vec1(-1.0), Vec1(1.0), 2.0),
        Triple(Vec1(3.0), Vec1(3.0), 0.0),
        Triple(Vec1(5.0), Vec1(-5.0), 10.0),
      )

    private val dotProductTestCases =
      listOf(
        Triple(Vec1(2.0), Vec1(3.0), 6.0),
        Triple(Vec1(-2.0), Vec1(3.0), -6.0),
        Triple(Vec1(0.0), Vec1(3.0), 0.0),
        Triple(Vec1(2.5), Vec1(4.0), 10.0),
        Triple(Vec1(-2.5), Vec1(-4.0), 10.0),
      )

    private val scalarMultiplicationTestCases =
      listOf(
        Triple(Vec1(2.0), 3.0, Vec1(6.0)),
        Triple(Vec1(-2.0), 3.0, Vec1(-6.0)),
        Triple(Vec1(0.0), 3.0, Vec1(0.0)),
        Triple(Vec1(2.5), 4.0, Vec1(10.0)),
        Triple(Vec1(-2.5), -4.0, Vec1(10.0)),
      )
  }
}
