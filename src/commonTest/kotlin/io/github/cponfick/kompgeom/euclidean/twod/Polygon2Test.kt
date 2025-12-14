package io.github.cponfick.kompgeom.euclidean.twod

import io.github.cponfick.kompgeom.core.Orientation
import io.github.cponfick.kompgeom.core.transform.Transformer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertFailsWith

class Polygon2Test {

  @Test
  fun `constructor requires at least 3 vertices`() {
    assertFailsWith<IllegalArgumentException> { Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0))) }
  }

  @Test
  fun `constructor accepts exactly 3 vertices`() {
    val triangle = listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(0.5, 1.0))
    val polygon = Polygon2(triangle)
    polygon.vertexCount shouldBe 3
    polygon.vertices shouldContainExactly triangle
  }

  @Test
  fun `vertexCount returns the correct vertices`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.vertexCount shouldBe 4
    square.vertices shouldContainExactly
      listOf(Vec2(0.0, 0.0), Vec2(2.0, 0.0), Vec2(2.0, 2.0), Vec2(0.0, 2.0))
  }

  @Test
  fun `edges returns correct number of edges and constructs edges correctly`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(0.5, 1.0)))
    triangle.edges.size shouldBe 3
    triangle.edges shouldContainExactly
      listOf(
        Segment2(Vec2(0.0, 0.0), Vec2(1.0, 0.0)),
        Segment2(Vec2(1.0, 0.0), Vec2(0.5, 1.0)),
        Segment2(Vec2(0.5, 1.0), Vec2(0.0, 0.0)),
      )
  }

  @Test
  fun `signedArea returns positive for counterclockwise square`() {
    val square = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(2.0, 0.0), Vec2(2.0, 2.0), Vec2(0.0, 2.0)))
    square.signedArea() shouldBe 4.0
  }

  @Test
  fun `signedArea returns negative for clockwise square`() {
    val square = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(0.0, 2.0), Vec2(2.0, 2.0), Vec2(2.0, 0.0)))
    square.signedArea() shouldBe -4.0
  }

  @Test
  fun `area returns absolute value of signed area`() {
    val square = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(0.0, 2.0), Vec2(2.0, 2.0), Vec2(2.0, 0.0)))
    square.area() shouldBe 4.0
  }

  @Test
  fun `area calculates triangle area correctly`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(3.0, 0.0), Vec2(0.0, 4.0)))
    triangle.area() shouldBe 6.0
  }

  @Test
  fun `perimeter calculates square perimeter correctly`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.perimeter() shouldBe 8.0
  }

  @Test
  fun `perimeter calculates triangle perimeter correctly`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(3.0, 0.0), Vec2(0.0, 4.0)))
    triangle.perimeter() shouldBe 12.0
  }

  @Test
  fun `centroid returns center of square`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val centroid = square.centroid()
    centroid.x shouldBe 1.0
    centroid.y shouldBe 1.0
  }

  @Test
  fun `centroid calculates triangle centroid correctly`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(3.0, 0.0), Vec2(0.0, 3.0)))
    val centroid = triangle.centroid()
    abs(centroid.x - 1.0) shouldBeLessThan 1e-10
    abs(centroid.y - 1.0) shouldBeLessThan 1e-10
  }

  @Test
  fun `isConvex returns true for square`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.isConvex() shouldBe true
  }

  @Test
  fun `isConvex returns true for triangle`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(2.0, 0.0), Vec2(1.0, 2.0)))
    triangle.isConvex() shouldBe true
  }

  @Test
  fun `isSimple returns true for square`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.isSimple() shouldBe true
  }

  @Test
  fun `isSimple returns true for triangle`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(2.0, 0.0), Vec2(1.0, 2.0)))
    triangle.isSimple() shouldBe true
  }

  @Test
  fun `isConvex returns false for concave polygon`() {
    val lShape =
      Polygon2(
        listOf(
          Vec2(0.0, 0.0),
          Vec2(2.0, 0.0),
          Vec2(2.0, 1.0),
          Vec2(1.0, 1.0),
          Vec2(1.0, 2.0),
          Vec2(0.0, 2.0),
        )
      )
    lShape.isConvex() shouldBe false
  }

  @Test
  fun `isSimple returns true for concave polygon`() {
    val lShape =
      Polygon2(
        listOf(
          Vec2(0.0, 0.0),
          Vec2(2.0, 0.0),
          Vec2(2.0, 1.0),
          Vec2(1.0, 1.0),
          Vec2(1.0, 2.0),
          Vec2(0.0, 2.0),
        )
      )
    lShape.isSimple() shouldBe true
  }

  @Test
  fun `isSimple returns false for self-intersecting polygon`() {
    val bowtie = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(2.0, 2.0), Vec2(0.0, 2.0), Vec2(2.0, 0.0)))
    bowtie.isSimple() shouldBe false
  }

  @Test
  fun `orientation returns COUNTERCLOCKWISE for counter clock wise square`() {
    val square = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(2.0, 0.0), Vec2(2.0, 2.0), Vec2(0.0, 2.0)))
    square.orientation() shouldBe Orientation.COUNTERCLOCKWISE
  }

  @Test
  fun `orientation returns CLOCKWISE for clock wise square`() {
    val square = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(0.0, 2.0), Vec2(2.0, 2.0), Vec2(2.0, 0.0)))
    square.orientation() shouldBe Orientation.CLOCKWISE
  }

  @Test
  fun `orientation returns COLLINEAR for degenerate polygon`() {
    val degenerate = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
    degenerate.orientation() shouldBe Orientation.COLLINEAR
  }

  @Test
  fun `contains returns true for point inside square`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.contains(Vec2(1.0, 1.0)) shouldBe true
  }

  @Test
  fun `contains returns false for point outside square`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.contains(Vec2(3.0, 3.0)) shouldBe false
  }

  @Test
  fun `contains returns true for point on boundary`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.contains(Vec2(0.0, 1.0)) shouldBe true
    square.contains(Vec2(1.0, 0.0)) shouldBe true
  }

  @Test
  fun `contains works for triangle`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(3.0, 0.0), Vec2(1.5, 3.0)))
    triangle.contains(Vec2(1.5, 1.0)) shouldBe true
    triangle.contains(Vec2(0.0, 3.0)) shouldBe false
  }

  @Test
  fun `contains works for concave polygon`() {
    // L-shape polygon
    val lShape =
      Polygon2(
        listOf(
          Vec2(0.0, 0.0),
          Vec2(2.0, 0.0),
          Vec2(2.0, 1.0),
          Vec2(1.0, 1.0),
          Vec2(1.0, 2.0),
          Vec2(0.0, 2.0),
        )
      )
    lShape.contains(Vec2(0.5, 0.5)) shouldBe true
    lShape.contains(Vec2(1.5, 1.5)) shouldBe false
  }

  @Test
  fun `boundingBox returns correct bounds for square`() {
    val square = Polygon2.rectangle(1.0, 2.0, 5.0, 7.0)
    val (min, max) = square.boundingBox()
    min shouldBe Vec2(1.0, 2.0)
    max shouldBe Vec2(5.0, 7.0)
  }

  @Test
  fun `boundingBox returns correct bounds for triangle`() {
    val triangle = Polygon2(listOf(Vec2(1.0, 1.0), Vec2(5.0, 2.0), Vec2(3.0, 6.0)))
    val (min, max) = triangle.boundingBox()
    min shouldBe Vec2(1.0, 1.0)
    max shouldBe Vec2(5.0, 6.0)
  }

  @Test
  fun `transform applies transformation to all vertices`() {
    val square = Polygon2.rectangle(0.0, 0.0, 1.0, 1.0)
    val transformer =
      object : Transformer<Vec2> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : Vec2> apply(obj: T): T = Vec2(obj.x * 2, obj.y * 2) as T

        override fun inverse(): Transformer<Vec2> = this

        override fun preserveOrientation(): Boolean = true
      }

    val transformed = square.transform(transformer)
    transformed.vertices[0] shouldBe Vec2(0.0, 0.0)
    transformed.vertices[1] shouldBe Vec2(2.0, 0.0)
    transformed.vertices[2] shouldBe Vec2(2.0, 2.0)
    transformed.vertices[3] shouldBe Vec2(0.0, 2.0)
  }

  @Test
  fun `reverse reverses vertex order`() {
    val triangle = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(0.5, 1.0)))
    val reversed = triangle.reverse()

    reversed.vertices[0] shouldBe Vec2(0.5, 1.0)
    reversed.vertices[1] shouldBe Vec2(1.0, 0.0)
    reversed.vertices[2] shouldBe Vec2(0.0, 0.0)
  }

  @Test
  fun `reverse changes orientation`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val originalOrientation = square.orientation()
    val reversed = square.reverse()

    when (originalOrientation) {
      Orientation.CLOCKWISE -> reversed.orientation() shouldBe Orientation.COUNTERCLOCKWISE
      Orientation.COUNTERCLOCKWISE -> reversed.orientation() shouldBe Orientation.CLOCKWISE
      else -> {}
    }
  }

  @Test
  fun `translate moves all vertices by offset`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val translated = square.translate(Vec2(3.0, 4.0))

    translated.vertices[0] shouldBe Vec2(3.0, 4.0)
    translated.vertices[1] shouldBe Vec2(5.0, 4.0)
    translated.vertices[2] shouldBe Vec2(5.0, 6.0)
    translated.vertices[3] shouldBe Vec2(3.0, 6.0)
  }

  @Test
  fun `translate preserves area`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val translated = square.translate(Vec2(10.0, 10.0))

    translated.area() shouldBe square.area()
  }

  @Test
  fun `scale with factor 2 doubles the distances from center`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val scaled = square.scale(2.0, Vec2(1.0, 1.0))

    scaled.vertices[0] shouldBe Vec2(-1.0, -1.0)
    scaled.vertices[1] shouldBe Vec2(3.0, -1.0)
    scaled.vertices[2] shouldBe Vec2(3.0, 3.0)
    scaled.vertices[3] shouldBe Vec2(-1.0, 3.0)
  }

  @Test
  fun `scale with factor 2 quadruples the area`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val scaled = square.scale(2.0, square.centroid())

    abs(scaled.area() - 4.0 * square.area()) shouldBeLessThan 1e-10
  }

  @Test
  fun `scale with factor 0_5 halves the distances from center`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    val scaled = square.scale(0.5, Vec2(1.0, 1.0))

    scaled.vertices[0] shouldBe Vec2(0.5, 0.5)
    scaled.vertices[1] shouldBe Vec2(1.5, 0.5)
    scaled.vertices[2] shouldBe Vec2(1.5, 1.5)
    scaled.vertices[3] shouldBe Vec2(0.5, 1.5)
  }

  @Test
  fun `dimensions returns 2`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.dimensions() shouldBe 2
  }

  @Test
  fun `isFinite returns true for finite vertices`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.isFinite() shouldBe true
  }

  @Test
  fun `isFinite returns false for infinite vertices`() {
    val polygon =
      Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(Double.POSITIVE_INFINITY, 1.0)))
    polygon.isFinite() shouldBe false
  }

  @Test
  fun `isInfinite returns true for infinite vertices`() {
    val polygon =
      Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(Double.POSITIVE_INFINITY, 1.0)))
    polygon.isInfinite() shouldBe true
  }

  @Test
  fun `isInfinite returns false for finite vertices`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.isInfinite() shouldBe false
  }

  @Test
  fun `isNaN returns true for NaN vertices`() {
    val polygon = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(Double.NaN, 1.0)))
    polygon.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns false for non-NaN vertices`() {
    val square = Polygon2.rectangle(0.0, 0.0, 2.0, 2.0)
    square.isNaN() shouldBe false
  }

  @Test
  fun `regular creates triangle with correct vertex count`() {
    val triangle = Polygon2.regular(3, 1.0)
    triangle.vertexCount shouldBe 3
  }

  @Test
  fun `regular creates square with correct area`() {
    val square = Polygon2.regular(4, 1.0)
    abs(square.area() - 2.0) shouldBeLessThan 1e-10 // Area of regular square with radius 1
  }

  @Test
  fun `regular creates hexagon with correct vertex count`() {
    val hexagon = Polygon2.regular(6, 1.0)
    hexagon.vertexCount shouldBe 6
  }

  @Test
  fun `regular polygon is convex`() {
    val pentagon = Polygon2.regular(5, 1.0)
    pentagon.isConvex() shouldBe true
  }

  @Test
  fun `regular polygon with custom center`() {
    val triangle = Polygon2.regular(3, 1.0, center = Vec2(5.0, 5.0))
    val centroid = triangle.centroid()
    abs(centroid.x - 5.0) shouldBeLessThan 1e-10
    abs(centroid.y - 5.0) shouldBeLessThan 1e-10
  }

  @Test
  fun `regular polygon with start angle rotates vertices`() {
    val triangle1 = Polygon2.regular(3, 1.0, startAngle = 0.0)
    val triangle2 = Polygon2.regular(3, 1.0, startAngle = PI / 2)

    // Vertices should be different due to rotation
    (triangle1.vertices[0].x != triangle2.vertices[0].x) shouldBe true
  }

  @Test
  fun `regular requires at least 3 sides`() {
    assertFailsWith<IllegalArgumentException> { Polygon2.regular(2, 1.0) }
  }

  @Test
  fun `regular requires positive radius`() {
    assertFailsWith<IllegalArgumentException> { Polygon2.regular(3, -1.0) }
  }

  @Test
  fun `rectangle creates correct vertices`() {
    val rect = Polygon2.rectangle(1.0, 2.0, 5.0, 7.0)
    rect.vertices[0] shouldBe Vec2(1.0, 2.0)
    rect.vertices[1] shouldBe Vec2(5.0, 2.0)
    rect.vertices[2] shouldBe Vec2(5.0, 7.0)
    rect.vertices[3] shouldBe Vec2(1.0, 7.0)
  }

  @Test
  fun `rectangle calculates correct area`() {
    val rect = Polygon2.rectangle(0.0, 0.0, 3.0, 4.0)
    rect.area() shouldBe 12.0
  }

  @Test
  fun `rectangle requires maxX greater than minX`() {
    assertFailsWith<IllegalArgumentException> { Polygon2.rectangle(5.0, 0.0, 2.0, 10.0) }
  }

  @Test
  fun `rectangle requires maxY greater than minY`() {
    assertFailsWith<IllegalArgumentException> { Polygon2.rectangle(0.0, 10.0, 5.0, 2.0) }
  }

  @Test
  fun `centroid throws exception for degenerate polygon`() {
    // Degenerate polygon with all vertices on a line
    val degenerate = Polygon2(listOf(Vec2(0.0, 0.0), Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
    assertFailsWith<IllegalArgumentException> { degenerate.centroid() }
  }

  @Test
  fun `polygon with many vertices is handled correctly`() {
    val vertices =
      (0 until 100).map { i ->
        val angle = 2.0 * PI * i / 100.0
        Vec2(kotlin.math.cos(angle), kotlin.math.sin(angle))
      }
    val polygon = Polygon2(vertices)

    polygon.vertexCount shouldBe 100
    polygon.isConvex() shouldBe true
    abs(polygon.area() - PI) shouldBeLessThan 0.01 // Should be close to circle area
  }
}
