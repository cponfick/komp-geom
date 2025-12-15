package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MutableSeg2Test {

  @Test
  fun `constructor with parameters initializes correctly`() {
    val start = MutableVec2(1.0, 2.0)
    val end = MutableVec2(3.0, 4.0)
    val segment = MutableSeg2(start, end)
    segment.start shouldBe start
    segment.end shouldBe end
  }

  @Test
  fun `toString returns correct format`() {
    val segment = MutableSeg2(MutableVec2(1.1, 2.2), MutableVec2(3.3, 4.4))
    segment.toString() shouldBe
      "MutableSeg2(start=MutableVec2(x=1.1, y=2.2), end=MutableVec2(x=3.3, y=4.4))"
  }

  @Test
  fun `length returns the correct length of the segment`() {
    val segment = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(0.0, 4.0))
    segment.length() shouldBe 4.0
  }

  @Test
  fun `length returns correct value for diagonal segment`() {
    val segment = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(3.0, 4.0))
    segment.length() shouldBe 5.0
  }

  @Test
  fun `transform mutates the segment in place`() {
    val start = MutableVec2(0.0, 0.0)
    val end = MutableVec2(1.0, 1.0)
    val segment = MutableSeg2(start, end)
    val transformer =
      object : Transformer<MutableVec2> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : MutableVec2> apply(obj: T): T {
          obj.x += 1.0
          obj.y += 1.0
          return obj
        }

        override fun inverse(): Transformer<MutableVec2> = this

        override fun preserveOrientation(): Boolean = true
      }

    val result = segment.transform(transformer)

    // Should return the same instance
    result shouldBe segment
    // Should mutate the original points
    segment.start.x shouldBe 1.0
    segment.start.y shouldBe 1.0
    segment.end.x shouldBe 2.0
    segment.end.y shouldBe 2.0
    // Original references should be mutated
    start.x shouldBe 1.0
    start.y shouldBe 1.0
    end.x shouldBe 2.0
    end.y shouldBe 2.0
  }

  @Test
  fun `transform applies the transformation to the segment endpoints`() {
    val segment = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 1.0))
    val transformer =
      object : Transformer<MutableVec2> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : MutableVec2> apply(obj: T): T {
          obj.x *= 2.0
          obj.y *= 2.0
          return obj
        }

        override fun inverse(): Transformer<MutableVec2> = this

        override fun preserveOrientation(): Boolean = true
      }

    segment.transform(transformer)

    segment.start.eq(MutableVec2(0.0, 0.0)) shouldBe true
    segment.end.eq(MutableVec2(2.0, 2.0)) shouldBe true
  }

  @Test
  fun `reverse mutates the segment in place`() {
    val start = MutableVec2(1.0, 2.0)
    val end = MutableVec2(3.0, 4.0)
    val segment = MutableSeg2(start, end)

    val result = segment.reverse()

    // Should return the same instance
    result shouldBe segment
    // Start and end should be swapped
    segment.start.x shouldBe 3.0
    segment.start.y shouldBe 4.0
    segment.end.x shouldBe 1.0
    segment.end.y shouldBe 2.0
  }

  @Test
  fun `reverse returns the reversed segment`() {
    val segment = MutableSeg2(MutableVec2(1.0, 1.0), MutableVec2(2.0, 2.0))
    val originalStartX = segment.start.x
    val originalStartY = segment.start.y
    val originalEndX = segment.end.x
    val originalEndY = segment.end.y

    val reversed = segment.reverse()

    reversed.start.x shouldBe originalEndX
    reversed.start.y shouldBe originalEndY
    reversed.end.x shouldBe originalStartX
    reversed.end.y shouldBe originalStartY
  }

  @Test
  fun `reverse twice returns to original state`() {
    val segment = MutableSeg2(MutableVec2(1.0, 2.0), MutableVec2(3.0, 4.0))

    segment.reverse().reverse()

    segment.start.x shouldBe 1.0
    segment.start.y shouldBe 2.0
    segment.end.x shouldBe 3.0
    segment.end.y shouldBe 4.0
  }

  private val nonIntersectingSegmentPairs =
    listOf(
      MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)) to
        MutableSeg2(MutableVec2(0.0, 1.0), MutableVec2(1.0, 1.0)),
      MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)) to
        MutableSeg2(MutableVec2(2.0, 0.0), MutableVec2(3.0, 0.0)),
      MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(0.0, 1.0)) to
        MutableSeg2(MutableVec2(1.0, 0.0), MutableVec2(1.0, 1.0)),
    )

  @Test
  fun `intersection returns NONE for non-intersecting segments`() {
    for ((segment1, segment2) in nonIntersectingSegmentPairs) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.NONE
    }
  }

  private val pointIntersectionExpectations =
    listOf(
      Triple(
        MutableSeg2(MutableVec2(-1.0, -1.0), MutableVec2(1.0, 1.0)),
        MutableSeg2(MutableVec2(-1.0, 1.0), MutableVec2(1.0, -1.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        MutableSeg2(MutableVec2(-1.0, 1.0), MutableVec2(1.0, -1.0)),
        MutableSeg2(MutableVec2(-1.0, -1.0), MutableVec2(1.0, 1.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(2.0, 2.0)),
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(0.0, 2.0)),
        MutableSeg2(MutableVec2(0.0, 1.0), MutableVec2(2.0, 1.0)),
        Vec2(0.0, 1.0),
      ),
    )

  @Test
  fun `intersection returns POINT for segments intersecting at a single point`() {
    for ((segment1, segment2, expectedPoint) in pointIntersectionExpectations) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.POINT
      intersection.point shouldBe expectedPoint
    }
  }

  private val overLappingSegmentsExpectations =
    listOf(
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        MutableSeg2(MutableVec2(0.5, 0.0), MutableVec2(2.0, 0.0)),
        Pair(Vec2(0.5, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.5, 0.0), MutableVec2(2.0, 0.0)),
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        Pair(Vec2(0.5, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        MutableSeg2(MutableVec2(-1.0, 0.0), MutableVec2(0.5, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(0.5, 0.0)),
      ),
      Triple(
        MutableSeg2(MutableVec2(-1.0, 0.0), MutableVec2(0.5, 0.0)),
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(0.5, 0.0)),
      ),
      Triple(
        MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(4.0, 4.0)),
        MutableSeg2(MutableVec2(2.0, 2.0), MutableVec2(6.0, 6.0)),
        Pair(Vec2(2.0, 2.0), Vec2(4.0, 4.0)),
      ),
    )

  @Test
  fun `intersection returns OVERLAP for overlapping segments`() {
    for ((segment1, segment2, expectedOverlap) in overLappingSegmentsExpectations) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.OVERLAP
      intersection.segment shouldBe expectedOverlap
    }
  }

  @Test
  fun `eq works correctly`() {
    val segment1 = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 1.0))
    val segment2 = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 1.0))
    val segment3 = MutableSeg2(MutableVec2(1.0, 1.0), MutableVec2(2.0, 2.0))

    segment1.eq(segment2) shouldBe true
    segment1.eq(segment3) shouldBe false
  }

  @Test
  fun `eq for reversed segments works correctly`() {
    val segment = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 1.0))
    segment.eq(MutableSeg2(MutableVec2(1.0, 1.0), MutableVec2(0.0, 0.0))) shouldBe true
  }

  @Test
  fun `eq with different vector types works correctly`() {
    val mutableSegment = MutableSeg2(MutableVec2(0.0, 0.0), MutableVec2(1.0, 1.0))
    val immutableSegment = Seg2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))

    mutableSegment.eq(immutableSegment) shouldBe true
  }

  @Test
  fun `intersection with immutable segment works correctly`() {
    val mutableSegment = MutableSeg2(MutableVec2(-1.0, -1.0), MutableVec2(1.0, 1.0))
    val immutableSegment = Seg2(Vec2(-1.0, 1.0), Vec2(1.0, -1.0))

    val intersection = mutableSegment.intersection(immutableSegment)

    intersection.type shouldBe IntersectionType.POINT
    intersection.point shouldBe Vec2(0.0, 0.0)
  }

  @Test
  fun `mutating start point affects the segment`() {
    val start = MutableVec2(1.0, 2.0)
    val end = MutableVec2(3.0, 4.0)
    val segment = MutableSeg2(start, end)

    start.x = 5.0
    start.y = 6.0

    segment.start.x shouldBe 5.0
    segment.start.y shouldBe 6.0
  }

  @Test
  fun `mutating end point affects the segment`() {
    val start = MutableVec2(1.0, 2.0)
    val end = MutableVec2(3.0, 4.0)
    val segment = MutableSeg2(start, end)

    end.x = 7.0
    end.y = 8.0

    segment.end.x shouldBe 7.0
    segment.end.y shouldBe 8.0
  }

  @Test
  fun `length updates when points are mutated`() {
    val start = MutableVec2(0.0, 0.0)
    val end = MutableVec2(0.0, 3.0)
    val segment = MutableSeg2(start, end)

    segment.length() shouldBe 3.0

    end.y = 4.0

    segment.length() shouldBe 4.0
  }
}
