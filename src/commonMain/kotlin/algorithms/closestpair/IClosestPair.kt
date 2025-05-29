package algorithms.closestpair

import algorithms.IAlgorithm
import core.Point2

data class Result(val distance: Float, val result: Pair<Point2, Point2>)

interface IClosestPair : IAlgorithm<Result> {
  override fun getGroup(): String {
    return "Closest Pair"
  }
}
