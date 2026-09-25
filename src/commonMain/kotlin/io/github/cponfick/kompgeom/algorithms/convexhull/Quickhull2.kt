package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.Vector2
import io.github.cponfick.kompgeom.core.shapes.Location
import io.github.cponfick.kompgeom.euclidean.twod.Line2

/**
 * Quickhull algorithm for computing the convex hull of a collection of 2D points.
 *
 * Duplicate points are ignored. If the unique input contains one point, the hull contains that
 * point; if it contains two points, the hull contains both points. Collinear input similarly
 * produces the two endpoints of the hull, ordered from the lower to the upper x-coordinate.
 *
 * @property input Collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class Quickhull2<V : Vector2<V>>(private val input: Collection<V>) : ConvexHull<V> {

  init {
    require(input.isNotEmpty()) { "Input must contain at least 1 element" }
  }

  override fun execute(): Result<V> {
    // Keeping occurrences with equal coordinates would make a zero-length base line possible and
    // would also make duplicate vertices appear in the result.
    val points = input.distinctBy { it.x to it.y }
    if (points.size <= 2) {
      return Result(points.sortedWith(compareBy<V> { it.x }.thenBy { it.y }))
    }

    val minX = points.minWithOrNull(compareBy<V> { it.x }.thenBy { it.y })!!
    val maxX = points.maxWithOrNull(compareBy<V> { it.x }.thenBy { it.y })!!

    val line = Line2.fromPoints(minX, maxX)
    val plusSide = points.filter { line.location(it) == Location.PLUS }
    val minusSide = points.filter { line.location(it) == Location.MINUS }

    return Result(
      buildList {
        add(minX)
        addAll(findHull(plusSide, minX, maxX))
        add(maxX)
        addAll(findHull(minusSide, maxX, minX))
      }
    )
  }

  private fun findHull(points: List<V>, p1: V, p2: V): List<V> {
    if (points.isEmpty()) return emptyList()

    val line = Line2.fromPoints(p1, p2)
    val furthestPoint = points.maxBy { line.offset(it) }

    val p1FurthestPoint = Line2.fromPoints(p1, furthestPoint)
    val furthestPointP2 = Line2.fromPoints(furthestPoint, p2)

    val p1FurthestPointPlusSide = points.filter { p1FurthestPoint.location(it) == Location.PLUS }
    val furthestPointP2PlusSide = points.filter { furthestPointP2.location(it) == Location.PLUS }

    return findHull(p1FurthestPointPlusSide, p1, furthestPoint) +
      listOf(furthestPoint) +
      findHull(furthestPointP2PlusSide, furthestPoint, p2)
  }

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Convex Hull"

    override fun getName(): String = "Quickhull"

    override fun getTimeComplexity(): String = "O(n log n) average, O(n²) worst case"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
