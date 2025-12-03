package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.core.shapes.Location
import io.github.cponfick.kompgeom.euclidean.twod.Line2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

/**
 * Quickhull algorithm for computing the convex hull of a collection of 2D points.
 *
 * @property input Collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class Quickhull2(private val input: Collection<Vec2>) : IConvexHull<Vec2> {

  private val convexHull = mutableSetOf<Vec2>()

  init {
    require(input.size >= 3) { "Input must contain at least 3 elements" }
  }

  override fun execute(): Result<Vec2> {
    val minX = input.minByOrNull { it.x }!!
    val maxX = input.maxByOrNull { it.x }!!

    val line = Line2.fromPoints(minX, maxX)
    val plusSide = input.filter { line.location(it) == Location.PLUS }
    val minusSide = input.filter { line.location(it) == Location.MINUS }
    convexHull.add(minX)
    convexHull.add(maxX)

    findHull(plusSide, minX, maxX)
    findHull(minusSide, maxX, minX)
    return Result(convexHull)
  }

  private fun findHull(points: List<Vec2>, p1: Vec2, p2: Vec2) {
    if (points.isEmpty()) return

    val line = Line2.fromPoints(p1, p2)
    val furthestPoint = points.maxByOrNull { line.offset(it) }!!
    convexHull.add(furthestPoint)

    val p1FurthestPoint = Line2.fromPoints(p1, furthestPoint)
    val furthestPointP2 = Line2.fromPoints(furthestPoint, p2)

    val p1FurthestPointPlusSide = points.filter { p1FurthestPoint.location(it) == Location.PLUS }
    val furthestPointP2PlusSide = points.filter { furthestPointP2.location(it) == Location.PLUS }

    findHull(p1FurthestPointPlusSide, p1, furthestPoint)
    findHull(furthestPointP2PlusSide, furthestPoint, p2)
  }

  public companion object : IAlgorithm.IAlgorithmInfo {
    override fun getGroup(): String = "Convex Hull"

    override fun getName(): String = "Quickhull"

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
