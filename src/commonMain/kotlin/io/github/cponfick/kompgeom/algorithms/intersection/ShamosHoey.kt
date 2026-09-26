package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Segment2

/**
 * Detects whether any pair of two-dimensional segments intersects. Only endpoint events are needed:
 * each newly neighboring pair is tested immediately, as in the
 * [Shamos–Hoey algorithm](https://euro.ecom.cmu.edu/people/faculty/mshamos/1976GeometricIntersection.pdf)
 * (Algorithm 1). This takes O(n log n) time and O(n) space with the AVL order-maintenance tree
 * under consistent geometric predicates. Near the floating-point tolerance boundary, the sweep can
 * disagree with pairwise intersection tests (see
 * [precision tracking issue](https://github.com/cponfick/komp-geom/issues/184)). Shared endpoints
 * and overlaps count as intersections. Polygon simplicity, which allows only shared corners of
 * consecutive edges, is handled separately by [polygonHasSelfIntersection].
 *
 * @param segments The segments to inspect.
 * @param precision The equivalence used for geometric comparisons.
 */
public class ShamosHoey(
  segments: Collection<Segment2<*>>,
  private val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Algorithm<Boolean> {
  private val input = segments.toList()

  override fun execute(): Boolean =
    SegmentSweep(input, precision, stopAtFirst = true, endpointOnly = true).execute().isNotEmpty()

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Intersection"

    override fun getName(): String = "Shamos-Hoey"

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}

/** Polygon edges may only meet at the common endpoint of consecutive edges. */
internal fun polygonHasSelfIntersection(
  edges: List<Segment2<*>>,
  precision: DoubleEquivalence,
): Boolean {
  if (edges.any { it.start.eq(it.end, precision) }) return true
  return SegmentSweep(
      edges,
      precision,
      stopAtFirst = true,
      endpointOnly = true,
      allowedContact = { first, second, intersection ->
        val consecutive = second == first + 1 || first == 0 && second == edges.lastIndex
        val shared = if (second == first + 1) edges[first].end else edges[second].end
        consecutive &&
          intersection.type == IntersectionType.POINT &&
          intersection.point?.eq(shared, precision) == true
      },
    )
    .execute()
    .isNotEmpty()
}
