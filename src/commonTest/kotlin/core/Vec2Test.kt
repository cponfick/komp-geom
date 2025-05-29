package core

import core.Vec2.Companion.times
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI

class Vec2Test :
  DescribeSpec({
    describe("constructor") {
      it("default constructor") {
        val vector = Vec2()
        vector.x shouldBe 0.0F
        vector.y shouldBe 0.0F
      }

      it("constructor with parameters") {
        val vector = Vec2(1.0F, 2.0F)
        vector.x shouldBe 1.0F
        vector.y shouldBe 2.0F
      }

      it("copy constructor") {
        val original = Vec2(3.0F, 4.0F)
        val copy = Vec2(original)
        original shouldBe copy
      }

      it("constructor with Point2") {
        val a = Point2(5.0F, 6.0F)
        val b = Point2(7.0F, 8.0F)
        val vector = Vec2(a, b)
        vector.x shouldBe 2.0F
        vector.y shouldBe 2.0F
      }
    }

    describe("companion object") {
      it("zero method") {
        val vector = Vec2.zero()
        vector shouldBe Vec2(0.0F, 0.0F)
      }

      it("one method") {
        val vector = Vec2.one()
        vector shouldBe Vec2(1.0F, 1.0F)
      }

      it("unitX method") {
        val vector = Vec2.unitX()
        vector shouldBe Vec2(1.0F, 0.0F)
      }

      it("unitY method") {
        val vector = Vec2.unitY()
        vector shouldBe Vec2(0.0F, 1.0F)
      }
    }

    describe("plus operator") {
      it("adding two Float2 objects") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        val result = vector1 + vector2
        result shouldBe Vec2(4.0F, 6.0F)
      }
    }

    describe("minus operator") {
      it("subtracting two Float2 objects") {
        val vector1 = Vec2(5.0F, 6.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        val result = vector1 - vector2
        result shouldBe Vec2(2.0F, 2.0F)
      }
    }

    describe("times operator") {
      it("multiplying Float2 by a scalar") {
        val vector = Vec2(1.0F, 2.0F)
        val scalar = 3.0F
        val result = vector * scalar
        result shouldBe Vec2(3.0F, 6.0F)
      }
      it("multiplying a scalar by Float2") {
        val vector = Vec2(1.0F, 2.0F)
        val scalar = 3.0F
        val result = scalar * vector
        result shouldBe Vec2(3.0F, 6.0F)
      }
    }

    describe("div operator") {
      it("dividing Float2 by a scalar") {
        val vector = Vec2(4.0F, 8.0F)
        val scalar = 2.0F
        val result = vector / scalar
        result shouldBe Vec2(2.0F, 4.0F)
      }

      it("dividing by zero") {
        val vector = Vec2(4.0F, 8.0F)
        val scalar = 0.0F
        shouldThrow<ArithmeticException> { vector / scalar }
      }
    }

    describe("dot operator") {
      it("dot product of two Float2 objects") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        val result = vector1 dot vector2
        result shouldBe 11.0F
      }
    }

    describe("cross operator") {
      it("cross product of two Float2 objects") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        val result = vector1 cross vector2
        result shouldBe -2.0F
      }
    }

    describe("length property") {
      it("length of a Float2 object") {
        val vector = Vec2(3.0F, 4.0F)
        val result = vector.length
        result shouldBe 5.0F
      }
    }

    describe("normalize method") {
      it("normalizing a Float2 object") {
        val vector = Vec2(3.0F, 4.0F)
        val result = vector.normalize()
        result shouldBe Vec2(0.6F, 0.8F)
      }
    }

    describe("get operator") {
      it("getting x and y components") {
        val vector = Vec2(1.0F, 2.0F)
        vector[0] shouldBe 1.0F
        vector[1] shouldBe 2.0F
      }

      it("index out of bounds") {
        val vector = Vec2(1.0F, 2.0F)
        shouldThrow<IndexOutOfBoundsException> { vector[2] }
      }
    }

    describe("equality") {
      it("equal vectors") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(1.0F, 2.0F)
        vector1 shouldBe vector2
      }

      it("not equal vectors") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        vector1 shouldNotBe vector2
      }

      it("negative 0 vectors") {
        val vector1 = Vec2(-0.0F, -0.0F)
        val vector2 = Vec2(0.0F, 0.0F)
        vector1 shouldBe vector2
      }
    }

    describe("approxEqual method") {
      it("approximately equal vectors") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(1.00001F, 2.00001F)
        vector1.approxEqual(vector2) shouldBe true
      }

      it("not approximately equal vectors") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(3.0F, 4.0F)
        vector1.approxEqual(vector2) shouldBe false
      }

      it("approximately equal with custom epsilon") {
        val vector1 = Vec2(1.0F, 2.0F)
        val vector2 = Vec2(1.0005F, 2.0005F)
        vector1.approxEqual(vector2, 0.001F) shouldBe true
      }

      it("approximately equal with zero vector") {
        val vector1 = Vec2(0.0F, 0.0F)
        val vector2 = Vec2(0.00001F, 0.00001F)
        vector1.approxEqual(vector2) shouldBe true
      }

      it("approximately equal with negative vector") {
        val vector1 = Vec2(-1.0F, -2.0F)
        val vector2 = Vec2(-1.00001F, -2.00001F)
        vector1.approxEqual(vector2) shouldBe true
      }
    }

    describe("angle method") {
      it("angle between two vectors in radians") {
        val vector1 = Vec2(1.0F, 0.0F)
        val vector2 = Vec2(0.0F, 1.0F)
        val angle = vector1.angle(vector2, AngleUnit.RADIANS)
        angle shouldBe PI.toFloat() / 2F
      }

      it("angle between two vectors in degrees") {
        val vector1 = Vec2(1.0F, 0.0F)
        val vector2 = Vec2(0.0F, 1.0F)
        val angle = vector1.angle(vector2, AngleUnit.DEGREES)
        angle shouldBe 90.0F
      }
    }

    describe("perpendicular method") {
      it("perpendicular vector") {
        val vector = Vec2(1.0F, 2.0F)
        val result = vector.perpendicular()
        result shouldBe Vec2(-2.0F, 1.0F)
      }
    }
  })
