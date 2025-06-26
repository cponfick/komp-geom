package io.github.cponfick.kompgeom.euclidean

import io.github.cponfick.kompgeom.core.AngleUnit
import kotlin.math.PI
import kotlin.math.acos

public abstract class MultiDimensionalEuclideanVector<V : MultiDimensionalEuclideanVector<V>> :
  EuclideanVector<V>() {

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

  override fun angle(other: V, angleUnit: AngleUnit): Double {
    val dotProduct = this dot other
    val lengthsProduct = (this.norm() * other.norm()).assertIsFiniteAndNotZero()
    val cosAlpha = dotProduct / lengthsProduct

    val angle = acos(cosAlpha)

    return when (angleUnit) {
      AngleUnit.RADIANS -> angle
      AngleUnit.DEGREES -> angle * RADIANS_TO_DEGREES
    }
  }

  private companion object {
    private const val RADIANS_TO_DEGREES: Double = 180.0 / PI
  }
}
