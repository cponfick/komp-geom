package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.Vector2

public class Result<V : Vector2<V>>(public val points: Collection<V>)

public interface ConvexHull<V : Vector2<V>> : Algorithm<Result<V>>
