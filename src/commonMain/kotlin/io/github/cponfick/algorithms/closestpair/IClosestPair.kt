package io.github.cponfick.algorithms.closestpair

import io.github.cponfick.algorithms.IAlgorithm
import io.github.cponfick.core.Point2

public class Result(public val distance: Float, public val result: Pair<Point2, Point2>)

public interface IClosestPair : IAlgorithm<Result> {
  override fun getGroup(): String {
    return "Closest Pair"
  }
}
