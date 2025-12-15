package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.shapes.Segment2
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a segment in 2D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public data class Seg2(public override val start: Vec2, public override val end: Vec2) :
  Segment2<Vec2> {
  override fun transform(transformer: Transformer<Vec2>): Segment2<Vec2> {
    val transformedStart = transformer.apply(start)
    val transformedEnd = transformer.apply(end)
    return Seg2(transformedStart, transformedEnd)
  }

  override fun reverse(): Segment2<Vec2> = Seg2(end, start)
}
