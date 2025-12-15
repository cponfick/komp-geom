package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.shapes.Segment3

/**
 * Represents a mutable segment in 3D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public class MutableSeg3(
  public override val start: MutableVec3,
  public override val end: MutableVec3,
) : Segment3<MutableVec3> {
  public override fun transform(
    transformer: io.github.cponfick.kompgeom.core.transform.Transformer<MutableVec3>
  ): Segment3<MutableVec3> =
    this.apply {
      transformer.apply(start)
      transformer.apply(end)
    }

  public override fun reverse(): Segment3<MutableVec3> =
    this.apply {
      val tempX = start.x
      val tempY = start.y
      val tempZ = start.z
      start.x = end.x
      start.y = end.y
      start.z = end.z
      end.x = tempX
      end.y = tempY
      end.z = tempZ
    }

  public override fun toString(): String = "MutableSeg3(start=$start, end=$end)"
}
