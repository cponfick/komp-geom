package algorithms.closestpair

import core.Point2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClosestPairNaiveTest :
    DescribeSpec({
      describe("calculates the closest pair of points") {
        val points = listOf(Point2(), Point2(1.0F, 1.0F))
        val closestPair = ClosestPairNaive(points)

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

      it("should return the correct id") {
        val closestPair = ClosestPairNaive(listOf(Point2(), Point2(1.0F, 1.0F)))
        closestPair.getId() shouldBe "closest-pair:closest-pair-naive"
      }

      it("should throw an exception on empty input") {
        shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf()).run() }
      }
    })
