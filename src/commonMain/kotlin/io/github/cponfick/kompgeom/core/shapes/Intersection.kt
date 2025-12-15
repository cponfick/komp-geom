package io.github.cponfick.kompgeom.core.shapes

import io.github.cponfick.kompgeom.core.Vector

/**
 * Represents the type of intersection between two segments.
 * - [NONE] indicates no intersection.
 * - [POINT] indicates a single point of intersection.
 * - [OVERLAP] indicates that the segments overlap in some region.
 */
public enum class IntersectionType {
  NONE,
  POINT,
  OVERLAP,
}

/**
 * Represents the result of an intersection operation between geometric segments.
 *
 * If the segments do not intersect, the type will be [IntersectionType.NONE]. If they intersect at
 * a single point, the type will be [IntersectionType.POINT] and the point will be non-null. If they
 * overlap, the type will be [IntersectionType.OVERLAP] and the segment will represent the
 * overlapping region.
 *
 * @param V The type of vector representing the points in the intersection.
 */
public data class IntersectionData<V : Vector<V>>(
  public val type: IntersectionType,
  public val point: V? = null,
  public val segment: Pair<V, V>? = null,
) {
  init {
    when (type) {
      IntersectionType.NONE -> {
        require(point == null) { "Point must be null when type is NONE." }
        require(segment == null) { "Segment must be null when type is NONE." }
      }

      IntersectionType.POINT -> {
        require(point != null) { "Point must not be null when type is POINT." }
        require(segment == null) { "Segment must be null when type is POINT." }
      }

      IntersectionType.OVERLAP -> {
        require(point == null) { "Point must be null when type is OVERLAP." }
        require(segment != null) { "Segment must not be null when type is OVERLAP." }
      }
    }
  }
}
