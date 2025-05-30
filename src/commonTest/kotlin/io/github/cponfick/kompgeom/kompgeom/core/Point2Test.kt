package io.github.cponfick.kompgeom.kompgeom.core

import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI
import kotlin.test.Test

class Point2Test {
  @Test
  fun `default constructor initializes to zero`() {
    val point = Point2()
    point.x shouldBe 0.0F
    point.y shouldBe 0.0F
  }

  @Test
  fun `constructor with parameters initializes correctly`() {
    val point = Point2(1.0F, 2.0F)
    point.x shouldBe 1.0F
    point.y shouldBe 2.0F
  }

  @Test
  fun `copy constructor creates an identical point`() {
    val original = Point2(3.0F, 4.0F)
    val copy = Point2(original)
    original shouldBe copy
  }

  @Test
  fun `equals returns true for identical points`() {
    val point1 = Point2(1.0F, 2.0F)
    val point2 = Point2(1.0F, 2.0F)
    point1 shouldBe point2
  }

  @Test
  fun `equals returns false for different points`() {
    val point1 = Point2(1.0F, 2.0F)
    val point2 = Point2(3.0F, 4.0F)
    point1 shouldNotBe point2
  }

  @Test
  fun `distance calculates correctly between two points`() {
    val point1 = Point2(1.0F, 2.0F)
    val point2 = Point2(4.0F, 6.0F)
    val distance = point1 distance point2
    distance shouldBe 5.0F
  }

  @Test
  fun `rotate rotates point correctly by 90 degrees`() {
    val point = Point2(1.0F, 0.0F)
    val rotatedPoint = point.rotate(PI.toFloat() / 2)
    rotatedPoint shouldBe Point2(0.0F, 1.0F)
  }

  @Test
  fun `rotate rotates point correctly by 180 degrees`() {
    val point = Point2(1.0F, 0.0F)
    val rotatedPoint = point.rotate(PI.toFloat())
    rotatedPoint shouldBe Point2(-1.0F, 0.0F)
  }

  @Test
  fun `rotate rotates point correctly by 270 degrees`() {
    val point = Point2(1.0F, 0.0F)
    val rotatedPoint = point.rotate(3 * PI.toFloat() / 2)
    rotatedPoint shouldBe Point2(0.0F, -1.0F)
  }

  @Test
  fun `rotate rotates point correctly by 360 degrees`() {
    val point = Point2(1.0F, 0.0F)
    val rotatedPoint = point.rotate(2 * PI.toFloat())
    rotatedPoint shouldBe Point2(1.0F, 0.0F)
  }

  @Test
  fun `rotate rotates point correctly by 45 degrees in radians`() {
    val point = Point2(1.0F, 0.0F)
    val expectedPoint = Point2(0.70710677F, 0.70710677F)

    val actual = point.rotate(PI.toFloat() / 4)

    actual.x shouldBe (expectedPoint.x plusOrMinus DEFAULT_EPSILON)
    actual.y shouldBe (expectedPoint.y plusOrMinus DEFAULT_EPSILON)
  }
}
