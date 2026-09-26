package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.euclidean.twod.Seg2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BentleyOttmannTest {
  @Test
  fun `reports all crossing intersections`() {
    val result =
      BentleyOttmann(
          listOf(
            Seg2(Vec2(0.0, 0.0), Vec2(3.0, 3.0)),
            Seg2(Vec2(0.0, 3.0), Vec2(3.0, 0.0)),
            Seg2(Vec2(1.5, -1.0), Vec2(1.5, 4.0)),
          )
        )
        .execute()
    result.size shouldBe 3
    result.all { it.intersection.type == IntersectionType.POINT } shouldBe true
  }

  @Test
  fun `reports intersections with vertical segments at their endpoint`() {
    val segments =
      listOf(
        Seg2(Vec2(15.0, 15.0), Vec2(11.0, 13.0)),
        Seg2(Vec2(1.0, 8.0), Vec2(12.0, 0.0)),
        Seg2(Vec2(17.0, 12.0), Vec2(17.0, 0.0)),
        Seg2(Vec2(13.0, 3.0), Vec2(2.0, 12.0)),
        Seg2(Vec2(8.0, 16.0), Vec2(9.0, 14.0)),
        Seg2(Vec2(4.0, 10.0), Vec2(15.0, 9.0)),
        Seg2(Vec2(0.0, 5.0), Vec2(17.0, 11.0)),
        Seg2(Vec2(19.0, 8.0), Vec2(4.0, 4.0)),
      )

    BentleyOttmann(segments).execute().map { it.first to it.second }.toSet() shouldBe
      setOf(1 to 6, 1 to 7, 2 to 6, 2 to 7, 3 to 5, 3 to 6, 3 to 7, 5 to 6)
  }

  @Test
  fun `reports overlap once`() {
    val result =
      BentleyOttmann(
          listOf(Seg2(Vec2(0.0, 0.0), Vec2(3.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
        )
        .execute()
    result.size shouldBe 1
    result.single().intersection.type shouldBe IntersectionType.OVERLAP
  }
}
