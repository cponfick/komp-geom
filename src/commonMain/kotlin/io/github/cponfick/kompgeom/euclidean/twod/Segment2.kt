package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Segment
import io.github.cponfick.kompgeom.core.Transformer

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
}
