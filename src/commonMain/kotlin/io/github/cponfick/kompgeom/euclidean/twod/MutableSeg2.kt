package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.shapes.Segment2
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a mutable segment in 2D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public class MutableSeg2(
  public override val start: MutableVec2,
  public override val end: MutableVec2,
) : Segment2<MutableVec2> {
  public override fun transform(transformer: Transformer<MutableVec2>): Segment2<MutableVec2> =
    this.apply {
      transformer.apply(start)
      transformer.apply(end)
    }

  public override fun reverse(): Segment2<MutableVec2> =
    this.apply {
      val tempX = start.x
      val tempY = start.y
      start.x = end.x
      start.y = end.y
      end.x = tempX
      end.y = tempY
    }

  public override fun toString(): String = "MutableSeg2(start=$start, end=$end)"
}
