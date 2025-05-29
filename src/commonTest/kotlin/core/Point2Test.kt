package core

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI

class Point2Test :
    DescribeSpec({
      describe("constructor") {
        it("default constructor") {
          val point = Point2()
          point.x shouldBe 0.0F
          point.y shouldBe 0.0F
        }

        it("constructor with parameters") {
          val point = Point2(1.0F, 2.0F)
          point.x shouldBe 1.0F
          point.y shouldBe 2.0F
        }

        it("copy constructor") {
          val original = Point2(3.0F, 4.0F)
          val copy = Point2(original)
          original shouldBe copy
        }
      }

      describe("equals") {
        it("should be equal for same values") {
          val point1 = Point2(1.0F, 2.0F)
          val point2 = Point2(1.0F, 2.0F)
          point1 shouldBe point2
        }

        it("should not be equal for different values") {
          val point1 = Point2(1.0F, 2.0F)
          val point2 = Point2(3.0F, 4.0F)
          point1 shouldNotBe point2
        }
      }

      describe("distance") {
        it("should calculate distance correctly") {
          val point1 = Point2(1.0F, 2.0F)
          val point2 = Point2(4.0F, 6.0F)
          val distance = point1 distance point2
          distance shouldBe 5.0F
        }
      }

      describe("rotate") {
        it("should rotate correctly in radians 90 degrees") {
          val point = Point2(1.0F, 0.0F)
          val rotatedPoint = point.rotate(PI.toFloat() / 2)
          rotatedPoint shouldBe Point2(0.0F, 1.0F)
        }

        it("should rotate correctly in radians 180 degrees") {
          val point = Point2(1.0F, 0.0F)
          val rotatedPoint = point.rotate(PI.toFloat())
          rotatedPoint shouldBe Point2(-1.0F, 0.0F)
        }

        it("should rotate correctly in radians 270 degrees") {
          val point = Point2(1.0F, 0.0F)
          val rotatedPoint = point.rotate(3 * PI.toFloat() / 2)
          rotatedPoint shouldBe Point2(0.0F, -1.0F)
        }

        it("should rotate correctly in radians 360 degrees") {
          val point = Point2(1.0F, 0.0F)
          val rotatedPoint = point.rotate(2 * PI.toFloat())
          rotatedPoint shouldBe Point2(1.0F, 0.0F)
        }

        it("should rotate correctly in radians 45 degrees") {
          val point = Point2(1.0F, 0.0F)
          val rotatedPoint = point.rotate(PI.toFloat() / 4)
          rotatedPoint shouldBe Point2(0.70710677F, 0.70710677F)
        }
      }
    })
