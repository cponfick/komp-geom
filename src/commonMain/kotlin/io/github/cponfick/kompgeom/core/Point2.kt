package io.github.cponfick.kompgeom.core

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a 2D point in Cartesian coordinates.
 *
 * @property x The x-coordinate of the point.
 * @property y The y-coordinate of the point.
 * @constructor Creates a point with the specified x and y coordinates.
 */
public class Point2(public val x: Float = 0.0F, public val y: Float = 0.0F) {
  /**
   * Creates a new point by copying the coordinates from another point.
   *
   * @param point The point to copy.
   */
  public constructor(point: Point2) : this(point.x, point.y)

  /**
   * Get the coordinate at the specified index.
   *
   * @return The x for index 0, y for index 1.
   */
  public operator fun get(index: Int): Float {
    return when (index) {
      0 -> this.x
      1 -> this.y
      else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 2")
    }
  }

  /**
   * Multiplies this point by a scalar value.
   *
   * @param scalar The scalar value to multiply the point by.
   * @return A new Point2 representing the scaled point.
   */
  public operator fun times(scalar: Float): Point2 = Point2(this.x * scalar, this.y * scalar)

  /**
   * Calculates the Euclidean distance between this point and another point.
   *
   * @param other The other point to calculate the distance to.
   * @return The distance between the two points.
   */
  public infix fun distance(other: Point2): Float {
    val dx = this.x - other.x
    val dy = this.y - other.y
    return sqrt(dx * dx + dy * dy)
  }

  /**
   * Rotates this point around the origin by the specified angle.
   *
   * @param angle The angle to rotate, in radians or degrees.
   * @param angleUnit The unit of the angle (default is radians).
   * @return A new Point2 representing the rotated point.
   */
  public fun rotate(angle: Float, angleUnit: AngleUnit = AngleUnit.RADIANS): Point2 {
    val angle =
      when (angleUnit) {
        AngleUnit.RADIANS -> angle
        AngleUnit.DEGREES -> (angle * PI.toFloat() / 180f)
      }

    return when (angle) {
      0f,
      2 * PI.toFloat() -> Point2(x, y)
      PI.toFloat() / 2 -> Point2(-y, x)
      PI.toFloat() -> Point2(-x, -y)
      3 * PI.toFloat() / 2 -> Point2(y, -x)
      else -> {
        val cosAngle = cos(angle)
        val sinAngle = sin(angle)

        Point2((x * cosAngle) - (y * sinAngle), (x * sinAngle) + (y * cosAngle))
      }
    }
  }

  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    if (other !is Point2) {
      return false
    }
    return this.x == other.x && this.y == other.y
  }
}
