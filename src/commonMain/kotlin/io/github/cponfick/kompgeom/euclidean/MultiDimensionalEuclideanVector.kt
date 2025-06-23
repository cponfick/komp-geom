package io.github.cponfick.kompgeom.euclidean

public abstract class MultiDimensionalEuclideanVector<V : MultiDimensionalEuclideanVector<V>> :
  EuclideanVector<V>() {

  /**
   * Calculate the orthogonal vector to this vector.
   *
   * @return The unit vector that is orthogonal to this vector.
   */
  public abstract fun orthogonal(): V

  /**
   * Calculate the orthogonal vector to this vector in the specified direction.
   *
   * @param direction The direction in which to calculate the orthogonal vector.
   * @return The unit vector that is orthogonal to this vector in the specified direction.
   */
  public abstract fun orthogonal(direction: V): V

  /**
   * Calculate the projection of this vector onto another vector.
   *
   * @param base The vector onto which to project this vector.
   * @return The projection of this vector onto the other vector.
   */
  public abstract fun project(base: V): V

  /**
   * Calculates the rejection of this vector from another vector.
   *
   * @param base The vector from which to reject this vector.
   * @return The rejection of this vector from the other vector.
   */
  public abstract fun reject(base: V): V
}
