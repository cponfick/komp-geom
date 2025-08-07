package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

/**
 * Closest Pair algorithm using the Divide and Conquer approach.
 *
 * @property input Collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class ClosestPairDivideAndConquer(input: Collection<Vec2>) : IClosestPair<Vec2> {
  private val sortedByX: List<Vec2> = input.sortedBy { it.x }
  private val sortedByY: List<Vec2> = input.sortedBy { it.y }

  init {
    require(input.size >= 2) { "Input must contain at least 2 elements" }
  }

  override fun execute(): Result<Vec2> {
    val closestPair = closestPair(sortedByX, sortedByY)
    val distance = closestPair.first distance closestPair.second
    return Result(distance, closestPair)
  }

  private fun closestSplitPair(px: List<Vec2>, py: List<Vec2>, d: Double): Pair<Vec2, Vec2> {
    val midX = px[px.size / 2].x
    val sy = py.filter { it.x in (midX - d)..(midX + d) }

    var bestPair = Pair(px[0], px[1])
    var bestDistance = d

    for (i in sy.indices) {
      for (j in i + 1 until minOf(i + 7, sy.size)) {
        val p = sy[i]
        val q = sy[j]
        val distance = p distance q
        if (distance < bestDistance) {
          bestDistance = distance
          bestPair = Pair(p, q)
        }
      }
    }
    return bestPair
  }

  private fun closestPair(px: List<Vec2>, py: List<Vec2>): Pair<Vec2, Vec2> {
    if (px.size <= 3) {
      return ClosestPairNaive(px).execute().result
    }

    val mid = px.size / 2
    val leftPx = px.subList(0, mid)
    val rightPx = px.subList(mid, px.size)

    val leftPy = py.filter { it.x <= leftPx.last().x }
    val rightPy = py.filter { it.x > leftPx.last().x }

    val (p1, q1) = closestPair(leftPx, leftPy)
    val (p2, q2) = closestPair(rightPx, rightPy)

    val d1 = p1 distance q1
    val d2 = p2 distance q2
    val d = minOf(d1, d2)

    val (p3, q3) = closestSplitPair(px, py, d)

    return when {
      d1 < d2 && d1 < (p3 distance q3) -> Pair(p1, q1)
      d2 < d1 && d2 < (p3 distance q3) -> Pair(p2, q2)
      else -> Pair(p3, q3)
    }
  }

  public companion object : IAlgorithm.IAlgorithmInfo {
    override fun getGroup(): String = "Closest Pair"

    override fun getName(): String = "Closest Pair Divide and Conquer"

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
