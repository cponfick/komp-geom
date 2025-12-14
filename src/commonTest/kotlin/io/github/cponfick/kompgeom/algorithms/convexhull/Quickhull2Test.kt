package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Quickhull2Test {
  @Test
  fun `viewer than 3 points should throw an exception`() {
    val points = listOf(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    shouldThrow<IllegalArgumentException> { Quickhull2(points).execute() }
  }

  @Test
  fun `getId returns the correct id`() {
    Quickhull2.getId() shouldBe "convex-hull:quickhull"
  }

  @Test
  fun `getComplexities return the correct complexities`() {
    Quickhull2.getTimeComplexity() shouldBe "O(n log n)"
    Quickhull2.getSpaceComplexity() shouldBe "O(n)"
  }

  @Test
  fun `simple 3 point hull`() {
    val points = listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(0.5, 1.0))

    val result = Quickhull2(points).execute()
    result.points.size shouldBe 3
    result.points.containsAll(points) shouldBe true
  }

  @Test
  fun `execute simple 4 point hull no additional points`() {
    val points = listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(1.0, 1.0), Vec2(0.0, 1.0))

    val result = Quickhull2(points).execute()
    result.points.size shouldBe 4
    result.points.containsAll(points) shouldBe true
  }

  @Test
  fun `execute hull with internal points`() {
    val hull = listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(1.0, 1.0), Vec2(0.0, 1.0))
    val internalPoints =
      listOf(
        Vec2(0.5, 0.5),
        Vec2(0.001, 0.001),
        Vec2(0.999, 0.999),
        Vec2(0.5, 0.25),
        Vec2(0.99999999, 0.99999999),
      )

    val result = Quickhull2(hull + internalPoints).execute()
    result.points.size shouldBe 4
    result.points.containsAll(hull) shouldBe true
  }

  private val hull =
    listOf(
      Vec2(-0.67, -15.05),
      Vec2(9.77, -15.27),
      Vec2(17.85, -12.41),
      Vec2(22.18, -6.49),
      Vec2(23.25, 0.97),
      Vec2(21.82, 7.50),
      Vec2(14.93, 12.32),
      Vec2(5.28, 13.60),
      Vec2(-3.49, 13.20),
      Vec2(-8.65, 10.35),
      Vec2(-10.83, 7.28),
      Vec2(-12.26, 1.88),
      Vec2(-12.35, -2.88),
      Vec2(-12.02, -5.95),
      Vec2(-11.62, -9.29),
    )

  private val internalPoints =
    listOf(
      Vec2(0.0, 0.0),
      Vec2(1.0, 1.0),
      Vec2(2.0, 2.0),
      Vec2(3.0, 3.0),
      Vec2(4.0, 4.0),
      Vec2(5.0, 5.0),
      Vec2(6.0, 6.0),
      Vec2(7.0, 7.0),
      Vec2(8.0, 8.0),
      Vec2(9.0, 9.0),
      Vec2(-5.13, 7.08),
      Vec2(1.14, 1.55),
      Vec2(-4.56, -2.23),
      Vec2(21.68, 6.90),
      Vec2(-0.32, -14.60),
      Vec2(-5.13, 11.11),
      Vec2(-11.69, -4.30),
    )

  @Test
  fun `execute larger hull with internal points`() {
    val result = Quickhull2(hull + internalPoints).execute()
    result.points.size shouldBe 15
    result.points.containsAll(hull) shouldBe true
  }

  @Test
  fun `execute hull with internal points mutable input`() {
    val mutableHull = hull.map { it.toMutableVec2() }
    val mutableInternalPoints = internalPoints.map { it.toMutableVec2() }

    val result = Quickhull2(mutableHull + mutableInternalPoints).execute()
    result.points.size shouldBe 15
    result.points.containsAll(mutableHull) shouldBe true
  }
}
