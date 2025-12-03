package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.Polygon
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.github.cponfick.kompgeom.euclidean.twod.Polygon2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import kotlin.math.abs

public data class Polygon3(
  public override val vertices: List<Vec3>,
  public val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Polygon<Vec3> {

  init {
    require(vertices.size >= 3) { "A polygon must have at least 3 vertices." }
    require(isCoplanar()) { "All vertices of the polygon must be coplanar." }
  }

  /** The normal vector of the polygon's plane. */
  public val normal: Vec3 by lazy { normal() }

  private fun normal(): Vec3 {
    val v1 = vertices[1] - vertices[0]
    val v2 = vertices[2] - vertices[1]
    return v1.cross(v2).normalize()
  }

  public val edges: List<Segment3> by lazy {
    vertices.indices.map { i ->
      val nextIndex = (i + 1) % vertices.size
      Segment3(vertices[i], vertices[nextIndex])
    }
  }

  override fun signedArea(): Double {
    var area = 0.0
    for (currentVertexIdx in 0 until vertexCount) {
      val nextVertexIdx = (currentVertexIdx + 1) % vertexCount
      area += vertices[currentVertexIdx].cross(vertices[nextVertexIdx]).dot(normal)
    }
    return area / 2.0
  }

  override fun area(): Double = abs(signedArea())

  override fun perimeter(): Double = edges.sumOf { it.length() }

  override fun centroid(): Vec3 {
    var accumulatedSignedArea = 0.0
    var weightedX = 0.0
    var weightedY = 0.0
    var weightedZ = 0.0

    for (currentVertexIdx in vertices.indices) {
      val nextVertexIdx = (currentVertexIdx + 1) % vertices.size
      val crossProduct = vertices[currentVertexIdx].cross(vertices[nextVertexIdx])
      accumulatedSignedArea += crossProduct.dot(normal)
      weightedX +=
        (vertices[currentVertexIdx].x + vertices[nextVertexIdx].x) * crossProduct.dot(normal)
      weightedY +=
        (vertices[currentVertexIdx].y + vertices[nextVertexIdx].y) * crossProduct.dot(normal)
      weightedZ +=
        (vertices[currentVertexIdx].z + vertices[nextVertexIdx].z) * crossProduct.dot(normal)
    }

    accumulatedSignedArea /= 2.0
    require(!precision.eqZero(accumulatedSignedArea)) {
      "Cannot compute centroid of a degenerate polygon."
    }

    return Vec3(
      weightedX / (6.0 * accumulatedSignedArea),
      weightedY / (6.0 * accumulatedSignedArea),
      weightedZ / (6.0 * accumulatedSignedArea),
    )
  }

  override fun isConvex(): Boolean = isConvexHolder

  private val isConvexHolder: Boolean by lazy {
    if (vertexCount < 4) return@lazy true
    for (i in vertices.indices) {
      val currentVertex = vertices[i]
      val nextVertex = vertices[(i + 1) % vertexCount]
      val vertexAfterNext = vertices[(i + 2) % vertexCount]
      val firstEdge = nextVertex - currentVertex
      val secondEdge = vertexAfterNext - nextVertex
      if (firstEdge.cross(secondEdge).dot(normal) < 0) {
        return@lazy false
      }
    }
    return@lazy true
  }

  override fun isSimple(): Boolean = isSimpleHolder

  private val isSimpleHolder: Boolean by lazy {
    if (vertexCount < 4) return@lazy true
    return@lazy projectTo2D().isSimple()
  }

  private fun isCoplanar(): Boolean {
    if (vertexCount < 4) return true
    val point = vertices[0]

    return vertices.all { vertex -> precision.eqZero((vertex - point).dot(normal())) }
  }

  private fun projectTo2D(): Polygon2 {
    val tangentVector = normal.cross(orthonormalBase)

    val projectionOrigin = vertices[0]
    val projected2DVertices =
      vertices.map { vertex ->
        val relative = vertex - projectionOrigin
        Vec2(relative.dot(orthonormalBase), relative.dot(tangentVector))
      }

    return Polygon2(projected2DVertices, precision)
  }

  private val orthonormalBase: Vec3 by lazy {
    val u =
      if (abs(normal.x) > abs(normal.z)) {
        Vec3(-normal.y, normal.x, 0.0).normalize()
      } else {
        Vec3(0.0, -normal.z, normal.y).normalize()
      }
    return@lazy u
  }

  override fun contains(point: Vec3): Boolean {
    // Check if point lies on the polygon's plane
    val pointToPlane = point - vertices[0]
    if (!precision.eqZero(pointToPlane.dot(normal))) {
      return false
    }

    val v = normal.cross(orthonormalBase)
    val origin = vertices[0]
    val relative = point - origin
    val point2D = Vec2(relative.dot(orthonormalBase), relative.dot(v))

    return projectTo2D().contains(point2D)
  }

  override fun transform(transformer: Transformer<Vec3>): Polygon<Vec3> {
    val transformedVertices = vertices.map { transformer.apply(it) }
    return Polygon3(transformedVertices, precision)
  }

  override fun reverse(): Polygon<Vec3> = Polygon3(vertices.reversed(), precision)

  override fun translate(offset: Vec3): Polygon<Vec3> {
    val translatedVertices = vertices.map { it + offset }
    return Polygon3(translatedVertices, precision)
  }

  override fun scale(factor: Double, center: Vec3): Polygon<Vec3> {
    val scaledVertices =
      vertices.map { vertex ->
        val offset = vertex - center
        center + (offset * factor)
      }
    return Polygon3(scaledVertices, precision)
  }

  override fun dimensions(): Int = 3

  override fun isFinite(): Boolean = vertices.all { it.isFinite() }

  override fun isInfinite(): Boolean = vertices.any { it.isInfinite() }

  override fun isNaN(): Boolean = vertices.any { it.isNaN() }
}
