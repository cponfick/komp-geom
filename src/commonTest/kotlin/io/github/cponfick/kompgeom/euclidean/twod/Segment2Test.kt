package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Transformer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Segment2Test {

  @Test
  fun `length returns the correct length of the segment`() {
    val segment = Segment2(Vec2(0.0, 0.0), Vec2(0.0, 4.0))
    segment.length() shouldBe 4.0
  }

  @Test
  fun `transform applies the transformation to the segment endpoints`() {
    val segment = Segment2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    val transformer =
      object : Transformer<Vec2> {
        override fun apply(obj: Vec2): Vec2 = Vec2(obj.x + 1, obj.y + 1)

        override fun inverse(): Transformer<Vec2> = this

        override fun preserveOrientation(): Boolean = true
      }
    val transformedSegment = segment.transform(transformer)
    transformedSegment.start shouldBe Vec2(1.0, 1.0)
    transformedSegment.end shouldBe Vec2(2.0, 2.0)
  }

  @Test
  fun `reverse returns the reverse segment`() {
    val segment = Segment2(Vec2(1.0, 1.0), Vec2(2.0, 2.0))

    val reversed = segment.reverse()

    reversed.start shouldBe segment.end
    reversed.end shouldBe segment.start
  }
}
