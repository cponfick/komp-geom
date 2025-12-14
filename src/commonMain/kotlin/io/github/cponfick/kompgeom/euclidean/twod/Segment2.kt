package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Orientation
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionData
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Segment
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a segment in 2D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public data class Segment2(public override val start: Vec2, public override val end: Vec2) :
  Segment<Vec2> {
  override fun length(): Double = start.distance(end)

  override fun transform(transformer: Transformer<Vec2>): Segment2 {
    val transformedStart = transformer.apply(start)
    val transformedEnd = transformer.apply(end)
    return Segment2(transformedStart, transformedEnd)
  }

  override fun reverse(): Segment2 = Segment2(end, start)

  override fun intersection(
    other: Segment<Vec2>,
    equivalence: DoubleEquivalence,
  ): IntersectionData<Vec2> {

    val o1 = orientation(start, end, other.start, equivalence)
    val o2 = orientation(start, end, other.end, equivalence)

    val o3 = orientation(other.start, other.end, start, equivalence)
    val o4 = orientation(other.start, other.end, end, equivalence)

    return when {
      (o1 != o2 && o3 != o4) ->
        IntersectionData(IntersectionType.POINT, point = computeIntersection(this, other))
      (o1 == Orientation.COLLINEAR &&
        o2 == Orientation.COLLINEAR &&
        o3 == Orientation.COLLINEAR &&
        o4 == Orientation.COLLINEAR) -> computeOverlappingSegment(this, other, equivalence)
      else -> IntersectionData(IntersectionType.NONE)
    }
  }
}

/**
 * Computes the overlapping segment of two collinear segments if they overlap. This function does
 * not check if the segments are collinear! The user must ensure that the segments are in fact valid
 * input.
 *
 * @param seg1 The first segment.
 * @param seg2 The second segment.
 * @param equivalence The equivalence to use for comparing floating-point values.
 * @return An [IntersectionData] containing the type of intersection and the overlapping segment if
 *   applicable, or [IntersectionType.NONE] if there is no overlap.
 */
internal fun computeOverlappingSegment(
  seg1: Segment<Vec2>,
  seg2: Segment<Vec2>,
  equivalence: DoubleEquivalence,
): IntersectionData<Vec2> {
  val start1 = seg1.start
  val end1 = seg1.end
  val start2 = seg2.start
  val end2 = seg2.end

  if (
    isOnSegment(start1, end1, start2, equivalence) ||
      isOnSegment(start1, end1, end2, equivalence) ||
      isOnSegment(start2, end2, start1, equivalence) ||
      isOnSegment(start2, end2, end1, equivalence)
  ) {
    return IntersectionData(
      IntersectionType.OVERLAP,
      segment =
        Segment2(
          Vec2(maxOf(start1.x, start2.x), maxOf(start1.y, start2.y)),
          Vec2(minOf(end1.x, end2.x), minOf(end1.y, end2.y)),
        ),
    )
  }
  return IntersectionData(IntersectionType.NONE)
}

private fun isOnSegment(p: Vec2, q: Vec2, r: Vec2, equivalence: DoubleEquivalence): Boolean =
  equivalence.gte(r.x, minOf(p.x, q.x)) &&
    equivalence.lte(r.x, maxOf(p.x, q.x)) &&
    equivalence.gte(r.y, minOf(p.y, q.y)) &&
    equivalence.lte(r.y, maxOf(p.y, q.y))

private fun orientation(p: Vec2, q: Vec2, r: Vec2, equivalence: DoubleEquivalence): Orientation {
  val value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
  return when {
    equivalence.eqZero(value) -> Orientation.COLLINEAR
    equivalence.gt(value, 0.0) -> Orientation.CLOCKWISE
    else -> Orientation.COUNTERCLOCKWISE
  }
}

/**
 * Computes the intersection point of two segments.
 *
 * @param segment1 The first segment.
 * @param segment2 The second segment.
 * @return The intersection point of the two segments.
 * @throws IllegalArgumentException if the segments do not intersect or are collinear.
 */
public fun computeIntersection(segment1: Segment<Vec2>, segment2: Segment<Vec2>): Vec2 {
  val x1 = segment1.start.x
  val y1 = segment1.start.y
  val x2 = segment1.end.x
  val y2 = segment1.end.y
  val x3 = segment2.start.x
  val y3 = segment2.start.y
  val x4 = segment2.end.x
  val y4 = segment2.end.y

  val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
  require(denominator != 0.0) { "Segments do not intersect or are collinear." }

  val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
  return Vec2(x1 + t * (x2 - x1), y1 + t * (y2 - y1))
}
