package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Seg2Test {

  @Test
  fun `length returns the correct length of the segment`() {
    val segment = Seg2(Vec2(0.0, 0.0), Vec2(0.0, 4.0))
    segment.length() shouldBe 4.0
  }

  @Test
  fun `transform applies the transformation to the segment endpoints`() {
    val segment = Seg2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    val transformer =
      object : Transformer<Vec2> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : Vec2> apply(obj: T): T = Vec2(obj.x + 1, obj.y + 1) as T

        override fun inverse(): Transformer<Vec2> = this

        override fun preserveOrientation(): Boolean = true
      }
    val transformedSegment = segment.transform(transformer)
    transformedSegment.start shouldBe Vec2(1.0, 1.0)
    transformedSegment.end shouldBe Vec2(2.0, 2.0)
  }

  @Test
  fun `reverse returns the reverse segment`() {
    val segment = Seg2(Vec2(1.0, 1.0), Vec2(2.0, 2.0))

    val reversed = segment.reverse()

    reversed.start shouldBe segment.end
    reversed.end shouldBe segment.start
  }

  private val nonIntersectingSegmentPairs =
    listOf(
      Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)) to Seg2(Vec2(0.0, 1.0), Vec2(1.0, 1.0)),
      Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)) to Seg2(Vec2(2.0, 0.0), Vec2(3.0, 0.0)),
      Seg2(Vec2(0.0, 0.0), Vec2(0.0, 1.0)) to Seg2(Vec2(1.0, 0.0), Vec2(1.0, 1.0)),
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
        Seg2(Vec2(-1.0, -1.0), Vec2(1.0, 1.0)),
        Seg2(Vec2(-1.0, 1.0), Vec2(1.0, -1.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        Seg2(Vec2(-1.0, 1.0), Vec2(1.0, -1.0)),
        Seg2(Vec2(-1.0, -1.0), Vec2(1.0, 1.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        Seg2(Vec2(0.0, 0.0), Vec2(2.0, 2.0)),
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Vec2(0.0, 0.0),
      ),
      Triple(
        Seg2(Vec2(0.0, 0.0), Vec2(0.0, 2.0)),
        Seg2(Vec2(0.0, 1.0), Vec2(2.0, 1.0)),
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
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Seg2(Vec2(0.5, 0.0), Vec2(2.0, 0.0)),
        Pair(Vec2(0.5, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        Seg2(Vec2(0.5, 0.0), Vec2(2.0, 0.0)),
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Pair(Vec2(0.5, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
      ),
      Triple(
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Seg2(Vec2(-1.0, 0.0), Vec2(0.5, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(0.5, 0.0)),
      ),
      Triple(
        Seg2(Vec2(-1.0, 0.0), Vec2(0.5, 0.0)),
        Seg2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Pair(Vec2(0.0, 0.0), Vec2(0.5, 0.0)),
      ),
      Triple(
        Seg2(Vec2(0.0, 0.0), Vec2(4.0, 4.0)),
        Seg2(Vec2(2.0, 2.0), Vec2(6.0, 6.0)),
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
    val segment1 = Seg2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    val segment2 = Seg2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    val segment3 = Seg2(Vec2(1.0, 1.0), Vec2(2.0, 2.0))

    segment1.eq(segment2) shouldBe true
    segment1.eq(segment3) shouldBe false
  }

  @Test
  fun `eq for reversed segments works correctly`() {
    val segment1 = Seg2(Vec2(0.0, 0.0), Vec2(1.0, 1.0))

    segment1.eq(segment1.reverse()) shouldBe true
  }
}
