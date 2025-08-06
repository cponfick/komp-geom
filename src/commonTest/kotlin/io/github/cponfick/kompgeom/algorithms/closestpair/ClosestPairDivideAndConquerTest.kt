package io.github.cponfick.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ClosestPairDivideAndConquerTest {
  @Test
  fun `calculates the closest pair of points`() {
    val points = listOf(Vec2.ZERO, Vec2(1.0, 1.0))
    val closestPair = ClosestPairDivideAndConquer(points)

    val actual = closestPair.run()

    actual.distance shouldBe 1.4142135623730951
    actual.result.first shouldBe points[0]
    actual.result.second shouldBe points[1]
  }

  @Test
  fun `calculates the closest pair of points 2`() {
    val points =
      listOf(
        Vec2(6.51, 4.61),
        Vec2(3.05, -6.65),
        Vec2(-7.38, -0.83),
        Vec2(-4.09, 2.98),
        Vec2(1.16, -2.13),
        Vec2(5.58, -2.42),
        Vec2(0.90, 4.94),
        Vec2(-5.18, 4.76),
        Vec2(-4.56, -3.42),
        Vec2(6.82, -5.87),
        Vec2(1.35, -8.11),
        Vec2(-7.74, -4.35),
        Vec2(-7.17, 0.97),
        Vec2(-2.98, 1.02),
        Vec2(4.58, -7.09),
        Vec2(-4.45, 6.13),
        Vec2(-6.91, 3.31),
        Vec2(2.87, -2.71),
        Vec2(2.93, 5.65),
        Vec2(7.11, -4.82),
        Vec2(3.80, 0.93),
        Vec2(2.24, 3.02),
        Vec2(-2.31, -5.24),
        Vec2(4.12, -5.35),
        Vec2(4.61, 4.45),
        Vec2(8.42, -3.38),
        Vec2(4.91, -0.64),
        Vec2(-7.03, -2.60),
        Vec2(-4.00, -1.44),
        Vec2(0.40, -5.06),
        Vec2(7.31, 2.60),
      )

    // Assuming the naive implementation is correct
    val closestPairNaiveResult = ClosestPairNaive(points).run()
    val actual = ClosestPairDivideAndConquer(points).run()

    actual.distance shouldBe closestPairNaiveResult.distance
    actual.result.first shouldBe closestPairNaiveResult.result.first
    actual.result.second shouldBe closestPairNaiveResult.result.second
  }

  @Test
  fun `getId the correct id`() {
    ClosestPairDivideAndConquer.getId() shouldBe "closest-pair:closest-pair-divide-and-conquer"
  }

  @Test
  fun `getTimeComplexity returns O(n log n)`() {
    ClosestPairDivideAndConquer.getTimeComplexity() shouldBe "O(n log n)"
  }

  @Test
  fun `getSpaceComplexity returns O(n)`() {
    ClosestPairDivideAndConquer.getSpaceComplexity() shouldBe "O(n)"
  }

  @Test
  fun `throws exception on empty input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairDivideAndConquer(listOf()).run() }
  }

  @Test
  fun `throws exception on single point input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairDivideAndConquer(listOf(Vec2.ZERO)).run() }
  }
}
