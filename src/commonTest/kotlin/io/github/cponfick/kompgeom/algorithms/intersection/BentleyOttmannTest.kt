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
