package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.Vector2

/**
 * Closest Pair algorithm using the Divide and Conquer approach.
 *
 * @property input Collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class ClosestPairDivideAndConquer<V : Vector2<V>>(input: Collection<V>) : ClosestPair<V> {
  private val points: List<V> = input.toList()
  private val sortedByX: List<Int> =
    points.indices.sortedWith(compareBy<Int> { points[it].x }.thenBy { it })
  private val sortedByY: List<Int> =
    points.indices.sortedWith(compareBy<Int> { points[it].y }.thenBy { it })

  init {
    require(input.size >= 2) { "Input must contain at least 2 elements" }
  }

  override fun execute(): Result<V> {
    val closestPair = closestPair(sortedByX, sortedByY)
    val distance = points[closestPair.first] distance points[closestPair.second]
    return Result(distance, Pair(points[closestPair.first], points[closestPair.second]))
  }

  private fun distance(pair: Pair<Int, Int>): Double =
    points[pair.first] distance points[pair.second]

  private fun closestSplitPair(
    px: List<Int>,
    py: List<Int>,
    d: Double,
    fallbackPair: Pair<Int, Int>,
  ): Pair<Int, Int> {
    val midX = points[px[px.size / 2]].x
    val sy = py.filter { points[it].x in (midX - d)..(midX + d) }

    var bestPair = fallbackPair
    var bestDistance = d

    for (i in sy.indices) {
      for (j in i + 1 until minOf(i + 7, sy.size)) {
        val p = sy[i]
        val q = sy[j]
        val distance = points[p] distance points[q]
        if (distance < bestDistance) {
          bestDistance = distance
          bestPair = Pair(p, q)
        }
      }
    }
    return bestPair
  }

  private fun closestPair(px: List<Int>, py: List<Int>): Pair<Int, Int> {
    if (px.size <= 3) {
      return closestPairNaive(px)
    }

    val mid = px.size / 2
    val leftPx = px.subList(0, mid)
    val rightPx = px.subList(mid, px.size)

    // Partition py by occurrence membership, matching the index split in px. Using x
    // coordinates here is incorrect when multiple points have the same x-coordinate.
    val isLeft = BooleanArray(points.size)
    leftPx.forEach { isLeft[it] = true }
    val leftPy = py.filter { isLeft[it] }
    val rightPy = py.filter { !isLeft[it] }

    val (p1, q1) = closestPair(leftPx, leftPy)
    val (p2, q2) = closestPair(rightPx, rightPy)

    val d1 = distance(Pair(p1, q1))
    val d2 = distance(Pair(p2, q2))
    val d = minOf(d1, d2)

    val bestHalfPair = if (d1 <= d2) Pair(p1, q1) else Pair(p2, q2)
    val (p3, q3) = closestSplitPair(px, py, d, bestHalfPair)

    return if (distance(Pair(p3, q3)) < d) Pair(p3, q3) else bestHalfPair
  }

  private fun closestPairNaive(indices: List<Int>): Pair<Int, Int> {
    var closestPair = Pair(indices[0], indices[1])
    var closestDistance = Double.POSITIVE_INFINITY

    for (i in indices.indices) {
      for (j in i + 1 until indices.size) {
        val pair = Pair(indices[i], indices[j])
        val distance = distance(pair)
        if (distance < closestDistance) {
          closestDistance = distance
          closestPair = pair
        }
      }
    }
    return closestPair
  }

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Closest Pair"

    override fun getName(): String = "Closest Pair Divide and Conquer"

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
