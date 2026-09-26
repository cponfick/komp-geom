package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.euclidean.twod.Seg2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class ShamosHoeyTest {
  @Test
  fun `detects crossing segments`() {
    ShamosHoey(listOf(Seg2(Vec2(0.0, 0.0), Vec2(2.0, 2.0)), Seg2(Vec2(0.0, 2.0), Vec2(2.0, 0.0))))
      .execute() shouldBe true
  }

  @Test
  fun `returns false for disjoint segments`() {
    ShamosHoey(listOf(Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)), Seg2(Vec2(0.0, 1.0), Vec2(1.0, 1.0))))
      .execute() shouldBe false
  }

  @Test
  fun `detects overlap`() {
    val segments =
      listOf(Seg2(Vec2(0.0, 0.0), Vec2(3.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
    ShamosHoey(segments).execute() shouldBe true
  }

  @Test
  fun `reports endpoint intersections`() {
    ShamosHoey(listOf(Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(1.0, 1.0))))
      .execute() shouldBe true
  }

  @Test
  fun `endpoint-only sweep matches pairwise detection`() {
    val random = Random(126)
    repeat(180) {
      val segments =
        (0 until 18).map {
          fun point() = Vec2(random.nextInt(-9, 10).toDouble(), random.nextInt(-9, 10).toDouble())
          Seg2(point(), point())
        }
      val expected =
        segments.indices.any { i ->
          (i + 1 until segments.size).any { j ->
            segments[i].intersection(segments[j]).type != IntersectionType.NONE
          }
        }
      ShamosHoey(segments).execute() shouldBe expected
    }
  }

  @Test
  fun `default detection stops without scheduling a dense set of crossing events`() {
    val segments =
      (1..1200).map { slope -> Seg2(Vec2(-1.0, -slope.toDouble()), Vec2(1.0, slope.toDouble())) }
    ShamosHoey(segments).execute() shouldBe true
  }

  @Test
  fun `disjoint vertical segments need only endpoint events`() {
    val segments =
      (0 until 1200).map { x -> Seg2(Vec2(x.toDouble(), 0.0), Vec2(x.toDouble(), 1.0)) }
    ShamosHoey(segments).execute() shouldBe false
  }

  @Test
  fun `has algorithm metadata`() {
    ShamosHoey.getId() shouldBe "intersection:shamos-hoey"
    ShamosHoey.getTimeComplexity() shouldBe "O(n log n)"
    ShamosHoey.getSpaceComplexity() shouldBe "O(n)"
  }
}
