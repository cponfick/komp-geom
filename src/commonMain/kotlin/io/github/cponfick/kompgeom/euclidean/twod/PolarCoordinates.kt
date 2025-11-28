package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Spatial
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Represents a point in polar coordinates. See
 * [polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system) for more
 * information.
 *
 * @property radius The distance from the origin to the point.
 * @property azimuth The azimuth angle, in radians.
 */
public class PolarCoordinates(radius: Double, azimuth: Double) : Spatial {
  public val radius: Double
  public val azimuth: Double

  init {
    var r = radius
    var a = azimuth

    // if the radius is negative we have to flip  the angle
    if (r < 0) {
      r = abs(r)
      a += PI
    }

    this.radius = r
    this.azimuth = a % (2 * PI) // normalize the angle to [0, 2π)
  }

  override fun dimensions(): Int = 2

  override fun isFinite(): Boolean = radius.isFinite() && azimuth.isFinite()

  override fun isInfinite(): Boolean = !isNaN() && (radius.isInfinite() || azimuth.isInfinite())

  override fun isNaN(): Boolean = radius.isNaN() || azimuth.isNaN()

  override fun hashCode(): Int {
    if (isNaN()) {
      return 0
    }
    var result = radius.hashCode()
    result = 31 * result + azimuth.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PolarCoordinates) return false
    if (other.isNaN()) return this.isNaN()

    return radius == other.radius && azimuth == other.azimuth
  }

  override fun toString(): String {
    return "PolarCoordinate(radius=$radius, azimuth=$azimuth)"
  }

  public fun toCartesian(): Vec2 = toCartesian(radius, azimuth)

  public companion object {
    /**
     * Creates a [PolarCoordinates] instance from Cartesian coordinates.
     *
     * @param x The x-coordinate in Cartesian coordinates.
     * @param y The y-coordinate in Cartesian coordinates.
     * @return A [PolarCoordinates] instance representing the same point in polar coordinates.
     */
    public fun fromCartesian(x: Double, y: Double): PolarCoordinates {
      val azimuth = atan2(y, x)
      val radius = hypot(x, y)
      return PolarCoordinates(radius, azimuth)
    }

    /**
     * Creates a [PolarCoordinates] instance from Cartesian coordinates.
     *
     * @param v The Cartesian coordinates as a [Vec2] instance.
     * @return A [PolarCoordinates] instance representing the same point in polar coordinates.
     */
    public fun fromCartesian(v: Vec2): PolarCoordinates = fromCartesian(v.x, v.y)

    /**
     * Converts polar coordinates to Cartesian coordinates.
     *
     * @param radius The distance from the origin to the point.
     * @param azimuth The azimuth angle, in radians.
     * @return A [Vec2] instance representing the Cartesian coordinates.
     */
    public fun toCartesian(radius: Double, azimuth: Double): Vec2 {
      val x = radius * cos(azimuth)
      val y = radius * sin(azimuth)
      return Vec2(x, y)
    }
  }
}
