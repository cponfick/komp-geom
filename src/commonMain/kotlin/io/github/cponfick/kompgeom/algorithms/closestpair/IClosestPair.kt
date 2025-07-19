// package io.github.cponfick.kompgeom.algorithms.closestpair
//
// import io.github.cponfick.kompgeom.algorithms.IAlgorithm
// import io.github.cponfick.kompgeom.euclidean.EuclideanVector
//
/// **
// * Represents the result of a closest pair algorithm.
// *
// * @property distance The distance between the closest pair of points.
// * @property result A pair of points that are the closest to each other.
// */
// public class Result<V>(
//  public val distance: Double,
//  public val result: Pair<V, V>,
// )
//
/// **
// * Interface for closest pair algorithms.
// *
// * This interface defines the contract for algorithms that find the closest pair of points in a
// * collection of points in 2D space.
// */
// public interface IClosestPair<V> : IAlgorithm<Result<V>> {
//  override fun getGroup(): String = "Closest Pair"
// }
