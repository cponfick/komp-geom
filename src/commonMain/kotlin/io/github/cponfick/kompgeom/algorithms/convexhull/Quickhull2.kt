package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.Vector2
import io.github.cponfick.kompgeom.core.shapes.Location
import io.github.cponfick.kompgeom.euclidean.twod.Line2

/**
 * Quickhull algorithm for computing the convex hull of a collection of 2D points.
 *
 * @property input Collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class Quickhull2<V : Vector2<V>>(private val input: Collection<V>) : ConvexHull<V> {

  init {
    require(input.size >= 3) { "Input must contain at least 3 elements" }
  }

  override fun execute(): Result<V> {
    val minX = input.minBy { it.x }
    val maxX = input.maxBy { it.x }

    val line = Line2.fromPoints(minX, maxX)
    val plusSide = input.filter { line.location(it) == Location.PLUS }
    val minusSide = input.filter { line.location(it) == Location.MINUS }

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

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
