package io.github.cponfick.kompgeom.core

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Represents a 2D vector with floating-point coordinates.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 * @constructor Creates a vector with the specified x and y coordinates.
 */
public class Vec2(public val x: Float = 0.0F, public val y: Float = 0.0F) {
  /**
   * Creates a new vector by copying the coordinates from another vector.
   *
   * @param vector The vector to copy.
   */
  public constructor(vector: Vec2) : this(vector.x, vector.y)

  /**
   * Creates a vector from two points, representing the direction and distance from point `a` to
   * point `b`.
   *
   * @param a The starting point.
   * @param b The ending point.
   */
  public constructor(a: Point2, b: Point2) : this(b.x - a.x, b.y - a.y)

  /**
   * Returns the length of the vector, calculated as the Euclidean distance from the origin.
   *
   * @return The length of the vector.
   */
  public val length: Float by lazy { sqrt(this.x * this.x + this.y * this.y) }

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
   * Multiplies this vector with another vector component-wise.
   *
   * @param other The vector to multiply with.
   * @return A new Vec2 representing the component-wise product.
   */
  public operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  /**
   * Subtracts another vector from this vector component-wise.
   *
   * @param other The vector to subtract.
   * @return A new Vec2 representing the component-wise difference.
   */
  public operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  /**
   * Multiplies this vector by a scalar value.
   *
   * @param scalar The scalar value to multiply the vector by.
   * @return A new Vec2 representing the scaled vector.
   */
  public operator fun times(scalar: Float): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  /**
   * Divides this vector by a scalar value.
   *
   * @param scalar The scalar value to divide the vector by.
   * @return A new Vec2 representing the scaled vector.
   * @throws ArithmeticException if the scalar is zero.
   */
  public operator fun div(scalar: Float): Vec2 =
    if (scalar != 0F) Vec2(this.x / scalar, this.y / scalar)
    else throw ArithmeticException("Division by zero")

  /**
   * Negates the vector, flipping its direction.
   *
   * @return A new Vec2 representing the negated vector.
   */
  public operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

  /**
   * Calculates the dot product of this vector with another vector.
   *
   * @param other The vector to calculate the dot product with.
   * @return The dot product.
   */
  public infix fun dot(other: Vec2): Float = this.x * other.x + this.y * other.y

  /**
   * Calculates the cross product of this vector with another vector.
   *
   * @param other The vector to calculate the cross product with.
   * @return The scalar value representing the cross product.
   */
  public infix fun cross(other: Vec2): Float = this.x * other.y - this.y * other.x

  /**
   * Calculates the perpendicular vector, which is a 90-degree rotation of this vector.
   *
   * @return A new Vec2 representing the perpendicular vector.
   */
  public fun perpendicular(): Vec2 = Vec2(-this.y, this.x)

  /**
   * Calculates the angle between this vector and another vector.
   *
   * @param other The vector to calculate the angle with.
   * @param angleUnit The unit of the angle (default is radians).
   * @return The angle in the specified unit.
   */
  public fun angle(other: Vec2, angleUnit: AngleUnit = AngleUnit.RADIANS): Float {
    val dotProduct = this dot other
    val lengthsProduct = this.length * other.length
    val cosTheta = dotProduct / lengthsProduct

    val angle = acos(cosTheta)

    return when (angleUnit) {
      AngleUnit.RADIANS -> angle
      AngleUnit.DEGREES -> angle * (180f / PI.toFloat())
    }
  }

  /**
   * Checks if this vector is approximately equal to another vector within a specified epsilon.
   *
   * @param other The vector to compare with.
   * @param epsilon The tolerance for comparison (default is 1e-6).
   * @return True if the vectors are approximately equal, false otherwise.
   */
  public fun approxEqual(other: Vec2, epsilon: Float = DEFAULT_EPSILON): Boolean =
    (this.x - other.x).absoluteValue < epsilon && (this.y - other.y).absoluteValue < epsilon

  /**
   * Normalizes the vector to a unit vector (length of 1). If the vector is zero, it returns a zero
   * vector.
   *
   * @return A new Vec2 representing the normalized vector.
   */
  public fun normalize(): Vec2 {
    val len = length
    return if (len > 0) {
      Vec2(this.x / len, this.y / len)
    } else {
      Vec2(0.0F, 0.0F)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (other !is Vec2) {
      return false
    }
    return this.x == other.x && this.y == other.y
  }

  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    return result
  }

  public companion object {
    /** Creates a zero vector (0, 0). */
    public fun zero(): Vec2 = Vec2(0.0F, 0.0F)

    /** Creates a unit vector (1, 1). */
    public fun one(): Vec2 = Vec2(1.0F, 1.0F)

    /** Creates a unit vector in the x direction (1, 0). */
    public fun unitX(): Vec2 = Vec2(1.0F, 0.0F)

    /** Creates a unit vector in the y direction (0, 1). */
    public fun unitY(): Vec2 = Vec2(0.0F, 1.0F)

    /**
     * Multiplies a Float scalar by a vector component-wise.
     *
     * @param vector The vector to multiply.
     * @return A new Vec2 representing the scaled vector.
     */
    public operator fun Float.times(vector: Vec2): Vec2 = Vec2(this * vector.x, this * vector.y)

    /**
     * Divides a Float scalar by a vector component-wise.
     *
     * @param vector The vector to divide.
     * @return A new Vec2 representing the scaled vector.
     */
    public operator fun Float.div(vector: Vec2): Vec2 = Vec2(this / vector.x, this / vector.y)
  }
}
