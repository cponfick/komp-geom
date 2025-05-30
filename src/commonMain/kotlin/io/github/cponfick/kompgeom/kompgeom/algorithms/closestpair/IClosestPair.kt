package io.github.cponfick.kompgeom.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.kompgeom.core.Point2

public class Result(public val distance: Float, public val result: Pair<Point2, Point2>)

public interface IClosestPair : IAlgorithm<Result> {
  override fun getGroup(): String {
    return "Closest Pair"
  }
}
