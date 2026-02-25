package io.github.cponfick.kompgeom.euclidean.threed

import io.github.cponfick.kompgeom.core.transform.Transformer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class Polygon3Test {

  @Test
  fun `constructor requires at least 3 vertices`() {
    assertFailsWith<IllegalArgumentException> {
      Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)))
    }
  }

  @Test
  fun `constructor requires coplanar vertices`() {
    assertFailsWith<IllegalArgumentException> {
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 1.0), Vec3(0.0, 2.0, 0.0))
      )
    }
  }

  @Test
  fun `constructor accepts exactly 3 vertices`() {
    val triangle = listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.5, 1.0, 0.0))
    val polygon = Polygon3(triangle)
    polygon.vertexCount shouldBe 3
    polygon.vertices shouldContainExactly triangle
  }

  @Test
  fun `vertexCount returns the correct vertices`() {
    val vertices =
      listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
    val square = Polygon3(vertices)
    square.vertices shouldContainExactly vertices
  }

  @Test
  fun `edges returns correct number of edges and constructs edges correctly`() {
    val triangle = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.5, 1.0, 0.0)))
    triangle.edges shouldContainExactly
      listOf(
        Seg3(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0)),
        Seg3(Vec3(1.0, 0.0, 0.0), Vec3(0.5, 1.0, 0.0)),
        Seg3(Vec3(0.5, 1.0, 0.0), Vec3(0.0, 0.0, 0.0)),
      )
  }

  @Test
  fun `area calculates triangle area correctly`() {
    val triangle = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(3.0, 0.0, 0.0), Vec3(0.0, 4.0, 0.0)))
    triangle.area() shouldBe 6.0
  }

  @Test
  fun `area calculates square area correctly`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    square.area() shouldBe 4.0
  }

  @Test
  fun `perimeter calculates square perimeter correctly`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    square.perimeter() shouldBe 8.0
  }

  @Test
  fun `centroid returns center of square`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val centroid = square.centroid()
    centroid.x shouldBe 1.0
    centroid.y shouldBe 1.0
    centroid.z shouldBe 0.0
  }

  @Test
  fun `centroid returns center of square standing on x axis`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(0.0, 2.0, 0.0), Vec3(0.0, 2.0, 2.0), Vec3(0.0, 0.0, 2.0))
      )
    val centroid = square.centroid()
    centroid.x shouldBe 0.0
    centroid.y shouldBe 1.0
    centroid.z shouldBe 1.0
  }

  @Test
  fun `isConvex returns true for square`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    square.isConvex() shouldBe true
  }

  @Test
  fun `isConvex returns false for L standing on x axis`() {
    val lShape =
      Polygon3(
        listOf(
          Vec3(0.0, 0.0, 0.0),
          Vec3(0.0, 2.0, 0.0),
          Vec3(0.0, 2.0, 1.0),
          Vec3(0.0, 1.0, 1.0),
          Vec3(0.0, 1.0, 2.0),
          Vec3(0.0, 0.0, 2.0),
        )
      )
    lShape.isConvex() shouldBe false
  }

  @Test
  fun `translate moves all vertices by offset`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val translated = square.translate(Vec3(3.0, 4.0, 5.0))

    translated.vertices[0] shouldBe Vec3(3.0, 4.0, 5.0)
    translated.vertices[1] shouldBe Vec3(5.0, 4.0, 5.0)
    translated.vertices[2] shouldBe Vec3(5.0, 6.0, 5.0)
    translated.vertices[3] shouldBe Vec3(3.0, 6.0, 5.0)
  }

  @Test
  fun `scale with factor 2 doubles the distances from center`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val scaled = square.scale(2.0, Vec3(1.0, 1.0, 0.0))

    scaled.vertices[0] shouldBe Vec3(-1.0, -1.0, 0.0)
    scaled.vertices[1] shouldBe Vec3(3.0, -1.0, 0.0)
    scaled.vertices[2] shouldBe Vec3(3.0, 3.0, 0.0)
    scaled.vertices[3] shouldBe Vec3(-1.0, 3.0, 0.0)
  }

  @Test
  fun `dimensions returns 3`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    square.dimensions() shouldBe 3
  }

  @Test
  fun `reverse returns polygon with vertices in reverse order`() {
    val triangle = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.5, 1.0, 0.0)))
    val reversed = triangle.reverse()
    reversed.vertices shouldContainExactly
      listOf(Vec3(0.5, 1.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.0, 0.0, 0.0))
  }

  @Test
  fun `isFinite returns false if any vertex is not finite`() {
    val polygon =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(Double.POSITIVE_INFINITY, 0.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    polygon.isFinite() shouldBe false
  }

  @Test
  fun `isFinite returns true if all vertices are finite`() {
    val polygon = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(0.0, 2.0, 0.0)))
    polygon.isFinite() shouldBe true
  }

  @Test
  fun `isInfinite returns true if any vertex is infinite`() {
    val polygon =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(Double.POSITIVE_INFINITY, 0.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    polygon.isInfinite() shouldBe true
  }

  @Test
  fun `isInfinite returns false if all vertices are finite`() {
    val polygon = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(0.0, 2.0, 0.0)))
    polygon.isInfinite() shouldBe false
  }

  @Test
  fun `isNaN returns true if any vertex is NaN`() {
    val polygon =
      Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(Double.NaN, 0.0, 0.0), Vec3(0.0, 2.0, 0.0)))
    polygon.isNaN() shouldBe true
  }

  @Test
  fun `isNaN returns false if all vertices are numbers`() {
    val polygon = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(0.0, 2.0, 0.0)))
    polygon.isNaN() shouldBe false
  }

  @Test
  fun `transform applies transformation to all vertices`() {
    val triangle = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.5, 1.0, 0.0)))
    val transformer =
      object : Transformer<Vec3> {
        @Suppress("UNCHECKED_CAST")
        override fun <T : Vec3> apply(obj: T): T = Vec3(obj.x + 1, obj.y + 1, obj.z + 1) as T

        override fun inverse(): Transformer<Vec3> = this

        override fun preserveOrientation(): Boolean = true
      }

    val transformedTriangle = triangle.transform(transformer)
    transformedTriangle.vertices shouldContainExactly
      listOf(Vec3(1.0, 1.0, 1.0), Vec3(2.0, 1.0, 1.0), Vec3(1.5, 2.0, 1.0))
  }

  @Test
  fun `isSimple returns true for square`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    square.isSimple() shouldBe true
  }

  @Test
  fun `isSimple returns true for triangle`() {
    val triangle = Polygon3(listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 0.0, 0.0), Vec3(0.0, 1.0, 5.0)))
    triangle.isSimple() shouldBe true
  }

  @Test
  fun `contains returns true for point inside polygon`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val pointInside = Vec3(1.0, 1.0, 0.0)
    square.contains(pointInside) shouldBe true
  }

  @Test
  fun `contains returns false for point outside polygon but same plane`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val pointOutside = Vec3(3.0, 3.0, 0.0)
    square.contains(pointOutside) shouldBe false
  }

  @Test
  fun `contains returns true for point on polygon edge`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val pointOnEdge = Vec3(1.0, 0.0, 0.0)
    square.contains(pointOnEdge) shouldBe true
  }

  @Test
  fun `contains true for point in standing L shape on Z axis`() {
    val lShape =
      Polygon3(
        listOf(
          Vec3(0.0, 0.0, 0.0),
          Vec3(0.0, 2.0, 0.0),
          Vec3(0.0, 2.0, 1.0),
          Vec3(0.0, 1.0, 1.0),
          Vec3(0.0, 1.0, 2.0),
          Vec3(0.0, 0.0, 2.0),
        )
      )
    val pointInside = Vec3(0.0, 1.5, 0.5)
    lShape.contains(pointInside) shouldBe true
  }

  @Test
  fun `contains returns false for point not on polygon plane`() {
    val square =
      Polygon3(
        listOf(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 0.0, 0.0), Vec3(2.0, 2.0, 0.0), Vec3(0.0, 2.0, 0.0))
      )
    val pointOffPlane = Vec3(1.0, 1.0, 1.0)
    square.contains(pointOffPlane) shouldBe false
  }

  @Test
  fun `boundingBox returns correct bounds for square in xy plane`() {
    val square =
      Polygon3(
        listOf(Vec3(1.0, 2.0, 0.0), Vec3(5.0, 2.0, 0.0), Vec3(5.0, 7.0, 0.0), Vec3(1.0, 7.0, 0.0))
      )
    val (min, max) = square.boundingBox()
    min shouldBe Vec3(1.0, 2.0, 0.0)
    max shouldBe Vec3(5.0, 7.0, 0.0)
  }

  @Test
  fun `boundingBox returns correct bounds for triangle with z variation`() {
    val triangle = Polygon3(listOf(Vec3(1.0, 2.0, 3.0), Vec3(5.0, 2.0, 1.0), Vec3(3.0, 6.0, 2.0)))
    val (min, max) = triangle.boundingBox()
    min shouldBe Vec3(1.0, 2.0, 1.0)
    max shouldBe Vec3(5.0, 6.0, 3.0)
  }
}
