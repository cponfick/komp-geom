package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.core.shapes.IntersectionData
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IntersectionTest {

  @Test
  fun `constructor works as expected with valid argument combinations`() {
    val noneType = IntersectionData(IntersectionType.NONE)
    noneType.type shouldBe IntersectionType.NONE
    noneType.point shouldBe null
    noneType.segment shouldBe null

    val point = Vec2(1.0, 1.0)
    val pointIntersection = IntersectionData(IntersectionType.POINT, Vec2(1.0, 1.0))
    pointIntersection.type shouldBe IntersectionType.POINT
    pointIntersection.point shouldBe point
    pointIntersection.segment shouldBe null

    val overlap = Pair(Vec2(0.0, 0.0), Vec2(2.0, 2.0))
    val overlapIntersection = IntersectionData(IntersectionType.OVERLAP, segment = overlap)
    overlapIntersection.type shouldBe IntersectionType.OVERLAP
    overlapIntersection.point shouldBe null
    overlapIntersection.segment shouldBe overlap
  }

  @Test
  fun `constructor throws exceptions for invalid argument combinations`() {
    shouldThrow<IllegalArgumentException> {
      IntersectionData(IntersectionType.NONE, Vec2(1.0, 1.0))
    }
    shouldThrow<IllegalArgumentException> {
      IntersectionData(IntersectionType.NONE, segment = Pair(Vec2(0.0, 0.0), Vec2(1.0, 1.0)))
    }
    shouldThrow<IllegalArgumentException> { IntersectionData(IntersectionType.POINT) }
    shouldThrow<IllegalArgumentException> {
      IntersectionData(IntersectionType.POINT, segment = Pair(Vec2(0.0, 0.0), Vec2(1.0, 1.0)))
    }
    shouldThrow<IllegalArgumentException> { IntersectionData(IntersectionType.OVERLAP) }
    shouldThrow<IllegalArgumentException> {
      IntersectionData(IntersectionType.OVERLAP, Vec2(1.0, 1.0))
    }
  }
}
