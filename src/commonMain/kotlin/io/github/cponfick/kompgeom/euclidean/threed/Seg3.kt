package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.shapes.Segment3
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a segment in 3D space defined by a start and end point.
 *
 * @property start The starting point of the segment.
 * @property end The ending point of the segment.
 */
public data class Seg3(public override val start: Vec3, public override val end: Vec3) :
  Segment3<Vec3> {
  override fun transform(transformer: Transformer<Vec3>): Segment3<Vec3> {
    val transformedStart = transformer.apply(start)
    val transformedEnd = transformer.apply(end)
    return Seg3(transformedStart, transformedEnd)
  }

  override fun reverse(): Segment3<Vec3> = Seg3(end, start)
}
