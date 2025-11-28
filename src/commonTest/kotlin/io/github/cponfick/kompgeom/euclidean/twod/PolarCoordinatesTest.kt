package io.github.cponfick.kompgeom.euclidean.twod

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI
import kotlin.test.Test

class PolarCoordinatesTest {
  @Test
  fun `constructor normalizes if azimuth is larger 2 PI`() {
    val polarCoordinate = PolarCoordinates(2.0, 2 * PI)
    polarCoordinate.azimuth shouldBe 0.0
    polarCoordinate.radius shouldBe 2.0
  }

  @Test
  fun `constructor flips angle if radius is negative`() {
    val polarCoordinate = PolarCoordinates(-2.0, 0.0)
    polarCoordinate.azimuth shouldBe PI
    polarCoordinate.radius shouldBe 2.0
  }

  @Test
  fun `constructor handles negative radius with angle adjustment`() {
    val polarCoordinate = PolarCoordinates(-3.0, PI / 4)
    polarCoordinate.radius shouldBe 3.0
    polarCoordinate.azimuth shouldBe 5 * PI / 4
  }

  @Test
  fun `fromCartesian creates correct polar coordinates`() {
    val polar1 = PolarCoordinates.fromCartesian(0.0, 1.0)
    polar1.radius shouldBe 1.0
    polar1.azimuth shouldBe PI / 2
  }

  @Test
  fun `fromCartesian with Vec2 creates correct polar coordinates`() {
    val vec = Vec2(3.0, 4.0)
    val polar = PolarCoordinates.fromCartesian(vec)
    polar.radius shouldBe 5.0 // sqrt(3^2 + 4^2) = 5
  }

  @Test
  fun `toCartesian converts to correct Cartesian coordinates`() {
    val polar1 = PolarCoordinates(1.0, PI / 2)
    val cart1 = polar1.toCartesian()

    cart1.x shouldBe (0.0 plusOrMinus 1e-15)
    cart1.y shouldBe 1.0
  }

  @Test
  fun `dimensions returns 2`() {
    val polar = PolarCoordinates(1.0, 0.0)
    polar.dimensions() shouldBe 2
  }

  @Test
  fun `isFinite returns true for finite values`() {
    val polar = PolarCoordinates(1.0, PI / 2)
    polar.isFinite() shouldBe true
  }

  @Test
  fun `isFinite returns false for infinite radius`() {
    val polar = PolarCoordinates(Double.POSITIVE_INFINITY, 0.0)
    polar.isFinite() shouldBe false
  }

  @Test
  fun `isFinite returns false for infinite azimuth`() {
    val polar = PolarCoordinates(1.0, Double.POSITIVE_INFINITY)
    polar.isFinite() shouldBe false
  }

  @Test
  fun `isInfinite returns true for infinite radius`() {
    val polar = PolarCoordinates(Double.POSITIVE_INFINITY, 0.0)
    polar.isInfinite() shouldBe true
  }

  @Test
  fun `isInfinite returns false for finite values`() {
    val polar = PolarCoordinates(1.0, PI)
    polar.isInfinite() shouldBe false
  }

  @Test
  fun `isNaN returns true for NaN radius`() {
    val polar = PolarCoordinates(Double.NaN, 0.0)
    polar.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns true for NaN azimuth`() {
    val polar = PolarCoordinates(1.0, Double.NaN)
    polar.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns false for valid values`() {
    val polar = PolarCoordinates(1.0, PI)
    polar.isNaN() shouldBe false
  }

  @Test
  fun `equals returns true for same coordinates`() {
    val polar1 = PolarCoordinates(1.0, PI / 2)
    val polar2 = PolarCoordinates(1.0, PI / 2)
    polar1 shouldBe polar2
  }

  @Test
  fun `equals returns false for different coordinates`() {
    val polar1 = PolarCoordinates(1.0, PI / 2)
    val polar2 = PolarCoordinates(2.0, PI / 2)
    polar1 shouldNotBe polar2
  }

  @Test
  fun `equals returns true for both NaN coordinates`() {
    val polar1 = PolarCoordinates(Double.NaN, 0.0)
    val polar2 = PolarCoordinates(Double.NaN, 1.0)
    polar1 shouldBe polar2
  }

  @Test
  fun `equals returns false when comparing NaN with valid coordinates`() {
    val polar1 = PolarCoordinates(Double.NaN, 0.0)
    val polar2 = PolarCoordinates(1.0, 0.0)
    polar1 shouldNotBe polar2
  }

  @Test
  fun `equals returns true for same object reference`() {
    val polar = PolarCoordinates(1.0, PI)
    polar shouldBe polar
  }

  @Test
  fun `equals returns false for different object types`() {
    val polar = PolarCoordinates(1.0, PI)
    polar shouldNotBe "not a polar coordinate"
  }

  @Test
  fun `hashCode is consistent for equal objects`() {
    val polar1 = PolarCoordinates(1.0, PI / 2)
    val polar2 = PolarCoordinates(1.0, PI / 2)
    polar1.hashCode() shouldBe polar2.hashCode()
  }

  @Test
  fun `hashCode returns 0 for NaN coordinates`() {
    val polar = PolarCoordinates(Double.NaN, 0.0)
    polar.hashCode() shouldBe 0
  }

  @Test
  fun `toString includes radius and azimuth`() {
    val polar = PolarCoordinates(1.5, PI / 3)
    val result = polar.toString()
    result shouldBe "PolarCoordinate(radius=1.5, azimuth=${PI / 3})"
  }

  @Test
  fun `constructor handles zero radius`() {
    val polar = PolarCoordinates(0.0, PI / 4)
    polar.radius shouldBe 0.0
    polar.azimuth shouldBe PI / 4
  }

  @Test
  fun `fromCartesian handles origin point`() {
    val polar = PolarCoordinates.fromCartesian(0.0, 0.0)
    polar.radius shouldBe 0.0
    polar.azimuth shouldBe 0.0
  }
}
