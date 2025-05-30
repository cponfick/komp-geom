package io.github.cponfick.kompgeom.kompgeom.algorithms.closestpair

import io.github.cponfick.kompgeom.kompgeom.core.DEFAULT_EPSILON
import io.github.cponfick.kompgeom.kompgeom.core.Point2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ClosestPairNaiveTest {
  @Test
  fun `calculates the closest pair of points`() {
    val points = listOf(Point2(), Point2(1.0F, 1.0F))
    val closestPair = ClosestPairNaive(points)

    val actual = closestPair.run()

    actual.distance shouldBe (1.4142135F plusOrMinus DEFAULT_EPSILON)
    actual.result.first shouldBe points[0]
    actual.result.second shouldBe points[1]
  }

  @Test
  fun `returns the correct id`() {
    val closestPair = ClosestPairNaive(listOf(Point2(), Point2(1.0F, 1.0F)))
    closestPair.getId() shouldBe "closest-pair:closest-pair-naive"
  }

  @Test
  fun `throws exception on empty input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf()).run() }
  }

  @Test
  fun `throws exception on single point input`() {
    shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf(Point2())).run() }
  }
}
