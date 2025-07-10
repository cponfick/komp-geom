package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.euclidean.MultiDimensionalEuclideanVector

public class Result<V : MultiDimensionalEuclideanVector<V>>(public val points: List<V>)

public interface IConvexHull<V : MultiDimensionalEuclideanVector<V>> : IAlgorithm<Result<V>> {
  override fun getGroup(): String = "Convex Hull"
}
