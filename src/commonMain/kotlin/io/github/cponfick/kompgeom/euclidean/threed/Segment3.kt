package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionData
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Segment
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a segment in 3D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public data class Segment3(public override val start: Vec3, public override val end: Vec3) :
  Segment<Vec3> {
  override fun length(): Double = start.distance(end)

  override fun transform(transformer: Transformer<Vec3>): Segment3 {
    val transformedStart = transformer.apply(start)
    val transformedEnd = transformer.apply(end)
    return Segment3(transformedStart, transformedEnd)
  }

  override fun reverse(): Segment3 = Segment3(end, start)

  override fun intersection(
    other: Segment<Vec3>,
    equivalence: DoubleEquivalence,
  ): IntersectionData<Vec3> {
    val p1 = this.start
    val p2 = this.end
    val p3 = other.start
    val p4 = other.end

    val d1 = p2 - p1
    val d2 = p4 - p3
    val r = p3 - p1

    if (equivalence.eqZero(d1.norm()) || equivalence.eqZero(d2.norm())) {
      if (equivalence.eqZero(d1.norm()) && equivalence.eqZero(d2.norm())) {
        return if (p1.eq(p3, equivalence)) {
          IntersectionData(IntersectionType.POINT, point = p1)
        } else {
          IntersectionData(IntersectionType.NONE)
        }
      }
      return IntersectionData(IntersectionType.NONE)
    }

    val cross = d1 cross d2
    val crossNorm = cross.norm()

    if (equivalence.eqZero(crossNorm)) {
      return handleParallelSegments(p1, p3, p4, d1, equivalence)
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
        return IntersectionData(IntersectionType.POINT, point = pointOnSeg1)
      }
    }

    return IntersectionData(IntersectionType.NONE)
  }
}

/**
 * Handles the case where two segments are parallel (including collinear).
 *
 * @param p1 Start of first segment
 * @param p3 Start of second segment
 * @param p4 End of second segment
 * @param d1 Direction vector of first segment
 * @param equivalence The equivalence to use for comparing floating-point values.
 * @return An [IntersectionData] containing the type of intersection.
 */
private fun handleParallelSegments(
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

  // Segments are collinear - check for overlap
  // Project all points onto the direction of the first segment
  val d1Norm = d1.norm()
  val t3 = (p3 - p1).dot(d1) / (d1Norm * d1Norm)
  val t4 = (p4 - p1).dot(d1) / (d1Norm * d1Norm)

  val tMin = minOf(t3, t4)
  val tMax = maxOf(t3, t4)

  // Check for overlap with [0, 1] (the first segment's parameter range)
  if (equivalence.gt(tMin, 1.0) || equivalence.lt(tMax, 0.0)) {
    // No overlap
    return IntersectionData(IntersectionType.NONE)
  }

  // Compute the overlap
  val overlapStart = maxOf(0.0, tMin)
  val overlapEnd = minOf(1.0, tMax)

  val startPoint = p1 + d1 * overlapStart
  val endPoint = p1 + d1 * overlapEnd

  // Check if overlap is a single point
  if (startPoint.eq(endPoint, equivalence)) {
    return IntersectionData(IntersectionType.POINT, point = startPoint)
  }

  return IntersectionData(IntersectionType.OVERLAP, segment = Segment3(startPoint, endPoint))
}
