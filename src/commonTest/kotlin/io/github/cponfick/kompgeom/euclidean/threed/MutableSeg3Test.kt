package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MutableSeg3Test {

  @Test
  fun `constructor initializes with correct start and end points`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val segment = MutableSeg3(start, end)

    segment.start shouldBe start
    segment.end shouldBe end
  }

  @Test
  fun `length returns the correct length of the segment`() {
    val segment = MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(0.0, 0.0, 4.0))
    segment.length() shouldBe 4.0
  }

  @Test
  fun `length returns correct value for diagonal segment`() {
    val segment = MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 1.0, 1.0))
    segment.length() shouldBe kotlin.math.sqrt(3.0)
  }

  @Test
  fun `transform mutates the segment in place`() {
    val start = MutableVec3(0.0, 0.0, 0.0)
    val end = MutableVec3(1.0, 1.0, 1.0)
    val segment = MutableSeg3(start, end)
    val transformer =
      object : Transformer<MutableVec3> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : MutableVec3> apply(obj: T): T {
          obj.x += 1.0
          obj.y += 1.0
          obj.z += 1.0
          return obj
        }

        override fun inverse(): Transformer<MutableVec3> = this

        override fun preserveOrientation(): Boolean = true
      }

    val result = segment.transform(transformer)

    // Should return the same instance
    result shouldBe segment

    // Should mutate the endpoints
    segment.start.x shouldBe 1.0
    segment.start.y shouldBe 1.0
    segment.start.z shouldBe 1.0
    segment.end.x shouldBe 2.0
    segment.end.y shouldBe 2.0
    segment.end.z shouldBe 2.0

    // Original vectors should also be mutated
    start.x shouldBe 1.0
    end.x shouldBe 2.0
  }

  @Test
  fun `reverse mutates the segment in place by swapping start and end`() {
    val start = MutableVec3(1.0, 2.0, 3.0)
    val end = MutableVec3(4.0, 5.0, 6.0)
    val segment = MutableSeg3(start, end)

    // Store original values
    val originalStartX = start.x
    val originalStartY = start.y
    val originalStartZ = start.z
    val originalEndX = end.x
    val originalEndY = end.y
    val originalEndZ = end.z

    val result = segment.reverse()

    // Should return the same instance
    result shouldBe segment

    // Start point should now have end values
    segment.start.x shouldBe originalEndX
    segment.start.y shouldBe originalEndY
    segment.start.z shouldBe originalEndZ

    // End point should now have start values
    segment.end.x shouldBe originalStartX
    segment.end.y shouldBe originalStartY
    segment.end.z shouldBe originalStartZ

    // The original mutable vectors should be mutated
    start.x shouldBe originalEndX
    start.y shouldBe originalEndY
    start.z shouldBe originalEndZ
    end.x shouldBe originalStartX
    end.y shouldBe originalStartY
    end.z shouldBe originalStartZ
  }

  @Test
  fun `reverse can be called multiple times`() {
    val segment = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))

    // Store original values
    val originalStartX = segment.start.x
    val originalEndX = segment.end.x

    // Reverse twice should restore original orientation
    segment.reverse()
    segment.reverse()

    segment.start.x shouldBe originalStartX
    segment.end.x shouldBe originalEndX
  }

  @Test
  fun `toString returns correct format`() {
    val segment = MutableSeg3(MutableVec3(1.1, 2.2, 3.3), MutableVec3(4.4, 5.5, 6.6))
    val string = segment.toString()
    string shouldBe
      "MutableSeg3(start=MutableVec3(x=1.1, y=2.2, z=3.3), end=MutableVec3(x=4.4, y=5.5, z=6.6))"
  }

  private val nonIntersectingSegmentPairs =
    listOf(
      // Parallel segments in different planes
      MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)) to
        MutableSeg3(MutableVec3(0.0, 1.0, 0.0), MutableVec3(1.0, 1.0, 0.0)),
      // Segments on same line but not overlapping
      MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)) to
        MutableSeg3(MutableVec3(2.0, 0.0, 0.0), MutableVec3(3.0, 0.0, 0.0)),
      // Skew segments (not parallel, not intersecting)
      MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)) to
        MutableSeg3(MutableVec3(0.0, 1.0, 1.0), MutableVec3(1.0, 1.0, 1.0)),
      // Segments that would intersect if extended, but don't within bounds
      MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)) to
        MutableSeg3(MutableVec3(2.0, 0.0, 1.0), MutableVec3(2.0, 1.0, 1.0)),
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
        MutableSeg3(MutableVec3(-1.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)),
        MutableSeg3(MutableVec3(0.0, -1.0, 0.0), MutableVec3(0.0, 1.0, 0.0)),
        Vec3(0.0, 0.0, 0.0),
      ),
      // Two segments crossing in 3D space
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(2.0, 2.0, 2.0)),
        MutableSeg3(MutableVec3(2.0, 0.0, 2.0), MutableVec3(0.0, 2.0, 0.0)),
        Vec3(1.0, 1.0, 1.0),
      ),
      // Segments sharing an endpoint
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 1.0, 1.0)),
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)),
        Vec3(0.0, 0.0, 0.0),
      ),
      // Segments intersecting at midpoint
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(2.0, 0.0, 0.0)),
        MutableSeg3(MutableVec3(1.0, -1.0, 0.0), MutableVec3(1.0, 1.0, 0.0)),
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
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)),
        MutableSeg3(MutableVec3(0.5, 0.0, 0.0), MutableVec3(2.0, 0.0, 0.0)),
        Seg3(Vec3(0.5, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
      ),
      // One segment contains the other
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(2.0, 0.0, 0.0)),
        MutableSeg3(MutableVec3(0.5, 0.0, 0.0), MutableVec3(1.5, 0.0, 0.0)),
        Seg3(Vec3(0.5, 0.0, 0.0), Vec3(1.5, 0.0, 0.0)),
      ),
      // Identical segments
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 1.0, 1.0)),
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 1.0, 1.0)),
        Seg3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),
      ),
      // Overlap from opposite directions
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(1.0, 0.0, 0.0)),
        MutableSeg3(MutableVec3(2.0, 0.0, 0.0), MutableVec3(0.5, 0.0, 0.0)),
        Seg3(Vec3(0.5, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
      ),
      // Partial overlap in 3D
      Triple(
        MutableSeg3(MutableVec3(0.0, 0.0, 0.0), MutableVec3(2.0, 2.0, 2.0)),
        MutableSeg3(MutableVec3(1.0, 1.0, 1.0), MutableVec3(3.0, 3.0, 3.0)),
        Seg3(Vec3(1.0, 1.0, 1.0), Vec3(2.0, 2.0, 2.0)),
      ),
    )

  @Test
  fun `intersection returns OVERLAP for overlapping collinear segments`() {
    for ((segment1, segment2, expectedOverlap) in overlappingSegmentsExpectations) {
      val intersection = segment1.intersection(segment2)
      intersection.type shouldBe IntersectionType.OVERLAP
      intersection.segment!!.first.eq(expectedOverlap.start) shouldBe true
      intersection.segment.second.eq(expectedOverlap.end) shouldBe true
    }
  }

  @Test
  fun `eq method checks equality with same values`() {
    val segment1 = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    val segment2 = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    segment1.eq(segment2) shouldBe true
  }

  @Test
  fun `eq method checks equality regardless of direction`() {
    val segment1 = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    val segment2 = MutableSeg3(MutableVec3(4.0, 5.0, 6.0), MutableVec3(1.0, 2.0, 3.0))
    segment1.eq(segment2) shouldBe true
  }

  @Test
  fun `eq method checks inequality with different values`() {
    val segment1 = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    val segment2 = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(7.0, 8.0, 9.0))
    segment1.eq(segment2) shouldBe false
  }

  @Test
  fun `eq method works with immutable Seg3`() {
    val mutableSegment = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    val immutableSegment = Seg3(Vec3(1.0, 2.0, 3.0), Vec3(4.0, 5.0, 6.0))
    mutableSegment.eq(immutableSegment) shouldBe true
  }

  @Test
  fun `mutation of endpoints affects segment properties`() {
    val start = MutableVec3(0.0, 0.0, 0.0)
    val end = MutableVec3(3.0, 0.0, 0.0)
    val segment = MutableSeg3(start, end)

    segment.length() shouldBe 3.0

    // Mutate the end point
    end.x = 4.0

    // Length should update automatically since we hold references
    segment.length() shouldBe 4.0
  }

  @Test
  fun `zero-length segment has length zero`() {
    val segment = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(1.0, 2.0, 3.0))
    segment.length() shouldBe 0.0
  }

  @Test
  fun `transform with identity transformer leaves segment unchanged`() {
    val segment = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(4.0, 5.0, 6.0))
    val originalStartX = segment.start.x
    val originalEndX = segment.end.x

    val identityTransformer =
      object : Transformer<MutableVec3> {
        @Suppress("UNCHECKED_CAST") override fun <T : MutableVec3> apply(obj: T): T = obj

        override fun inverse(): Transformer<MutableVec3> = this

        override fun preserveOrientation(): Boolean = true
      }

    segment.transform(identityTransformer)

    segment.start.x shouldBe originalStartX
    segment.end.x shouldBe originalEndX
  }

  @Test
  fun `transform with scaling transformer scales endpoints correctly`() {
    val segment = MutableSeg3(MutableVec3(1.0, 2.0, 3.0), MutableVec3(2.0, 4.0, 6.0))

    val scalingTransformer =
      object : Transformer<MutableVec3> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : MutableVec3> apply(obj: T): T {
          obj.x *= 2.0
          obj.y *= 2.0
          obj.z *= 2.0
          return obj
        }

        override fun inverse(): Transformer<MutableVec3> = this

        override fun preserveOrientation(): Boolean = true
      }

    segment.transform(scalingTransformer)

    segment.start.x shouldBe 2.0
    segment.start.y shouldBe 4.0
    segment.start.z shouldBe 6.0
    segment.end.x shouldBe 4.0
    segment.end.y shouldBe 8.0
    segment.end.z shouldBe 12.0
  }
}
