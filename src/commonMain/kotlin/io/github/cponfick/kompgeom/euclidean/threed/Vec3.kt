package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.Vector3
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil

/**
 * An immutable 3-dimensional vector with double-precision components.
 *
 * @property x The x-component of the vector.
 * @property y The y-component of the vector.
 * @property z The z-component of the vector.
 */
public data class Vec3(
  public override val x: Double,
  public override val y: Double,
  public override val z: Double,
) : Vector3<Vec3> {

  public override operator fun plus(other: Vec3): Vec3 =
    Vec3(this.x + other.x, this.y + other.y, this.z + other.z)

  public override operator fun times(scalar: Double): Vec3 =
    Vec3(this.x * scalar, this.y * scalar, this.z * scalar)

  public override operator fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

  public override fun normalize(): Vec3 {
    val inverseNorm = VectorUtil.inverseNorm(this.x, this.y, this.z)
    return Vec3(x * inverseNorm, y * inverseNorm, z * inverseNorm)
  }

  public override operator fun minus(other: Vec3): Vec3 =
    Vec3(this.x - other.x, this.y - other.y, this.z - other.z)

  override fun zero(): Vec3 = ZERO

  override fun withComponents(x: Double, y: Double, z: Double): Vec3 = Vec3(x, y, z)

  /**
   * Creates an immutable copy of this vector.
   *
   * @return An immutable [Vec3] with the same components as this vector.
   */
  public fun toVec3(): Vec3 = Vec3(x, y, z)

  /**
   * Creates a mutable copy of this vector.
   *
   * @return A mutable [MutableVec3] with the same components as this immutable vector.
   */
  public fun toMutableVec3(): MutableVec3 = MutableVec3(x, y, z)

  public companion object {
    /** The zero vector. */
    public val ZERO: Vec3 = Vec3(0.0, 0.0, 0.0)

    /** Vector with all components set to positive infinity. */
    public val POSITIVE_INFINITY: Vec3 =
      Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

    /** Vector with all components set to negative infinity. */
    public val NEGATIVE_INFINITY: Vec3 =
      Vec3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

    /** Vector with all components set to NaN. */
    public val NaN: Vec3 = Vec3(Double.NaN, Double.NaN, Double.NaN)

    /**
     * Creates a [Vec3] from a generic [Vector3].
     *
     * @param vector The input vector.
     * @return A [Vec3] with the same components as the input vector.
     */
    public fun from(vector: Vector3<*>): Vec3 = Vec3(vector.x, vector.y, vector.z)
  }
}
