package io.github.cponfick.kompgeom.core.shapes

import io.github.cponfick.kompgeom.core.Orientation
import io.github.cponfick.kompgeom.core.Vector
import io.github.cponfick.kompgeom.core.Vector2
import io.github.cponfick.kompgeom.core.Vector3
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.github.cponfick.kompgeom.euclidean.threed.Vec3
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

/**
 * Represents a geometric segment defined by a start and an endpoint.
 *
 * @param V The type of vector representing the start and end points of the segment.
 */
public interface Segment<V : Vector<V>> {
  /**
   * Get the length of the segment.
   *
   * @return The length of the segment.
   */
  public fun length(): Double = start.distance(end)

  /**
   * Returns the start point of the segment.
   *
   * @return The start point of the segment.
   */
  public val start: V

  /**
   * Returns the end point of the segment.
   *
   * @return The end point of the segment.
   */
  public val end: V
}

public interface Segment2<V : Vector2<V>> : Segment<V> {
  /**
   * Applies a transformation to the endpoints of this segment.
   *
   * @param transformer The transformer to apply to the segment endpoints.
   * @return A segment with transformed endpoints.
   */
  public fun transform(transformer: Transformer<V>): Segment2<V>

  /**
   * Reverses the segment, swapping its start and end points.
   *
   * @return A segment with the start and end points swapped.
   */
  public fun reverse(): Segment2<V>

  /**
   * Checks if this segment is equal to another segment, considering the order of points irrelevant.
   *
   * @param other The other segment to compare with.
   * @param equivalence The equivalence to use for comparing floating-point values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the segments are equal (regardless of point order), false otherwise.
   */
  public fun eq(
    other: Segment2<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean {
    return (start.eq(other.start, equivalence) && end.eq(other.end, equivalence)) ||
      (start.eq(other.end, equivalence) && end.eq(other.start, equivalence))
  }

  /**
   * Compute the intersection of this segment with another segment.
   *
   * @param other The other segment to intersect with.
   * @param equivalence The equivalence to use for comparing floating-point values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return A new segment representing the intersection, or null if there is no intersection/ the
   *   produce an infinite amount of intersections.
   */
  public fun intersection(
    other: Segment2<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): IntersectionData<Vec2> {
    val start = Vec2.from(this.start)
    val end = Vec2.from(this.end)
    val otherStart = Vec2.from(other.start)
    val otherEnd = Vec2.from(other.end)

    val o1 = orientation(start, end, otherStart, equivalence)
    val o2 = orientation(start, end, otherEnd, equivalence)

    val o3 = orientation(otherStart, otherEnd, start, equivalence)
    val o4 = orientation(otherStart, otherEnd, end, equivalence)

    return when {
      (o1 != o2 && o3 != o4) ->
        IntersectionData(IntersectionType.POINT, point = computeIntersection(this, other))

      (o1 == Orientation.COLLINEAR &&
        o2 == Orientation.COLLINEAR &&
        o3 == Orientation.COLLINEAR &&
        o4 == Orientation.COLLINEAR) ->
        computeOverlappingSegment(start, end, otherStart, otherEnd, equivalence)

      else -> IntersectionData(IntersectionType.NONE)
    }
  }
}

public interface Segment3<V : Vector3<V>> : Segment<V> {
  /**
   * Applies a transformation to the endpoints of this segment.
   *
   * @param transformer The transformer to apply to the segment endpoints.
   * @return A segment with transformed endpoints.
   */
  public fun transform(transformer: Transformer<V>): Segment3<V>

  /**
   * Reverses the segment, swapping its start and end points.
   *
   * @return A segment with the start and end points swapped.
   */
  public fun reverse(): Segment3<V>

  /**
   * Compute the intersection of this segment with another segment.
   *
   * @param other The other segment to intersect with.
   * @param equivalence The equivalence to use for comparing floating-point values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return A new segment representing the intersection, or null if there is no intersection/ the
   *   produce an infinite amount of intersections.
   */
  public fun intersection(
    other: Segment3<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): IntersectionData<Vec3> {
    val p1 = Vec3.from(this.start)
    val p2 = Vec3.from(this.end)
    val p3 = Vec3.from(other.start)
    val p4 = Vec3.from(other.end)

    val d1 = p2 - p1
    val d2 = p4 - p3
    val r = p3 - p1

    if (equivalence.eqZero(d1.norm()) || equivalence.eqZero(d2.norm())) {
      if (equivalence.eqZero(d1.norm()) && equivalence.eqZero(d2.norm())) {
        return if (p1.eq(p3, equivalence)) {
          IntersectionData(IntersectionType.POINT, point = Vec3.from(p1))
        } else {
          IntersectionData(IntersectionType.NONE)
        }
      }
      return IntersectionData(IntersectionType.NONE)
    }

    val cross = d1 cross d2
    val crossNorm = cross.norm()

    if (equivalence.eqZero(crossNorm)) {
      return computeOverlappingSegment(p1, p3, p4, d1, equivalence)
    }

    val rCrossD2 = r cross d2
    val t = (rCrossD2 dot cross) / (crossNorm * crossNorm)
    val rCrossD1 = r cross d1
    val s = (rCrossD1 dot cross) / (crossNorm * crossNorm)

    if (
      equivalence.gte(t, 0.0) &&
        equivalence.lte(t, 1.0) &&
        equivalence.gte(s, 0.0) &&
        equivalence.lte(s, 1.0)
    ) {
      val pointOnSeg1 = p1 + d1 * t
      val pointOnSeg2 = p3 + d2 * s

      if (pointOnSeg1.eq(pointOnSeg2, equivalence)) {
        return IntersectionData(IntersectionType.POINT, point = Vec3.from(pointOnSeg1))
      }
    }

    return IntersectionData(IntersectionType.NONE)
  }

