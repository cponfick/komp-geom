package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.Vector3
import io.github.cponfick.kompgeom.euclidean.internal.VectorUtil

public data class MutableVec3(
  public override var x: Double,
  public override var y: Double,
  public override var z: Double,
) : Vector3<MutableVec3> {

  override fun withComponents(x: Double, y: Double, z: Double): MutableVec3 {
    this.x = x
    this.y = y
    this.z = z
    return this
  }

  override fun plus(other: MutableVec3): MutableVec3 {
    this.x += other.x
    this.y += other.y
    this.z += other.z
    return this
  }

  override fun times(scalar: Double): MutableVec3 {
    this.x *= scalar
    this.y *= scalar
    this.z *= scalar
    return this
  }

  override fun unaryMinus(): MutableVec3 {
    this.x = -this.x
    this.y = -this.y
    this.z = -this.z
    return this
  }

  override fun normalize(): MutableVec3 {
    val inverseNorm = VectorUtil.inverseNorm(this.x, this.y, this.z)
    this.x *= inverseNorm
    this.y *= inverseNorm
    this.z *= inverseNorm
    return this
  }

  override fun minus(other: MutableVec3): MutableVec3 {
    x -= other.x
    y -= other.y
    z -= other.z
    return this
  }

  override fun zero(): MutableVec3 = MutableVec3(0.0, 0.0, 0.0)

  /**
   * Converts this mutable vector to an immutable [Vec3].
   *
   * @return An immutable [Vec3] with the same components as this mutable vector.
   */
  public fun toVec3(): Vec3 = Vec3(x, y, z)

  /**
   * Creates a mutable copy of this vector.
   *
   * @return A new [MutableVec3] instance with the same components as this vector.
   */
  public fun toMutableVec3(): MutableVec3 = MutableVec3(x, y, z)

  public companion object {
    /**
     * Creates a zero vector.
     *
     * @return A [MutableVec3] instance with all components set to zero.
     */
    public fun zero(): MutableVec3 = MutableVec3(0.0, 0.0, 0.0)

    /**
     * Creates a vector with all components set to positive infinity.
     *
     * @return A [MutableVec3] instance with all components set to positive infinity.
     */
    public fun positiveInfinity(): MutableVec3 =
      MutableVec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

    /**
     * Creates a vector with all components set to negative infinity.
     *
     * @return A [MutableVec3] instance with all components set to negative infinity.
     */
    public fun negativeInfinity(): MutableVec3 =
      MutableVec3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

    /**
     * Creates a vector with all components set to NaN.
     *
     * @return A [MutableVec3] instance with all components set to NaN.
     */
    public fun nan(): MutableVec3 = MutableVec3(Double.NaN, Double.NaN, Double.NaN)
  }
}
