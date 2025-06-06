package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.core.Point2

/**
 * Closest Pair algorithm using the Divide and Conquer approach. This implementation sorts the
 * points by x and y coordinates and recursively finds the closest pair.
 *
 * @property input collection of points in 2D space.
 * @constructor initializes the algorithm with a collection of points.
 */
public class ClosestPairNaive(input: Collection<Point2>) : IClosestPair {
  private val points = input.toList()

  init {
    if (input.size < 2) {
      throw IllegalArgumentException("Input must contain at least 2 elements")
    }
  }

  override fun run(): Result {
    var closestPair = Pair(points[0], points[1])
    var closestDistance = Float.POSITIVE_INFINITY

    for (i in points.indices) {
      for (j in i + 1 until points.size) {
        val distance = points[i] distance points[j]
        if (distance < closestDistance) {
          closestDistance = distance
          closestPair = Pair(points[i], points[j])
        }
      }
    }
    return Result(closestDistance, closestPair)
  }

  override fun getName(): String = "Closest Pair Naive"
}
