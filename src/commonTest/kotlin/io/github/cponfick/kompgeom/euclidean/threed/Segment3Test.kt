package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.Transformer
import io.github.cponfick.kompgeom.euclidean.utils.IntersectionType
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Segment3Test {

  @Test
  fun `length returns the correct length of the segment`() {
    val segment = Segment3(Vec3(0.0, 0.0, 0.0), Vec3(0.0, 0.0, 4.0))
    segment.length() shouldBe 4.0
  }

  @Test
  fun `length returns correct value for diagonal segment`() {
    val segment = Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0))
    segment.length() shouldBe kotlin.math.sqrt(3.0)
  }

  @Test
  fun `transform applies the transformation to the segment endpoints`() {
    val segment = Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0))
    val transformer =
      object : Transformer<Vec3> {
        override fun apply(obj: Vec3): Vec3 = Vec3(obj.x + 1, obj.y + 1, obj.z + 1)

        override fun inverse(): Transformer<Vec3> = this

        override fun preserveOrientation(): Boolean = true
      }
    val transformedSegment = segment.transform(transformer)
    transformedSegment.start shouldBe Vec3(1.0, 1.0, 1.0)
    transformedSegment.end shouldBe Vec3(2.0, 2.0, 2.0)
  }

  @Test
  fun `reverse returns the reverse segment`() {
    val segment = Segment3(Vec3(1.0, 1.0, 1.0), Vec3(2.0, 2.0, 2.0))

    val reversed = segment.reverse()

    reversed.start shouldBe segment.end
    reversed.end shouldBe segment.start
  }

  private val nonIntersectingSegmentPairs =
    listOf(
      // Parallel segments in different planes
      Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)) to
        Segment3(Vec3(0.0, 1.0, 0.0), Vec3(1.0, 1.0, 0.0)),
      // Segments on same line but not overlapping
      Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)) to
        Segment3(Vec3(2.0, 0.0, 0.0), Vec3(3.0, 0.0, 0.0)),
      // Skew segments (not parallel, not intersecting)
      Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)) to
        Segment3(Vec3(0.0, 1.0, 1.0), Vec3(1.0, 1.0, 1.0)),
      // Segments that would intersect if extended, but don't within bounds
      Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)) to
        Segment3(Vec3(2.0, 0.0, 1.0), Vec3(2.0, 1.0, 1.0)),
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
      // Two segments crossing at origin
      Triple(
        Segment3(Vec3(-1.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
        Segment3(Vec3(0.0, -1.0, 0.0), Vec3(0.0, 1.0, 0.0)),
        Vec3(0.0, 0.0, 0.0),
      ),
      // Two segments crossing in 3D space
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 2.0, 2.0)),
        Segment3(Vec3(2.0, 0.0, 2.0), Vec3(0.0, 2.0, 0.0)),
        Vec3(1.0, 1.0, 1.0),
      ),
      // Segments sharing an endpoint
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
        Vec3(0.0, 0.0, 0.0),
      ),
      // Segments intersecting at midpoint
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0)),
        Segment3(Vec3(1.0, -1.0, 0.0), Vec3(1.0, 1.0, 0.0)),
        Vec3(1.0, 0.0, 0.0),
      ),
    )

  @Test
  fun `intersection returns POINT for segments intersecting at a single point`() {
    for ((segment1, segment2, expectedPoint) in pointIntersectionExpectations) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.POINT
      intersection.point!!.eq(expectedPoint) shouldBe true
    }
  }

  private val overlappingSegmentsExpectations =
    listOf(
      // Partial overlap on same line
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
        Segment3(Vec3(0.5, 0.0, 0.0), Vec3(2.0, 0.0, 0.0)),
        Segment3(Vec3(0.5, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
      ),
      // One segment contains the other
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0)),
        Segment3(Vec3(0.5, 0.0, 0.0), Vec3(1.5, 0.0, 0.0)),
        Segment3(Vec3(0.5, 0.0, 0.0), Vec3(1.5, 0.0, 0.0)),
      ),
      // Identical segments
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),
      ),
      // Overlap from opposite directions
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
        Segment3(Vec3(2.0, 0.0, 0.0), Vec3(0.5, 0.0, 0.0)),
        Segment3(Vec3(0.5, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
      ),
      // Partial overlap in 3D
      Triple(
        Segment3(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 2.0, 2.0)),
        Segment3(Vec3(1.0, 1.0, 1.0), Vec3(3.0, 3.0, 3.0)),
        Segment3(Vec3(1.0, 1.0, 1.0), Vec3(2.0, 2.0, 2.0)),
      ),
    )

  @Test
  fun `intersection returns OVERLAP for overlapping collinear segments`() {
    for ((segment1, segment2, expectedOverlap) in overlappingSegmentsExpectations) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.OVERLAP
      intersection.segment!!.start.eq(expectedOverlap.start) shouldBe true
      intersection.segment.end.eq(expectedOverlap.end) shouldBe true
    }
  }

  @Test
  fun `intersection handles degenerate segments`() {
    val point = Vec3(1.0, 1.0, 1.0)
    val degenerateSegment = Segment3(point, point)
    val normalSegment = Segment3(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 2.0, 2.0))

    val intersection = degenerateSegment.intersection(normalSegment)
    intersection.type shouldBe IntersectionType.NONE
  }

  @Test
  fun `intersection of two identical point segments`() {
    val point = Vec3(1.0, 1.0, 1.0)
    val segment1 = Segment3(point, point)
    val segment2 = Segment3(point, point)

    val intersection = segment1.intersection(segment2)
    intersection.type shouldBe IntersectionType.POINT
    intersection.point!!.eq(point) shouldBe true
  }

  @Test
  fun `collinear segments touching at endpoint`() {
    val segment1 = Segment3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0))
    val segment2 = Segment3(Vec3(1.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0))

    val intersection = segment1.intersection(segment2)
    intersection.type shouldBe IntersectionType.POINT
    intersection.point!!.eq(Vec3(1.0, 0.0, 0.0)) shouldBe true
  }
}
