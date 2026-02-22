package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.euclidean.oned.MutableVec1
import io.github.cponfick.kompgeom.euclidean.oned.Vec1
import io.github.cponfick.kompgeom.euclidean.threed.MutableVec3
import io.github.cponfick.kompgeom.euclidean.threed.Vec3
import io.github.cponfick.kompgeom.euclidean.twod.MutableVec2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

class VectorExtensionsTest {

  @Test
  fun `toImmutable on Vec1 returns same instance`() {
    val vec = Vec1(1.0)
    val result = vec.toImmutable()
    result shouldBeSameInstanceAs vec
  }

  @Test
  fun `toImmutable on MutableVec1 returns Vec1 with same component`() {
    val mutable = MutableVec1(2.5)
    val result = mutable.toImmutable()
    result.x shouldBe 2.5
  }

  @Test
  fun `toMutable on Vec1 returns new MutableVec1 with same component`() {
    val vec = Vec1(3.0)
    val result = vec.toMutable()
    result.x shouldBe 3.0
  }

  @Test
  fun `toMutable on MutableVec1 returns a new instance`() {
    val mutable = MutableVec1(4.0)
    val result = mutable.toMutable()
    result.eq(mutable) shouldBe true
    result shouldNotBe mutable
  }

  @Test
  fun `toImmutable on Vec2 returns same instance`() {
    val vec = Vec2(1.0, 2.0)
    val result = vec.toImmutable()
    result shouldBeSameInstanceAs vec
  }

  @Test
  fun `toImmutable on MutableVec2 returns Vec2 with same components`() {
    val mutable = MutableVec2(3.0, 4.0)
    val result = mutable.toImmutable()
    result.x shouldBe 3.0
    result.y shouldBe 4.0
  }

  @Test
  fun `toMutable on Vec2 returns new MutableVec2 with same components`() {
    val vec = Vec2(5.0, 6.0)
    val result = vec.toMutable()
    result.x shouldBe 5.0
    result.y shouldBe 6.0
  }

  @Test
  fun `toMutable on MutableVec2 returns a new instance`() {
    val mutable = MutableVec2(7.0, 8.0)
    val result = mutable.toMutable()
    result.eq(mutable) shouldBe true
    result shouldNotBe mutable
  }

  @Test
  fun `toImmutable on Vec3 returns same instance`() {
    val vec = Vec3(1.0, 2.0, 3.0)
    val result = vec.toImmutable()
    result shouldBeSameInstanceAs vec
  }

  @Test
  fun `toImmutable on MutableVec3 returns Vec3 with same components`() {
    val mutable = MutableVec3(4.0, 5.0, 6.0)
    val result = mutable.toImmutable()
    result.x shouldBe 4.0
    result.y shouldBe 5.0
    result.z shouldBe 6.0
  }

  @Test
  fun `toMutable on Vec3 returns new MutableVec3 with same components`() {
    val vec = Vec3(7.0, 8.0, 9.0)
    val result = vec.toMutable()
    result.x shouldBe 7.0
    result.y shouldBe 8.0
    result.z shouldBe 9.0
  }

  @Test
  fun `toMutable on MutableVec3 returns a new instance`() {
    val mutable = MutableVec3(10.0, 11.0, 12.0)
    val result = mutable.toMutable()
    result.eq(mutable) shouldBe true
    result shouldNotBe mutable
  }
}
