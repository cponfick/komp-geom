package io.github.cponfick.kompgeom.euclidean.oned

import io.github.cponfick.kompgeom.core.AngleUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI
import kotlin.test.Test

class MutableVec1Test {
  @Test
  fun `constructor with parameter initializes correctly`() {
    val vector = MutableVec1(5.0)
    vector.x shouldBe 5.0
  }

  @Test
  fun `addition of two vectors`() {
    for ((a, b, expected) in plusTestCases) {
      val actual = a + b
      actual.eq(expected) shouldBe true
    }
  }

  @Test
  fun `subtraction of two vectors`() {
    for ((a, b, expected) in minusTestCases) {
      val actual = a - b
      actual.eq(expected) shouldBe true
    }
  }

  @Test
  fun `scalar multiplication`() {
    for ((vec, scalar, expected) in scalarMultiplicationTestCases) {
      val actual = vec * scalar
      actual.eq(expected) shouldBe true
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
    val positiveVec = MutableVec1(3.0)
    val negativeVec = MutableVec1(-3.0)

    (-positiveVec).eq(MutableVec1(-3.0)) shouldBe true
    (-negativeVec).eq(MutableVec1(3.0)) shouldBe true
  }

  @Test
  fun `isNaN returns true when x is NaN`() {
    val vec = MutableVec1(Double.NaN)
    vec.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns false when x is a number`() {
    val vec = MutableVec1(3.0)
    vec.isNaN() shouldBe false
  }

  @Test
  fun `isFinite returns true when x is finite`() {
    val vec = MutableVec1(3.0)
    vec.isFinite() shouldBe true
  }

  @Test
  fun `isFinite returns false when x is infinite`() {
    val vec = MutableVec1(Double.POSITIVE_INFINITY)
    vec.isFinite() shouldBe false
  }

  @Test
  fun `isInfinite returns true when x is infinite`() {
    val vec = MutableVec1(Double.POSITIVE_INFINITY)
    vec.isInfinite() shouldBe true
  }

  @Test
  fun `isInfinite returns false when x is finite`() {
    val vec = MutableVec1(3.0)
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
    val vec = MutableVec1(3.0)
    val actual = vec.norm()
    actual shouldBe 3.0
  }

  @Test
  fun `normalize positive value`() {
    val vec = MutableVec1(3.0)
    val normalized = vec.normalize()
    normalized.eq(MutableVec1(1.0))
  }

  @Test
  fun `normalize negative value`() {
    val vec = MutableVec1(-3.0)
    val normalized = vec.normalize()
    normalized.eq(MutableVec1(-1.0))
  }

  @Test
  fun `normalize zero value throws exception`() {
    val vec = MutableVec1(0.0)
    shouldThrow<ArithmeticException> { vec.normalize() }
  }

  @Test
  fun `eq returns true for equal vectors`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(1.0)
    vec1.eq(vec2) shouldBe true
  }

  @Test
  fun `eq returns false for different vectors`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(2.0)
    vec1.eq(vec2) shouldBe false
  }

  @Test
  fun `eq returns true for approximate equality`() {
    val vec1 = MutableVec1(0.3)
    val vec2 = MutableVec1(0.1 + 0.2)
    vec1.eq(vec2) shouldBe true
  }

  @Test
  fun `eq returns false for non-approximate equality`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(1.000000001)
    vec1.eq(vec2) shouldBe false
  }

  @Test
  fun `hashCode consistency for NaN values`() {
    val vec = MutableVec1(Double.NaN)
    val hashCode1 = vec.hashCode()
    val hashCode2 = vec.hashCode()
    hashCode1 shouldBe hashCode2
  }

  @Test
  fun `angle between two vectors in radians and degrees`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(2.0)

    vec1.angle(vec2, AngleUnit.RADIANS) shouldBe PI
    vec1.angle(vec2, AngleUnit.DEGREES) shouldBe 180.0
  }

  @Test
  fun `angle calculation throws exception for NaN values`() {
    val vec1 = MutableVec1(Double.NaN)
    val vec2 = MutableVec1(2.0)

    shouldThrow<IllegalArgumentException> { vec1.angle(vec2, AngleUnit.RADIANS) }
  }

  @Test
  fun `lerp computes correct interpolation`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(3.0)
    val t = 0.5

    val actual = vec1.lerp(vec2, t)
    actual.eq(MutableVec1(2.0)) shouldBe true
  }

  @Test
  fun `lerp with t=0 returns first vector`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(3.0)
    val t = 0.0

    val actual = vec1.lerp(vec2, t)
    actual.eq(vec1) shouldBe true
  }

  @Test
  fun `lerp with t=1 returns second vector`() {
    val vec1 = MutableVec1(1.0)
    val vec2 = MutableVec1(3.0)
    val t = 1.0

    val actual = vec1.lerp(vec2, t)
    actual.eq(vec2) shouldBe true
  }

  @Test
  fun `toString returns correct format`() {
    val vec = MutableVec1(3.1)
    val actual = vec.toString()
    actual shouldBe "MutableVec1(x=3.1)"
  }

  @Test
  fun `toVec1 returns correct Vec1`() {
    val mutableVec = MutableVec1(4.2)
    val vec = mutableVec.toVec1()
    vec.x shouldBe 4.2
  }

  @Test
  fun `toMutableVec1 returns new MutableVec1`() {
    val mutableVec = MutableVec1(5.3)
    val result = mutableVec.toMutableVec1()
    result.eq(mutableVec) shouldBe true
    result shouldNotBe mutableVec
  }

  @Test
  fun `zero vector creation`() {
    val zeroVec = MutableVec1.zero()
    zeroVec.x shouldBe 0.0
  }

  @Test
  fun `positive infinity vector creation`() {
    val posInfVec = MutableVec1.positiveInfinity()
    posInfVec.x shouldBe Double.POSITIVE_INFINITY
  }

  @Test
  fun `negative infinity vector creation`() {
    val negInfVec = MutableVec1.negativeInfinity()
    negInfVec.x shouldBe Double.NEGATIVE_INFINITY
  }

  @Test
  fun `nan vector creation`() {
    val nanVec = MutableVec1.nan()
    nanVec.x.isNaN() shouldBe true
  }

  @Test
  fun `zero on instance returns zero vector`() {
    val vec = MutableVec1(3.0)
    val zeroVec = vec.zero()
    zeroVec.x shouldBe 0.0
  }

  companion object {
    private val plusTestCases =
      listOf(
        Triple(MutableVec1(1.0), MutableVec1(2.0), MutableVec1(3.0)),
        Triple(MutableVec1(-1.0), MutableVec1(-2.0), MutableVec1(-3.0)),
        Triple(MutableVec1(0.0), MutableVec1(0.0), MutableVec1(0.0)),
        Triple(MutableVec1(1.5), MutableVec1(2.5), MutableVec1(4.0)),
        Triple(MutableVec1(-1.5), MutableVec1(2.5), MutableVec1(1.0)),
      )

    private val minusTestCases =
      listOf(
        Triple(MutableVec1(3.0), MutableVec1(2.0), MutableVec1(1.0)),
        Triple(MutableVec1(2.0), MutableVec1(3.0), MutableVec1(-1.0)),
        Triple(MutableVec1(0.0), MutableVec1(0.0), MutableVec1(0.0)),
        Triple(MutableVec1(-2.0), MutableVec1(-3.0), MutableVec1(1.0)),
        Triple(MutableVec1(-3.0), MutableVec1(-2.0), MutableVec1(-1.0)),
      )

    private val distanceTestCases =
      listOf(
        Triple(MutableVec1(0.0), MutableVec1(0.0), 0.0),
        Triple(MutableVec1(1.0), MutableVec1(2.0), 1.0),
        Triple(MutableVec1(-1.0), MutableVec1(1.0), 2.0),
        Triple(MutableVec1(3.0), MutableVec1(3.0), 0.0),
        Triple(MutableVec1(5.0), MutableVec1(-5.0), 10.0),
      )

    private val dotProductTestCases =
      listOf(
        Triple(MutableVec1(2.0), MutableVec1(3.0), 6.0),
        Triple(MutableVec1(-2.0), MutableVec1(3.0), -6.0),
        Triple(MutableVec1(0.0), MutableVec1(3.0), 0.0),
        Triple(MutableVec1(2.5), MutableVec1(4.0), 10.0),
        Triple(MutableVec1(-2.5), MutableVec1(-4.0), 10.0),
      )

    private val scalarMultiplicationTestCases =
      listOf(
        Triple(MutableVec1(2.0), 3.0, MutableVec1(6.0)),
        Triple(MutableVec1(-2.0), 3.0, MutableVec1(-6.0)),
        Triple(MutableVec1(0.0), 3.0, MutableVec1(0.0)),
        Triple(MutableVec1(2.5), 4.0, MutableVec1(10.0)),
        Triple(MutableVec1(-2.5), -4.0, MutableVec1(10.0)),
      )
  }
}
