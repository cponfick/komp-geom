package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.core.Vector2

public class Result<V : Vector2<V>>(public val points: Collection<V>)

public interface IConvexHull<V : Vector2<V>> : IAlgorithm<Result<V>>
