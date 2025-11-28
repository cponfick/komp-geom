package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.euclidean.utils.IntersectionData

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
  public fun length(): Double

  /**
   * Applies a transformation to the endpoints of this segment.
   *
   * @param transformer The transformer to apply to the segment endpoints.
   * @return A new segment with transformed endpoints.
   */
  public fun transform(transformer: Transformer<V>): Segment<V>

  /**
   * Reverses the segment, swapping its start and end points.
   *
   * @return A new segment with the start and end points swapped.
   */
  public fun reverse(): Segment<V>

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
    other: Segment<V>,
    equivalence: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  ): IntersectionData<V>
}
