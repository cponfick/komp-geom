package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.Vector3
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.Line
import io.github.cponfick.kompgeom.core.shapes.Location
import kotlin.math.abs

/**
 * Represents a line in 3D space defined by a direction vector and a moment (point offset).
 *
 * The line is stored in Plücker form using a unit [direction] vector and a [moment] vector
 * computed as `point × direction` for any point on the line. This representation allows stable
 * distance and side classification while remaining translation invariant.
 *
 * @property direction The unit direction vector of the line.
 * @property moment The moment vector (`point × direction`) identifying the line's position.
 * @property precision The precision used for geometric computations, defaulting to
 *   [DEFAULT_DOUBLE_EQUIVALENCE].
 */
public data class Line3(
  public val direction: Vec3,
  public val moment: Vec3,
  public val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Line<Vector3<*>> {
  private val referenceNormal: Vec3 by lazy { computeReferenceNormal(direction) }

  init {
    require(precision.eq(1.0, direction.norm())) { "Direction vector cannot be zero." }
  }

  override fun distance(other: Vector3<*>): Double = abs(offset(other))

  override fun offset(vec: Vector3<*>): Double {
    val offsetVector = computeOffsetVector(vec)
    val magnitude = offsetVector.norm()
    if (precision.eqZero(magnitude)) return 0.0

    val sign = precision.signum(referenceNormal dot offsetVector)
    return sign * magnitude
  }

  override fun reverse(): Line3 = Line3(-direction, -moment, precision)

  override fun location(vec: Vector3<*>): Location {
    val signedDistance = offset(vec)
    val signum = precision.signum(signedDistance)
    return when {
      signum > 0 -> Location.PLUS
      signum < 0 -> Location.MINUS
      else -> Location.ON
    }
  }

  private fun computeOffsetVector(vec: Vector3<*>): Vec3 = (Vec3.from(vec) cross direction) - moment

  private fun computeReferenceNormal(direction: Vec3): Vec3 {
    val absX = abs(direction.x)
    val absY = abs(direction.y)
    val absZ = abs(direction.z)

    val helper =
      when {
        absX <= absY && absX <= absZ -> Vec3(1.0, 0.0, 0.0)
        absY <= absX && absY <= absZ -> Vec3(0.0, 1.0, 0.0)
        else -> Vec3(0.0, 0.0, 1.0)
      }

    val normal = direction cross helper
    val norm = normal.norm()
    require(!precision.eqZero(norm)) { "Cannot compute reference normal for direction $direction." }
    return Vec3(normal.x / norm, normal.y / norm, normal.z / norm)
  }

  public companion object {
    /**
     * Creates a line from a point and a direction vector.
     *
     * @param point The point through which the line passes.
     * @param direction The direction vector of the line.
     * @param precision The precision used for geometric computations, defaulting to
     *   [DEFAULT_DOUBLE_EQUIVALENCE].
     * @return A new [Line3] instance representing the line.
     * @throws IllegalArgumentException if the direction vector is zero.
     */
    public fun fromPointAndDirection(
      point: Vector3<*>,
      direction: Vector3<*>,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Line3 {
      require(!(direction.eq(Vec3.ZERO, precision))) { "Direction vector cannot be zero." }

      val unitDirection = direction.normalize()
      val moment = Vec3.from(point) cross unitDirection

      return Line3(Vec3.from(unitDirection), moment, precision)
    }

    /**
     * Creates a line from two points.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @param precision The precision used for geometric computations, defaulting to
     *   [DEFAULT_DOUBLE_EQUIVALENCE].
     * @return A new [Line3] instance representing the line through the two points.
     * @throws IllegalArgumentException if the two points are the same (resulting in a zero
     *   direction vector).
     */
    public fun fromPoints(
      p1: Vector3<*>,
      p2: Vector3<*>,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Line3 =
      fromPointAndDirection(p1, Vec3(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z), precision)
  }
}
