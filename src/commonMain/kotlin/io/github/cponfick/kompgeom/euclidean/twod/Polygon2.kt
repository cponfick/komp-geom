package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.Orientation
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Polygon
import io.github.cponfick.kompgeom.core.transform.Transformer
import kotlin.math.abs

/**
 * Represents a polygon in 2D space defined by a list of vertices.
 *
 * The polygon is defined by its vertices in order (either clockwise or counterclockwise). The last
 * vertex is implicitly connected to the first vertex to close the polygon.
 *
 * @property vertices The list of vertices defining the polygon, must have at least 3 vertices.
 * @property precision The precision used for geometric computations, defaulting to
 *   [DEFAULT_DOUBLE_EQUIVALENCE].
 */
public data class Polygon2(
  public override val vertices: List<Vec2>,
  public val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Polygon<Vec2> {

  init {
    require(vertices.size >= 3) { "A polygon must have at least 3 vertices." }
  }

  /**
   * Returns the edges of the polygon as a list of segments.
   *
   * Each segment connects consecutive vertices, with the last segment connecting the last vertex to
   * the first vertex.
   */
  public val edges: List<Segment2> by lazy {
    vertices.indices.map { i ->
      val nextIndex = (i + 1) % vertices.size
      Segment2(vertices[i], vertices[nextIndex])
    }
  }

  public override fun signedArea(): Double {
    var sum = 0.0
    for (i in vertices.indices) {
      val j = (i + 1) % vertices.size
      sum += vertices[i].x * vertices[j].y
      sum -= vertices[j].x * vertices[i].y
    }
    return sum / 2.0
  }

  override fun area(): Double = abs(signedArea())

  public override fun perimeter(): Double = edges.sumOf { it.length() }

  public override fun centroid(): Vec2 {
    var cx = 0.0
    var cy = 0.0
    var signedArea = 0.0

    for (i in vertices.indices) {
      val j = (i + 1) % vertices.size
      val cross = vertices[i].x * vertices[j].y - vertices[j].x * vertices[i].y
      cx += (vertices[i].x + vertices[j].x) * cross
      cy += (vertices[i].y + vertices[j].y) * cross
      signedArea += cross
    }

    signedArea /= 2.0
    require(!precision.eqZero(signedArea)) { "Cannot compute centroid of a degenerate polygon." }

    return Vec2(cx / (6.0 * signedArea), cy / (6.0 * signedArea))
  }

  public override fun isConvex(): Boolean = isConvexHolder

  public override fun isSimple(): Boolean = isSimpleHolder

  private val isConvexHolder: Boolean by lazy {
    var hasPositive = false
    var hasNegative = false

    for (i in vertices.indices) {
      val prev = vertices[(i - 1 + vertices.size) % vertices.size]
      val curr = vertices[i]
      val next = vertices[(i + 1) % vertices.size]

      val edge1 = curr - prev
      val edge2 = next - curr
      val cross = edge1.signedArea(edge2)

      if (precision.gt(cross, 0.0)) {
        hasPositive = true
      } else if (precision.lt(cross, 0.0)) {
        hasNegative = true
      }

      if (hasPositive && hasNegative) {
        return@lazy false
      }
    }

    return@lazy true
  }

  private val isSimpleHolder: Boolean by lazy {
    // TODO: Optimize as soon as we have a sweeping line algorithm implemented
    //  Currently O(n^2) check, which is fine for small polygons but not efficient for large ones.
    for (i in edges.indices) {
      for (j in edges.indices) {
        if (i == j || (i + 1) % edges.size == j || i == (j + 1) % edges.size) {
          continue
        }
        if (
          edges[i].intersection(edges[j], precision).type in
            setOf(IntersectionType.POINT, IntersectionType.OVERLAP)
        ) {
          return@lazy false
        }
      }
    }
    return@lazy true
  }

  /**
   * Determines the orientation of the polygon based on the sign of its area.
   *
   * @return [Orientation.COUNTERCLOCKWISE] if vertices are ordered counterclockwise,
   *   [Orientation.CLOCKWISE] if ordered clockwise, [Orientation.COLLINEAR] if the polygon is
   *   degenerate (zero area).
   */
  public fun orientation(): Orientation {
    val area = signedArea()
    return when {
      precision.gt(area, 0.0) -> Orientation.COUNTERCLOCKWISE
      precision.lt(area, 0.0) -> Orientation.CLOCKWISE
      else -> Orientation.COLLINEAR
    }
  }

  public override fun contains(point: Vec2): Boolean {
    var crossings = 0

    for (i in vertices.indices) {
      val j = (i + 1) % vertices.size
      val vi = vertices[i]
      val vj = vertices[j]

      // Check if point is on the edge
      if (isPointOnSegment(point, vi, vj)) {
        return true
      }

      // Ray casting: count crossings of a ray from point to the right
      if ((vi.y > point.y) != (vj.y > point.y)) {
        val atX = (vj.x - vi.x) * (point.y - vi.y) / (vj.y - vi.y) + vi.x
        if (point.x < atX) {
          crossings++
        }
      }
    }

    return crossings % 2 == 1
  }

  private fun isPointOnSegment(point: Vec2, segmentStart: Vec2, segmentEnd: Vec2): Boolean {
    val cross =
      (point.y - segmentStart.y) * (segmentEnd.x - segmentStart.x) -
        (point.x - segmentStart.x) * (segmentEnd.y - segmentStart.y)

    if (!precision.eqZero(cross)) {
      return false
    }

    if (
      precision.lte(point.x, maxOf(segmentStart.x, segmentEnd.x)) &&
        precision.gte(point.x, minOf(segmentStart.x, segmentEnd.x)) &&
        precision.lte(point.y, maxOf(segmentStart.y, segmentEnd.y)) &&
        precision.gte(point.y, minOf(segmentStart.y, segmentEnd.y))
    ) {
      return true
    }

    return false
  }

  /**
   * Computes the axis-aligned bounding box of the polygon.
   *
   * @return A pair of Vec2 representing the minimum (bottom-left) and maximum (top-right) corners.
   */
  public fun boundingBox(): Pair<Vec2, Vec2> {
    val minX = vertices.minOf { it.x }
    val minY = vertices.minOf { it.y }
    val maxX = vertices.maxOf { it.x }
    val maxY = vertices.maxOf { it.y }
    return Pair(Vec2(minX, minY), Vec2(maxX, maxY))
  }

  public override fun transform(transformer: Transformer<Vec2>): Polygon2 {
    val transformedVertices = vertices.map { transformer.apply(it) }
    return Polygon2(transformedVertices, precision)
  }

  public override fun reverse(): Polygon2 = Polygon2(vertices.reversed(), precision)

  public override fun translate(offset: Vec2): Polygon2 {
    val translatedVertices = vertices.map { it + offset }
    return Polygon2(translatedVertices, precision)
  }

  public override fun scale(factor: Double, center: Vec2): Polygon2 {
    val scaledVertices =
      vertices.map { vertex ->
        val offset = vertex - center
        center + (offset * factor)
      }
    return Polygon2(scaledVertices, precision)
  }

  override fun dimensions(): Int = 2

  override fun isFinite(): Boolean = vertices.all { it.isFinite() }

  override fun isInfinite(): Boolean = vertices.any { it.isInfinite() }

  override fun isNaN(): Boolean = vertices.any { it.isNaN() }

  public companion object {
    /**
     * Creates a regular polygon with the specified number of sides, centered at a given point.
     *
     * @param sides The number of sides of the polygon (must be at least 3).
     * @param radius The radius of the circumscribed circle.
     * @param center The center point of the polygon. Defaults to the origin.
     * @param startAngle The starting angle in radians for the first vertex. Defaults to 0.
     * @param precision The precision used for geometric computations.
     * @return A new regular polygon.
     */
    public fun regular(
      sides: Int,
      radius: Double,
      center: Vec2 = Vec2.ZERO,
      startAngle: Double = 0.0,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Polygon2 {
      require(sides >= 3) { "A polygon must have at least 3 sides." }
      require(radius > 0.0) { "Radius must be positive." }

      val angleStep = 2.0 * kotlin.math.PI / sides
      val vertices =
        (0 until sides).map { i ->
          val angle = startAngle + i * angleStep
          Vec2(
            center.x + radius * kotlin.math.cos(angle),
            center.y + radius * kotlin.math.sin(angle),
          )
        }

      return Polygon2(vertices, precision)
    }

    /**
     * Creates a rectangle polygon.
     *
     * @param minX The minimum x-coordinate.
     * @param minY The minimum y-coordinate.
     * @param maxX The maximum x-coordinate.
     * @param maxY The maximum y-coordinate.
     * @param precision The precision used for geometric computations.
     * @return A new rectangular polygon.
     */
    public fun rectangle(
      minX: Double,
      minY: Double,
      maxX: Double,
      maxY: Double,
      precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
    ): Polygon2 {
      require(maxX > minX) { "maxX must be greater than minX." }
      require(maxY > minY) { "maxY must be greater than minY." }

      val vertices = listOf(Vec2(minX, minY), Vec2(maxX, minY), Vec2(maxX, maxY), Vec2(minX, maxY))

      return Polygon2(vertices, precision)
    }
  }
}
