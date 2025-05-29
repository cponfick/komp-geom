package core

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.sqrt

data class Vec2(var x: Float = 0.0F, var y: Float = 0.0F) {
  constructor(vector: Vec2) : this(vector.x, vector.y)

  constructor(a: Point2, b: Point2) : this(b.x - a.x, b.y - a.y)

  val length: Float by lazy { sqrt(this.x * this.x + this.y * this.y) }

  operator fun get(index: Int): Float {
    return when (index) {
      0 -> this.x
      1 -> this.y
      else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 2")
    }
  }

  operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

  operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

  operator fun times(scalar: Float): Vec2 = Vec2(this.x * scalar, this.y * scalar)

  operator fun div(scalar: Float): Vec2 =
      if (scalar != 0F) Vec2(this.x / scalar, this.y / scalar)
      else throw ArithmeticException("Division by zero")

  operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

  infix fun dot(other: Vec2): Float = this.x * other.x + this.y * other.y

  infix fun cross(other: Vec2): Float = this.x * other.y - this.y * other.x

  fun perpendicular(): Vec2 = Vec2(-this.y, this.x)

  fun angle(other: Vec2, angleUnit: AngleUnit = AngleUnit.RADIANS): Float {
    val dotProduct = this dot other
    val lengthsProduct = this.length * other.length
    val cosTheta = dotProduct / lengthsProduct

    val angle = acos(cosTheta)

    return when (angleUnit) {
      AngleUnit.RADIANS -> angle
      AngleUnit.DEGREES -> angle * (180f / PI.toFloat())
    }
  }

  fun approxEqual(other: Vec2, epsilon: Float = DEFAULT_EPSILON): Boolean =
      (this.x - other.x).absoluteValue < epsilon && (this.y - other.y).absoluteValue < epsilon

  fun normalize(): Vec2 {
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

  companion object {
    fun zero(): Vec2 = Vec2(0.0F, 0.0F)

    fun one(): Vec2 = Vec2(1.0F, 1.0F)

    fun unitX(): Vec2 = Vec2(1.0F, 0.0F)

    fun unitY(): Vec2 = Vec2(0.0F, 1.0F)

    operator fun Float.times(vector: Vec2): Vec2 = Vec2(this * vector.x, this * vector.y)

    operator fun Float.div(vector: Vec2): Vec2 = Vec2(this / vector.x, this / vector.y)
  }
}