  /**
   * Checks if this segment is equal to another segment, considering the order of points irrelevant.
   *
   * @param other The other segment to compare with.
   * @param equivalence The equivalence to use for comparing floating-point values. Defaults to
   *   [DEFAULT_DOUBLE_EQUIVALENCE].
   * @return True if the segments are equal (regardless of point order), false otherwise.
   */
  public fun eq(
    other: Segment3<*>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): Boolean {
    return (start.eq(other.start, equivalence) && end.eq(other.end, equivalence)) ||
      (start.eq(other.end, equivalence) && end.eq(other.start, equivalence))
  }
}

private fun orientation(
  p: Vector2<*>,
  q: Vector2<*>,
  r: Vector2<*>,
  equivalence: DoubleEquivalence,
): Orientation {
  val value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
  return when {
    equivalence.eqZero(value) -> Orientation.COLLINEAR
    equivalence.gt(value, 0.0) -> Orientation.CLOCKWISE
    else -> Orientation.COUNTERCLOCKWISE
  }
}

private fun isOnSegment(
  p: Vector2<*>,
  q: Vector2<*>,
  r: Vector2<*>,
  equivalence: DoubleEquivalence,
): Boolean =
  equivalence.gte(r.x, minOf(p.x, q.x)) &&
    equivalence.lte(r.x, maxOf(p.x, q.x)) &&
    equivalence.gte(r.y, minOf(p.y, q.y)) &&
    equivalence.lte(r.y, maxOf(p.y, q.y))

/**
 * Computes the intersection point of two segments.
 *
 * @param segment1 The first segment.
 * @param segment2 The second segment.
 * @return The intersection point of the two segments.
 * @throws IllegalArgumentException if the segments do not intersect or are collinear.
 */
public fun computeIntersection(segment1: Segment2<*>, segment2: Segment2<*>): Vec2 {
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

private fun computeOverlappingSegment(
  p1: Vec2,
  p2: Vec2,
  p3: Vec2,
  p4: Vec2,
  equivalence: DoubleEquivalence,
): IntersectionData<Vec2> {
  val d1 = p2 - p1
  val d1NormSquared = d1 dot d1

  // Handle degenerate case where first segment is a point
  if (equivalence.eqZero(d1NormSquared)) {
    return if (isOnSegment(p3, p4, p1, equivalence)) {
      IntersectionData(IntersectionType.POINT, point = p1)
    } else {
      IntersectionData(IntersectionType.NONE)
    }
  }

  val t3 = (p3 - p1).dot(d1) / d1NormSquared
  val t4 = (p4 - p1).dot(d1) / d1NormSquared

  val overlap =
    computeOverlap(t3, t4, p1, d1, equivalence) ?: return IntersectionData(IntersectionType.NONE)

  if (overlap.first.eq(overlap.second, equivalence)) {
    return IntersectionData(IntersectionType.POINT, point = overlap.first)
  }

  return IntersectionData(IntersectionType.OVERLAP, segment = overlap)
}

private fun computeOverlappingSegment(
  p1: Vec3,
  p3: Vec3,
  p4: Vec3,
  d1: Vec3,
  equivalence: DoubleEquivalence,
): IntersectionData<Vec3> {
  val r = p3 - p1

  // Check if segments are collinear by checking if r is parallel to d1
  val rCrossD1 = r cross d1
  if (!equivalence.eqZero(rCrossD1.norm())) {
    return IntersectionData(IntersectionType.NONE)
  }

  val d1Norm = d1.norm()
  val t3 = (p3 - p1).dot(d1) / (d1Norm * d1Norm)
  val t4 = (p4 - p1).dot(d1) / (d1Norm * d1Norm)

  val overlap =
    computeOverlap(t3, t4, p1, d1, equivalence) ?: return IntersectionData(IntersectionType.NONE)

  if (overlap.first.eq(overlap.second, equivalence)) {
    return IntersectionData(IntersectionType.POINT, point = overlap.first)
  }

  return IntersectionData(IntersectionType.OVERLAP, segment = overlap)
}

private fun <V : Vector<V>> computeOverlap(
  t3: Double,
  t4: Double,
  p1: V,
  d1: V,
  equivalence: DoubleEquivalence,
): Pair<V, V>? {
  val tMin = minOf(t3, t4)
  val tMax = maxOf(t3, t4)

  if (equivalence.gt(tMin, 1.0) || equivalence.lt(tMax, 0.0)) {
    return null
  }

  val overlapStart = maxOf(0.0, tMin)
  val overlapEnd = minOf(1.0, tMax)

  val startPoint = p1 + d1 * overlapStart
  val endPoint = p1 + d1 * overlapEnd

  return Pair(startPoint, endPoint)
}
