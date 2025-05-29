package algorithms.closestpair

import core.Point2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClosestPairDivideAndConquerTest :
    DescribeSpec({
      describe("calculates the closest pair of points") {
        val points = listOf(Point2(), Point2(1.0F, 1.0F))
        val closestPair = ClosestPairDivideAndConquer(points)

        it("should return the correct distance") {
          val result = closestPair.run()
          result.distance shouldBe 1.4142135F
        }

        it("should return the correct pair") {
          val result = closestPair.run()
          result.result.first shouldBe points[0]
          result.result.second shouldBe points[1]
        }
      }

      describe("calculates the closest pair of multiple points") {
        val points =
            listOf(
                Point2(6.51F, 4.61F),
                Point2(3.05F, -6.65F),
                Point2(-7.38F, -0.83F),
                Point2(-4.09F, 2.98F),
                Point2(1.16F, -2.13F),
                Point2(5.58F, -2.42F),
                Point2(0.90F, 4.94F),
                Point2(-5.18F, 4.76F),
                Point2(-4.56F, -3.42F),
                Point2(6.82F, -5.87F),
                Point2(1.35F, -8.11F),
                Point2(-7.74F, -4.35F),
                Point2(-7.17F, 0.97F),
                Point2(-2.98F, 1.02F),
                Point2(4.58F, -7.09F),
                Point2(-4.45F, 6.13F),
                Point2(-6.91F, 3.31F),
                Point2(2.87F, -2.71F),
                Point2(2.93F, 5.65F),
                Point2(7.11F, -4.82F),
                Point2(3.80F, 0.93F),
                Point2(2.24F, 3.02F),
                Point2(-2.31F, -5.24F),
                Point2(4.12F, -5.35F),
                Point2(4.61F, 4.45F),
                Point2(8.42F, -3.38F),
                Point2(4.91F, -0.64F),
                Point2(-7.03F, -2.60F),
                Point2(-4.00F, -1.44F),
                Point2(0.40F, -5.06F),
                Point2(7.31F, 2.60F),
            )
        val closestPair = ClosestPairDivideAndConquer(points)
        // Assuming the naive implementation is correct, we can use it to verify the results
        val closestPairNaiveResult = ClosestPairNaive(points).run()

        it("should return the correct distance") {
          val result = closestPair.run()
          result.distance shouldBe closestPairNaiveResult.distance
        }

        it("should return the correct pair") {
          val result = closestPair.run()
          result.result.first shouldBe closestPairNaiveResult.result.first
          result.result.second shouldBe closestPairNaiveResult.result.second
        }
      }

      it("should return the correct id") {
        val closestPair = ClosestPairDivideAndConquer(listOf(Point2(), Point2(1.0F, 1.0F)))
        closestPair.getId() shouldBe "closest-pair:closest-pair-divide-and-conquer"
      }

      it("should throw an exception on empty input") {
        shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf()).run() }
      }

      it("should throw an exception on input with one point") {
        shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf(Point2())).run() }
      }
    })
