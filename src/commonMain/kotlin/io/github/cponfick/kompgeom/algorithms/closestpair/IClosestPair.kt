package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.algorithms.IAlgorithm
import io.github.cponfick.kompgeom.core.Point2

/**
 * Represents the result of a closest pair algorithm.
 *
 * @property distance The distance between the closest pair of points.
 * @property result A pair of points that are the closest to each other.
 */
public class Result(public val distance: Float, public val result: Pair<Point2, Point2>)

/**
 * Interface for closest pair algorithms.
 *
 * This interface defines the contract for algorithms that find the closest pair of points in a
 * collection of points in 2D space.
 */
public interface IClosestPair : IAlgorithm<Result> {
  override fun getGroup(): String = "Closest Pair"
}
