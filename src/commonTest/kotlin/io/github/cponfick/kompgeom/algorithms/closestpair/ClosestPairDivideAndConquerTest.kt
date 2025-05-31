package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.core.DEFAULT_EPSILON
import io.github.cponfick.kompgeom.core.Point2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ClosestPairDivideAndConquerTest {
  @Test
  fun `calculates the closest pair of points`() {
    val points = listOf(Point2(), Point2(1.0F, 1.0F))
    val closestPair = ClosestPairDivideAndConquer(points)

    val actual = closestPair.run()

    actual.distance shouldBe (1.4142135F plusOrMinus DEFAULT_EPSILON)
    actual.result.first shouldBe points[0]
    actual.result.second shouldBe points[1]
  }

  @Test
  fun `calculates the closest pair of points 2`() {
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

    // Assuming the naive implementation is correct
    val closestPairNaiveResult = ClosestPairNaive(points).run()
    val actual = ClosestPairDivideAndConquer(points).run()

    actual.distance shouldBe closestPairNaiveResult.distance
    actual.result.first shouldBe closestPairNaiveResult.result.first
    actual.result.second shouldBe closestPairNaiveResult.result.second
  }

  @Test
  fun `returns the correct id`() {
    val closestPair = ClosestPairDivideAndConquer(listOf(Point2(), Point2(1.0F, 1.0F)))
    closestPair.getId() shouldBe "closest-pair:closest-pair-divide-and-conquer"
  }

  @Test
  fun `throws exception on empty input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairDivideAndConquer(listOf()).run() }
  }

  @Test
  fun `throws exception on single point input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairDivideAndConquer(listOf(Point2())).run() }
  }
}
