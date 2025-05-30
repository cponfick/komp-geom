package io.github.cponfick.core

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.sqrt

public class Vec2(public val x: Float = 0.0F, public val y: Float = 0.0F) {
  public constructor(vector: Vec2) : this(vector.x, vector.y)

  public constructor(a: Point2, b: Point2) : this(b.x - a.x, b.y - a.y)

  public val length: Float by lazy { sqrt(this.x * this.x + this.y * this.y) }

  public operator fun get(index: Int): Float {
    return when (index) {
      0 -> this.x
      1 -> this.y
      else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 2")
    }
  }

  public operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  public operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  public operator fun times(scalar: Float): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  public operator fun div(scalar: Float): Vec2 =
    if (scalar != 0F) Vec2(this.x / scalar, this.y / scalar)
    else throw ArithmeticException("Division by zero")

  public operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

  public infix fun dot(other: Vec2): Float = this.x * other.x + this.y * other.y

  public infix fun cross(other: Vec2): Float = this.x * other.y - this.y * other.x

  public fun perpendicular(): Vec2 = Vec2(-this.y, this.x)

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

  public fun approxEqual(other: Vec2, epsilon: Float = DEFAULT_EPSILON): Boolean =
    (this.x - other.x).absoluteValue < epsilon && (this.y - other.y).absoluteValue < epsilon

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
    public fun zero(): Vec2 = Vec2(0.0F, 0.0F)

    public fun one(): Vec2 = Vec2(1.0F, 1.0F)

    public fun unitX(): Vec2 = Vec2(1.0F, 0.0F)

    public fun unitY(): Vec2 = Vec2(0.0F, 1.0F)

    public operator fun Float.times(vector: Vec2): Vec2 = Vec2(this * vector.x, this * vector.y)

    public operator fun Float.div(vector: Vec2): Vec2 = Vec2(this / vector.x, this / vector.y)
  }
}
