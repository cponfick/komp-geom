package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.euclidean.twod.Seg2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
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
  fun `detects overlap and supports ignored pairs`() {
    val segments =
      listOf(Seg2(Vec2(0.0, 0.0), Vec2(3.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
    ShamosHoey(segments).execute() shouldBe true
    ShamosHoey(segments, ignoredPair = { _, _ -> true }).execute() shouldBe false
  }

  @Test
  fun `reports endpoint intersections`() {
    ShamosHoey(listOf(Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(1.0, 1.0))))
      .execute() shouldBe true
  }

  @Test
  fun `has algorithm metadata`() {
    ShamosHoey.getId() shouldBe "intersection:shamos-hoey"
    ShamosHoey.getTimeComplexity() shouldBe "O(n log n)"
  }
}
