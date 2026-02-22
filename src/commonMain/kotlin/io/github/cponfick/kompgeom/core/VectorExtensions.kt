package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.euclidean.oned.MutableVec1
import io.github.cponfick.kompgeom.euclidean.oned.Vec1
import io.github.cponfick.kompgeom.euclidean.threed.MutableVec3
import io.github.cponfick.kompgeom.euclidean.threed.Vec3
import io.github.cponfick.kompgeom.euclidean.twod.MutableVec2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

/**
 * Returns this vector as an immutable [Vec1].
 *
 * If this vector is already a [Vec1], it is returned directly without copying.
 *
 * @return An immutable [Vec1] with the same component as this vector.
 */
public fun Vector1<*>.toImmutable(): Vec1 =
  when (this) {
    is Vec1 -> this
    else -> Vec1(x)
  }

/**
 * Returns a mutable copy of this vector as a [MutableVec1].
 *
 * Always creates a new instance, even if this vector is already a [MutableVec1].
 *
 * @return A new [MutableVec1] with the same component as this vector.
 */
public fun Vector1<*>.toMutable(): MutableVec1 = MutableVec1(x)

/**
 * Returns this vector as an immutable [Vec2].
 *
 * If this vector is already a [Vec2], it is returned directly without copying.
 *
 * @return An immutable [Vec2] with the same components as this vector.
 */
public fun Vector2<*>.toImmutable(): Vec2 =
  when (this) {
    is Vec2 -> this
    else -> Vec2(x, y)
  }

/**
 * Returns a mutable copy of this vector as a [MutableVec2].
 *
 * Always creates a new instance, even if this vector is already a [MutableVec2].
 *
 * @return A new [MutableVec2] with the same components as this vector.
 */
public fun Vector2<*>.toMutable(): MutableVec2 = MutableVec2(x, y)

/**
 * Returns this vector as an immutable [Vec3].
 *
 * If this vector is already a [Vec3], it is returned directly without copying.
 *
 * @return An immutable [Vec3] with the same components as this vector.
 */
public fun Vector3<*>.toImmutable(): Vec3 =
  when (this) {
    is Vec3 -> this
    else -> Vec3(x, y, z)
  }

/**
 * Returns a mutable copy of this vector as a [MutableVec3].
 *
 * Always creates a new instance, even if this vector is already a [MutableVec3].
 *
 * @return A new [MutableVec3] with the same components as this vector.
 */
public fun Vector3<*>.toMutable(): MutableVec3 = MutableVec3(x, y, z)
