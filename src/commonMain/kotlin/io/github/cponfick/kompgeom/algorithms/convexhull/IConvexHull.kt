package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.core.Vector
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

public class Result<V : Vector<Vec2>>(public val points: Collection<V>)

public interface IConvexHull<V : Vector<Vec2>> : IAlgorithm<Result<V>>
