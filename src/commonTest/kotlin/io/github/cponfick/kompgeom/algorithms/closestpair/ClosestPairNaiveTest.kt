// package io.github.cponfick.kompgeom.algorithms.closestpair
//
// import io.github.cponfick.kompgeom.euclidean.twod.Vec2
// import io.kotest.assertions.throwables.shouldThrow
// import io.kotest.matchers.shouldBe
// import kotlin.test.Test
//
// class ClosestPairNaiveTest {
//
//  @Test
//  fun `calculates the closest pair of points`() {
//    val points = listOf(Vec2.ZERO, Vec2(1.0, 1.0))
//    val closestPair = ClosestPairNaive(points)
//
//    val actual = closestPair.run()
//
//    actual.distance shouldBe 1.4142135623730951
//    actual.result.first shouldBe points[0]
//    actual.result.second shouldBe points[1]
//  }
//
//  @Test
//  fun `returns the correct id`() {
//    val closestPair = ClosestPairNaive(listOf(Vec2.ZERO, Vec2(1.0, 1.0)))
//    closestPair.getId() shouldBe "closest-pair:closest-pair-naive"
//  }
//
//  @Test
//  fun `throws exception on empty input`() {
//    shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf()).run() }
//  }
//
//  @Test
//  fun `throws exception on single point input`() {
//    shouldThrow<IllegalArgumentException> { ClosestPairNaive(listOf(Vec2.ZERO)).run() }
//  }
// }
