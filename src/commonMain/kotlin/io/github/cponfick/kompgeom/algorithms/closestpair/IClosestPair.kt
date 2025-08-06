package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.core.Vector

/**
 * Represents the result of the closest pair algorithm.
 *
 * @property distance The distance between the closest pair of points.
 * @property result A pair of points that are the closest to each other.
 */
public class Result<V : Vector<V>>(public val distance: Double, public val result: Pair<V, V>)

/**
 * Interface for closest pair algorithms.
 *
 * This interface defines the contract for algorithms that find the closest pair of points in a
 * collection of points in 2D space.
 */
public interface IClosestPair<V : Vector<V>> : IAlgorithm<Result<V>>
