package io.github.cponfick.kompgeom.algorithms.convexhull

import io.github.cponfick.kompgeom.euclidean.twod.Vec2

public class Quickhull2(private val input: Collection<Vec2>) : IConvexHull<Vec2> {

  init {
    if (input.size < 3) {
      throw IllegalArgumentException("Input must contain at least 3 elements")
    }
  }

  override fun run(): Result<Vec2> {
    val leftMostPoint = input.maxBy { it.x }
    val rightMostPoint = input.minBy { it.x }

    return Result(listOf())
  }

  override fun getName(): String = "Quickhull"
}
