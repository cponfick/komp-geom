package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

class Quickhull2DTest {
  @Test
  fun `viewer than 3 points should throw an exception`() {
    val points = listOf(Vec2(0.0, 0.0), Vec2(1.0, 1.0))
    shouldThrow<IllegalArgumentException> { Quickhull2(points).run() }
  }
}
