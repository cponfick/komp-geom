package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.Location
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.sqrt
import kotlin.test.Test

class Line3Test {

  @Test
  fun `constructor initializes correctly`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val moment = Vec3(0.0, 1.0, 0.0) cross direction
    val line = Line3(direction, moment)

    line.direction shouldBe direction
    line.moment shouldBe moment
  }

  @Test
  fun `constructor throws exception for non-unit direction`() {
    val direction = Vec3(2.0, 0.0, 0.0)
    val moment = Vec3(0.0, 1.0, 0.0)

    shouldThrow<IllegalArgumentException> { Line3(direction, moment) }
  }

  @Test
  fun `distance returns correct result for point on line`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val pointOnLine = Vec3(5.0, 0.0, 0.0)

    line.distance(pointOnLine) shouldBe 0.0
  }

  @Test
  fun `distance returns correct result for point off line`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val pointOffLine = Vec3(3.0, 4.0, 12.0)

    line.distance(pointOffLine) shouldBe sqrt(4.0 * 4.0 + 12.0 * 12.0)
  }

  @Test
  fun `offset returns correct result for positive side`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val point = Vec3(0.0, -2.0, 0.0)

    line.offset(point) shouldBe 2.0
  }

  @Test
  fun `offset returns correct result for negative side`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val point = Vec3(0.0, 2.0, 0.0)

    line.offset(point) shouldBe -2.0
  }

  @Test
  fun `offset returns zero for point on line`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val pointOnLine = Vec3(5.0, 0.0, 0.0)

    line.offset(pointOnLine) shouldBe 0.0
  }

  @Test
  fun `offset returns opposite signs for symmetric points`() {
    val cases =
      listOf(
        Triple(Vec3(0.1, 1.0, 2.0).normalize(), Vec3.ZERO, Vec3(0.0, 1.0, 0.0)),
        Triple(Vec3(2.0, 0.1, 3.0).normalize(), Vec3.ZERO, Vec3(0.0, 0.0, 1.0)),
        Triple(Vec3(2.0, 3.0, 0.1).normalize(), Vec3.ZERO, Vec3(0.0, 1.0, 0.0)),
        Triple(Vec3(0.2, 1.0, 0.3).normalize(), Vec3(0.0, 2.0, 0.0), Vec3(1.0, 0.0, 0.0)),
      )

    cases.forEach { (direction, linePoint, testVector) ->
      val line = Line3.fromPointAndDirection(linePoint, direction)
      val pointA = linePoint + testVector
      val pointB = linePoint - testVector

      val offsetA = line.offset(pointA)
      val offsetB = line.offset(pointB)

      offsetA shouldNotBe 0.0
      offsetA shouldBe -offsetB
    }
  }

  @Test
  fun `reverse creates line with opposite direction and moment`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val moment = Vec3(0.0, 2.0, 0.0) cross direction
    val line = Line3(direction, moment)

    val reversed = line.reverse()

    reversed.direction shouldBe -direction
    reversed.moment shouldBe -moment
  }

  @Test
  fun `equals returns true for identical lines`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val moment = Vec3(0.0, 2.0, 0.0) cross direction
    val line1 = Line3(direction, moment)
    val line2 = Line3(direction, moment)

    (line1 == line2) shouldBe true
  }

  @Test
  fun `equals returns false for different directions`() {
    val line1 = Line3(Vec3(1.0, 0.0, 0.0).normalize(), Vec3.ZERO)
    val line2 = Line3(Vec3(0.0, 1.0, 0.0).normalize(), Vec3.ZERO)

    (line1 == line2) shouldBe false
  }

  @Test
  fun `equals returns false for different moments`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line1 = Line3(direction, Vec3.ZERO)
    val line2 = Line3(direction, Vec3(0.0, 1.0, 0.0))

    (line1 == line2) shouldBe false
  }

  @Test
  fun `hashCode is consistent with equals`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val moment = Vec3(0.0, 1.0, 0.0) cross direction
    val line1 = Line3(direction, moment)
    val line2 = Line3(direction, moment)

    line1.hashCode() shouldBe line2.hashCode()
  }

  @Test
  fun `fromPointAndDirection throws exception for zero direction`() {
    val point = Vec3(1.0, 2.0, 3.0)
    val zeroDirection = Vec3.ZERO

    shouldThrow<IllegalArgumentException> { Line3.fromPointAndDirection(point, zeroDirection) }
  }

  @Test
  fun `fromPoints creates correct line`() {
    val p1 = Vec3(0.0, 1.0, 0.0)
    val p2 = Vec3(3.0, 1.0, 0.0)

    val line = Line3.fromPoints(p1, p2)

    line.distance(p1) shouldBe 0.0
    line.distance(p2) shouldBe 0.0
  }

  @Test
  fun `fromPoints throws exception for identical points`() {
    val point = Vec3(1.0, 2.0, 3.0)

    shouldThrow<IllegalArgumentException> { Line3.fromPoints(point, point) }
  }

  @Test
  fun `custom precision is respected in constructor`() {
    val customPrecision = EpsilonDoubleEquivalence(1e-10)
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO, customPrecision)

    line.precision shouldBe customPrecision
  }

  @Test
  fun `location returns correct side for point on positive side`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val point = Vec3(0.0, -1.0, 0.0)

    line.location(point) shouldBe Location.PLUS
  }

  @Test
  fun `location returns correct side for point on negative side`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val point = Vec3(0.0, 1.0, 0.0)

    line.location(point) shouldBe Location.MINUS
  }

  @Test
  fun `location returns ON for point on line`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val pointOnLine = Vec3(5.0, 0.0, 0.0)

    line.location(pointOnLine) shouldBe Location.ON
  }

  @Test
  fun `contains is true for point on line`() {
    val direction = Vec3(1.0, 0.0, 0.0).normalize()
    val line = Line3(direction, Vec3.ZERO)
    val pointOnLine = Vec3(5.0, 0.0, 0.0)

    line.contains(pointOnLine) shouldBe true
  }
}
