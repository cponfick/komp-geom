package io.github.cponfick.kompgeom.kompgeom.core

import io.github.cponfick.kompgeom.kompgeom.core.Vec2.Companion.times
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI
import kotlin.test.Test

class Vec2Test {
  @Test
  fun `default constructor initializes to zero`() {
    val vector = Vec2()
    vector.x shouldBe 0.0F
    vector.y shouldBe 0.0F
  }

  @Test
  fun `constructor with parameters initializes correctly`() {
    val vector = Vec2(1.0F, 2.0F)
    vector.x shouldBe 1.0F
    vector.y shouldBe 2.0F
  }

  @Test
  fun `copy constructor creates an identical vector`() {
    val original = Vec2(3.0F, 4.0F)
    val copy = Vec2(original)
    original shouldBe copy
  }

  @Test
  fun `constructor with Point2 creates correct vector`() {
    val a = Point2(5.0F, 6.0F)
    val b = Point2(7.0F, 8.0F)
    val vector = Vec2(a, b)
    vector.x shouldBe 2.0F
    vector.y shouldBe 2.0F
  }

  @Test
  fun `zero method returns zero vector`() {
    val vector = Vec2.zero()
    vector shouldBe Vec2(0.0F, 0.0F)
  }

  @Test
  fun `one method returns unit vector`() {
    val vector = Vec2.one()
    vector shouldBe Vec2(1.0F, 1.0F)
  }

  @Test
  fun `unitX method returns unit vector along X axis`() {
    val vector = Vec2.unitX()
    vector shouldBe Vec2(1.0F, 0.0F)
  }

  @Test
  fun `unitY method returns unit vector along Y axis`() {
    val vector = Vec2.unitY()
    vector shouldBe Vec2(0.0F, 1.0F)
  }

  @Test
  fun `plus operator adds two vectors`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    val result = vector1 + vector2
    result shouldBe Vec2(4.0F, 6.0F)
  }

  @Test
  fun `minus operator subtracts two vectors`() {
    val vector1 = Vec2(5.0F, 6.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    val result = vector1 - vector2
    result shouldBe Vec2(2.0F, 2.0F)
  }

  @Test
  fun `times operator multiplies vector by scalar`() {
    val vector = Vec2(1.0F, 2.0F)
    val scalar = 3.0F
    val result = vector * scalar
    result shouldBe Vec2(3.0F, 6.0F)
  }

  @Test
  fun `scalar times operator multiplies scalar by vector`() {
    val vector = Vec2(1.0F, 2.0F)
    val scalar = 3.0F
    val result = scalar * vector
    result shouldBe Vec2(3.0F, 6.0F)
  }

  @Test
  fun `div operator divides vector by scalar`() {
    val vector = Vec2(4.0F, 8.0F)
    val scalar = 2.0F
    val result = vector / scalar
    result shouldBe Vec2(2.0F, 4.0F)
  }

  @Test
  fun `div operator throws exception when dividing by zero`() {
    val vector = Vec2(4.0F, 8.0F)
    val scalar = 0.0F
    shouldThrow<ArithmeticException> { vector / scalar }
  }

  @Test
  fun `dot operator calculates dot product of two vectors`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    val result = vector1 dot vector2
    result shouldBe 11.0F
  }

  @Test
  fun `cross operator calculates cross product of two vectors`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    val result = vector1 cross vector2
    result shouldBe -2.0F
  }

  @Test
  fun `length property calculates length of vector`() {
    val vector = Vec2(3.0F, 4.0F)
    val result = vector.length
    result shouldBe 5.0F
  }

  @Test
  fun `normalize method normalizes vector`() {
    val vector = Vec2(3.0F, 4.0F)
    val result = vector.normalize()
    result shouldBe Vec2(0.6F, 0.8F)
  }

  @Test
  fun `get operator retrieves x and y components`() {
    val vector = Vec2(1.0F, 2.0F)
    vector[0] shouldBe 1.0F
    vector[1] shouldBe 2.0F
  }

  @Test
  fun `get operator throws exception for out of bounds index`() {
    val vector = Vec2(1.0F, 2.0F)
    shouldThrow<IndexOutOfBoundsException> { vector[2] }
  }

  @Test
  fun `equality checks for identical vectors`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(1.0F, 2.0F)
    vector1 shouldBe vector2
  }

  @Test
  fun `equality checks for different vectors`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    vector1 shouldNotBe vector2
  }

  @Test
  fun `negative zero vectors are equal to positive zero vectors`() {
    val vector1 = Vec2(-0.0F, -0.0F)
    val vector2 = Vec2(0.0F, 0.0F)
    vector1 shouldBe vector2
  }

  @Test
  fun `approxEqual method checks for approximate equality`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(1.00001F, 2.00001F)
    vector1.approxEqual(vector2) shouldBe true
  }

  @Test
  fun `approxEqual method checks for non-approximate equality`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(3.0F, 4.0F)
    vector1.approxEqual(vector2) shouldBe false
  }

  @Test
  fun `approxEqual method checks with custom epsilon`() {
    val vector1 = Vec2(1.0F, 2.0F)
    val vector2 = Vec2(1.0005F, 2.0005F)
    vector1.approxEqual(vector2, 0.001F) shouldBe true
  }

  @Test
  fun `approxEqual method checks with zero vector`() {
    val vector1 = Vec2(0.0F, 0.0F)
    val vector2 = Vec2(0.00001F, 0.00001F)
    vector1.approxEqual(vector2) shouldBe true
  }

  @Test
  fun `approxEqual method checks with negative vector`() {
    val vector1 = Vec2(-1.0F, -2.0F)
    val vector2 = Vec2(-1.00001F, -2.00001F)
    vector1.approxEqual(vector2) shouldBe true
  }

  @Test
  fun `angle method calculates angle between two vectors in radians`() {
    val vector1 = Vec2(1.0F, 0.0F)
    val vector2 = Vec2(0.0F, 1.0F)
    val angle = vector1.angle(vector2, AngleUnit.RADIANS)
    angle shouldBe (PI.toFloat() / 2F)
  }

  @Test
  fun `angle method calculates angle between two vectors in degrees`() {
    val vector1 = Vec2(1.0F, 0.0F)
    val vector2 = Vec2(0.0F, 1.0F)
    val angle = vector1.angle(vector2, AngleUnit.DEGREES)
    angle shouldBe 90.0F
  }

  @Test
  fun `perpendicular method returns perpendicular vector`() {
    val vector = Vec2(1.0F, 2.0F)
    val result = vector.perpendicular()
    result shouldBe Vec2(-2.0F, 1.0F)
  }
}
