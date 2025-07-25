package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.partitioning.Location
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Line2Test {

  @Test
  fun `constructor initializes correctly`() {
    val direction = Vec2.unit(1.0, 0.0)
    val originOffset = 5.0
    val line = Line2(direction, originOffset)

    line.direction shouldBe direction
    line.originOffSet shouldBe originOffset
  }

  @Test
  fun `constructor throws exception for non-unit direction`() {
    val direction = Vec2(2.0, 0.0)
    val originOffset = 5.0

    shouldThrow<IllegalArgumentException> { Line2(direction, originOffset) }
  }

  @Test
  fun `distance returns correct result for point on line`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val pointOnLine = Vec2(5.0, 0.0)

    line.distance(pointOnLine) shouldBe 0.0
  }

  @Test
  fun `distance returns correct result for point off line`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val pointOffLine = Vec2(3.0, 4.0)

    line.distance(pointOffLine) shouldBe 4.0
  }

  @Test
  fun `offset returns correct result for positive side`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val point = Vec2(0.0, 2.0)

    line.offset(point) shouldBe -2.0
  }

  @Test
  fun `offset returns correct result for negative side`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val point = Vec2(0.0, -2.0)

    line.offset(point) shouldBe 2.0
  }

  @Test
  fun `offset returns zero for point on line`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val pointOnLine = Vec2(5.0, 0.0)

    line.offset(pointOnLine) shouldBe 0.0
  }

  @Test
  fun `reverse creates line with opposite direction and offset`() {
    val direction = Vec2.unit(1.0, 0.0)
    val originOffset = 3.0
    val line = Line2(direction, originOffset)

    val reversed = line.reverse()

    reversed.direction shouldBe -direction
    reversed.originOffSet shouldBe -originOffset
  }

  @Test
  fun `equals returns true for identical lines`() {
    val direction = Vec2.unit(1.0, 0.0)
    val originOffset = 2.5
    val line1 = Line2(direction, originOffset)
    val line2 = Line2(direction, originOffset)

    (line1 == line2) shouldBe true
  }

  @Test
  fun `equals returns false for different directions`() {
    val line1 = Line2(Vec2.unit(1.0, 0.0), 2.5)
    val line2 = Line2(Vec2.unit(0.0, 1.0), 2.5)

    (line1 == line2) shouldBe false
  }

  @Test
  fun `equals returns false for different offsets`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line1 = Line2(direction, 2.5)
    val line2 = Line2(direction, 3.0)

    (line1 == line2) shouldBe false
  }

  @Test
  fun `hashCode is consistent with equals`() {
    val direction = Vec2.unit(1.0, 0.0)
    val originOffset = 2.5
    val line1 = Line2(direction, originOffset)
    val line2 = Line2(direction, originOffset)

    line1.hashCode() shouldBe line2.hashCode()
  }

  @Test
  fun `fromPointAndDirection throws exception for zero direction`() {
    val point = Vec2(1.0, 2.0)
    val zeroDirection = Vec2(0.0, 0.0)

    shouldThrow<IllegalArgumentException> { Line2.fromPointAndDirection(point, zeroDirection) }
  }

  @Test
  fun `fromPoints creates correct line`() {
    val p1 = Vec2(0.0, 0.0)
    val p2 = Vec2(3.0, 4.0)

    val line = Line2.fromPoints(p1, p2)

    line.distance(p1) shouldBe 0.0
    line.distance(p2) shouldBe 0.0
  }

  @Test
  fun `fromPoints throws exception for identical points`() {
    val point = Vec2(1.0, 2.0)

    shouldThrow<IllegalArgumentException> { Line2.fromPoints(point, point) }
  }

  @Test
  fun `custom precision is respected in constructor`() {
    val customPrecision = DoubleEquivalence(1e-10)
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0, customPrecision)

    line.precision shouldBe customPrecision
  }

  @Test
  fun `location returns correct side for point on positive side`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val point = Vec2(0.0, 1.0)

    line.location(point) shouldBe Location.MINUS
  }

  @Test
  fun `location returns correct side for point on negative side`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val point = Vec2(0.0, -10.0)

    line.location(point) shouldBe Location.PLUS
  }

  @Test
  fun `location returns ON for point on line`() {
    val direction = Vec2.unit(1.0, 0.0)
    val line = Line2(direction, 0.0)
    val pointOnLine = Vec2(5.0, 0.0)

    line.location(pointOnLine) shouldBe Location.ON
  }
}
