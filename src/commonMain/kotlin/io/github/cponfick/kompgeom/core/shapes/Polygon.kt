package io.github.cponfick.kompgeom.core.shapes

import io.github.cponfick.kompgeom.core.Spatial
import io.github.cponfick.kompgeom.core.Vector
import io.github.cponfick.kompgeom.core.transform.Transformer

/**
 * Represents a polygon in a vector space.
 *
 * A polygon is defined by an ordered list of vertices that form a closed shape. The polygon can
 * exist in any dimensional space (2D, 3D, etc.) as determined by the vector type.
 *
 * @param V The type of vector representing the vertices of the polygon.
 */
public interface Polygon<V : Vector<V>> : Spatial {
  /**
   * Returns the list of vertices defining the polygon.
   *
   * The vertices are ordered either clockwise or counterclockwise, and the last vertex is
   * implicitly connected to the first vertex to close the polygon.
   *
   * @return The list of vertices.
   */
  public val vertices: List<V>

  /**
   * Returns the number of vertices in the polygon.
   *
   * @return The vertex count.
   */
  public val vertexCount: Int
    get() = vertices.size

  /**
   * Computes the signed area of the polygon.
   *
   * @return The signed area of the polygon.
   */
  public fun signedArea(): Double

  /**
   * Computes the area of the polygon.
   *
   * @return The area of the polygon.
   */
  public fun area(): Double

  /**
   * Checks if the polygon is convex.
   *
   * A polygon is convex if all interior angles are less than 180 degrees.
   *
   * @return True if the polygon is convex, false otherwise.
   */
  public fun isConvex(): Boolean

  /**
   * Checks if the polygon is simple (non-self-intersecting).
   *
   * A simple polygon does not intersect itself.
   *
   * @return True if the polygon is simple, false otherwise.
   */
  public fun isSimple(): Boolean

  /**
   * Computes the perimeter of the polygon.
   *
   * The perimeter is the sum of the lengths of all edges.
   *
   * @return The perimeter of the polygon.
   */
  public fun perimeter(): Double

  /**
   * Computes the centroid (geometric center) of the polygon.
   *
   * For a non-self-intersecting polygon, this is the center of mass assuming uniform density.
   *
   * @return The centroid as a vector.
   */
  public fun centroid(): V

  /**
   * Checks if a point is inside the polygon.
   *
   * The exact behavior depends on the implementation and dimensionality of the polygon.
   *
   * @param point The point to check.
   * @return True if the point is inside or on the boundary of the polygon, false otherwise.
   */
  public fun contains(point: V): Boolean

  /**
   * Transforms the polygon by applying a transformation to all vertices.
   *
   * @param transformer The transformation to apply.
   * @return A new polygon with transformed vertices.
   */
  public fun transform(transformer: Transformer<V>): Polygon<V>

  /**
   * Reverses the order of vertices, effectively reversing the orientation of the polygon.
   *
   * @return A new polygon with vertices in reverse order.
   */
  public fun reverse(): Polygon<V>

  /**
   * Translates the polygon by a given offset vector.
   *
   * @param offset The vector by which to translate the polygon.
   * @return A new polygon with all vertices translated by the offset.
   */
  public fun translate(offset: V): Polygon<V>

  /**
   * Scales the polygon by a given factor around a center point.
   *
   * @param factor The scaling factor.
   * @param center The center point for scaling. Defaults to the centroid if not specified.
   * @return A new polygon with all vertices scaled.
   */
  public fun scale(factor: Double, center: V): Polygon<V>
}
