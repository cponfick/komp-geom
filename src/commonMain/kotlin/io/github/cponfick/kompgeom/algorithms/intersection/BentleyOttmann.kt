package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionData
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Segment2
import io.github.cponfick.kompgeom.euclidean.twod.Seg2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2

/** A pair of input segments and their intersection returned by [BentleyOttmann]. */
public data class SegmentIntersection(
  public val first: Int,
  public val second: Int,
  public val intersection: IntersectionData<Vec2>,
)

/**
 * Reports intersections between two-dimensional segments. The sweep uses a shear to make vertical
 * segments x-monotone; reported coordinates remain in the original coordinate system. The status is
 * an order-maintenance tree: its keys never depend on the current sweep coordinate.
 *
 * Expected time O((n + k) log(n + k)), space O(n + k), for k reported pairs under consistent
 * geometric predicates. Epsilon-based intersection tests near their tolerance boundary can disagree
 * with floating-point event coordinates, so finite near-coincident inputs can produce missing
 * intersections (see [precision tracking issue](https://github.com/cponfick/komp-geom/issues/184)).
 */
public class BentleyOttmann(
  segments: Collection<Segment2<*>>,
  private val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Algorithm<List<SegmentIntersection>> {
  private val input = segments.toList()

  override fun execute(): List<SegmentIntersection> = SegmentSweep(input, precision).execute()

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Intersection"

    override fun getName(): String = "Bentley-Ottmann"

    override fun getTimeComplexity(): String = "O((n + k) log n)"

    override fun getSpaceComplexity(): String = "O(n + k)"
  }
}

/** Shared event/status invariants for intersection reporting and endpoint-only detection. */
internal class SegmentSweep(
  private val input: List<Segment2<*>>,
  private val precision: DoubleEquivalence,
  private val stopAtFirst: Boolean = false,
  // Valid only when every suppressed contact is an endpoint touch that cannot reorder active edges.
  private val endpointOnly: Boolean = false,
  private val allowedContact: (Int, Int, IntersectionData<Vec2>) -> Boolean = { _, _, _ -> false },
) {
  private data class Point(val x: Double, val y: Double) : Comparable<Point> {
    override fun compareTo(other: Point): Int =
      compareValues(x, other.x).takeIf { it != 0 } ?: compareValues(y, other.y)
  }

  private class Edge(val index: Int, val segment: Segment2<*>, val left: Point, val right: Point) {
    val slope: Double = if (left == right) 0.0 else (right.y - left.y) / (right.x - left.x)

    fun height(x: Double): Double = left.y + (x - left.x) * slope
  }

  private class Event(
    val point: Point,
    val start: Edge? = null,
    val end: Edge? = null,
    val pair: Pair<Int, Int>? = null,
  )

  private val results = mutableListOf<SegmentIntersection>()
  private val reported = mutableSetOf<Pair<Int, Int>>()
  private val pending = mutableSetOf<Pair<Int, Int>>()
  private val events = EventHeap()
  private val status = Status()
  private lateinit var edges: List<Edge>

  fun execute(): List<SegmentIntersection> {
    if (input.size < 2) return emptyList()
    // At most n slopes are forbidden; choosing the first available integer is deterministic.
    val forbidden =
      input
        .mapNotNull { s ->
          val dy = s.end.y - s.start.y
          if (dy == 0.0) null else -(s.end.x - s.start.x) / dy
        }
        .toSet()
    var alpha = 1.0
    while (alpha in forbidden) alpha++
    shearAlpha = alpha
    fun shear(x: Double, y: Double) = Point(x + alpha * y, y)
    edges =
      input.mapIndexed { i, s ->
        val a = shear(s.start.x, s.start.y)
        val b = shear(s.end.x, s.end.y)
        Edge(i, s, minOf(a, b), maxOf(a, b))
      }
    for (edge in edges) {
      events.add(Event(edge.left, start = edge))
      events.add(Event(edge.right, end = edge))
    }
    while (events.peek() != null) {
      val point = events.peek()!!.point
      val starts = mutableListOf<Edge>()
      val ends = mutableListOf<Edge>()
      val crossing = mutableListOf<Pair<Int, Int>>()
      do {
        val event = events.remove()
        event.start?.let(starts::add)
        event.end?.let(ends::add)
        event.pair?.let {
          crossing += it
          pending.remove(it)
        }
      } while (events.peek()?.point == point)

      // The incident status block is contiguous just before the event. Finding it by height also
      // handles endpoint events that have never been scheduled as pairwise crossing events.
      val crossingEdges = crossing.flatMap { listOf(edges[it.first], edges[it.second]) }
      val crossingIndices = crossingEdges.mapTo(mutableSetOf()) { it.index }
      // A computed crossing may be a few ULPs away from the exact meeting point after shearing.
      // Its scheduled participants must still exchange places, even if probing that rounded point
      // against either original segment fails the point-on-segment predicate.
      val incident =
        (status.at(point, precision).filter { touches(it, point) } +
            crossingEdges.filter(status::contains))
          .distinctBy { it.index }
      val group =
        (incident + starts + ends + crossingEdges)
          .distinctBy { it.index }
          .filter { it.index in crossingIndices || touches(it, point) }
      for (i in group.indices) for (j in i + 1 until group.size) {
        report(group[i], group[j])
        if (stopAtFirst && results.isNotEmpty()) return results
      }
      val removed = (incident + ends).distinctBy { it.index }.filter(status::contains)
      val firstRemoved = removed.minByOrNull { status.position(it) }
      val below = firstRemoved?.let(status::lower)
      val above = removed.maxByOrNull { status.position(it) }?.let(status::higher)
      // Locate the affected block before deletion. A crossing can produce slightly different
      // floating-point heights for segments at the same computed point; their right-side order
      // must be determined by slope, not by comparing those rounded heights.
      val insertionRank = firstRemoved?.let(status::position) ?: status.rankAt(point)
      removed.forEach(status::remove)
      val entering =
        (incident.filter { it.right > point } + starts.filter { it.right > point })
          .distinctBy { it.index }
          .sortedWith(compareBy<Edge>({ it.slope }, { it.index }))
      entering.forEachIndexed { index, edge -> status.insertAt(edge, insertionRank + index) }
      if (entering.isEmpty()) check(below, above, point)
      else {
        check(status.lower(entering.first()), entering.first(), point)
        check(entering.last(), status.higher(entering.last()), point)
        for (i in 1 until entering.size) check(entering[i - 1], entering[i], point)
      }
      if (stopAtFirst && results.isNotEmpty()) return results
    }
    return results
  }

  private fun touches(edge: Edge, point: Point): Boolean {
    if (edge.left > point || edge.right < point) return false
    // Check in original coordinates to avoid introducing a second, sheared precision model.
    val original = Vec2(point.x - shearAlpha * point.y, point.y)
    return edge.segment.intersection(Seg2(original, original), precision).type !=
      IntersectionType.NONE
  }

  private var shearAlpha: Double = 1.0

  private fun report(a: Edge, b: Edge) {
    val pair = minOf(a.index, b.index) to maxOf(a.index, b.index)
    if (pair in reported) return
    val data = a.segment.intersection(b.segment, precision)
    if (data.type != IntersectionType.NONE && !allowedContact(pair.first, pair.second, data)) {
      reported += pair
      results += SegmentIntersection(pair.first, pair.second, data)
    }
  }

  private fun check(a: Edge?, b: Edge?, now: Point) {
    if (a == null || b == null) return
    val pair = minOf(a.index, b.index) to maxOf(a.index, b.index)
    val data = a.segment.intersection(b.segment, precision)
    if (endpointOnly) {
      // Shamos–Hoey checks a newly neighboring pair immediately. If it is a real crossing,
      // there is no need to reach that crossing or maintain its right-hand status order.
      report(a, b)
      return
    }
    when (data.type) {
      IntersectionType.NONE -> return
      IntersectionType.OVERLAP -> report(a, b)
      IntersectionType.POINT -> {
        val intersection = data.point ?: return
        val at = Point(intersection.x + shearAlpha * intersection.y, intersection.y)
        if (at <= now) report(a, b) else if (pending.add(pair)) events.add(Event(at, pair = pair))
      }
    }
  }

  /** AVL order-maintenance tree. Rotations never compare geometry or change existing keys. */
  private inner class Status {
    private inner class Node(var edge: Edge) {
      var left: Node? = null
      var right: Node? = null
      var parent: Node? = null
      var size: Int = 1
      var height: Int = 1
    }

    private var root: Node? = null
    private val nodes = mutableMapOf<Int, Node>()

    private fun size(node: Node?): Int = node?.size ?: 0

    private fun height(node: Node?): Int = node?.height ?: 0

    private fun update(node: Node) {
      node.size = 1 + size(node.left) + size(node.right)
      node.height = 1 + maxOf(height(node.left), height(node.right))
    }

    private fun rebalance(start: Node?) {
      var node = start
      while (node != null) {
        update(node)
        val balance = height(node.left) - height(node.right)
        val newRoot =
          when {
            balance > 1 -> {
              val left = node.left!!
              if (height(left.left) < height(left.right)) rotate(left.right!!)
              rotate(node.left!!)
            }
            balance < -1 -> {
              val right = node.right!!
              if (height(right.right) < height(right.left)) rotate(right.left!!)
              rotate(node.right!!)
            }
            else -> node
          }
        node = newRoot.parent
      }
    }

    private fun rotate(node: Node): Node {
      val parent = node.parent!!
      val grand = parent.parent
      if (parent.left === node) {
        parent.left = node.right
        node.right?.parent = parent
        node.right = parent
      } else {
        parent.right = node.left
        node.left?.parent = parent
        node.left = parent
      }
      parent.parent = node
      node.parent = grand
      if (grand == null) root = node
      else if (grand.left === parent) grand.left = node else grand.right = node
      update(parent)
      update(node)
      return node
    }

    fun contains(edge: Edge): Boolean = edge.index in nodes

    fun position(edge: Edge): Int {
      var node = nodes.getValue(edge.index)
      var rank = size(node.left)
      while (node.parent != null) {
        if (node.parent!!.right === node) rank += size(node.parent!!.left) + 1
        node = node.parent!!
      }
      return rank
    }

    fun lower(edge: Edge): Edge? {
      var n = nodes[edge.index] ?: return null
      if (n.left != null) {
        n = n.left!!
        while (n.right != null) n = n.right!!
        return n.edge
      }
      while (n.parent != null && n.parent!!.left === n) n = n.parent!!
      return n.parent?.edge
    }

    fun higher(edge: Edge): Edge? {
      var n = nodes[edge.index] ?: return null
      if (n.right != null) {
        n = n.right!!
        while (n.left != null) n = n.left!!
        return n.edge
      }
      while (n.parent != null && n.parent!!.right === n) n = n.parent!!
      return n.parent?.edge
    }

    // Only compare a new event against the heights of unaffected segments. Exact ordering is
    // transitive; epsilon equivalence is reserved for geometric incidence tests in at().
    fun rankAt(point: Point): Int {
      var current = root
      var rank = 0
      while (current != null) {
        if (point.y <= current.edge.height(point.x)) current = current.left
        else {
          rank += size(current.left) + 1
          current = current.right
        }
      }
      return rank
    }

    // Insert by position, not with a comparator depending on sweep position or epsilon. The
    // affected block has already been removed and sorted in its right-of-event order.
    fun insertAt(edge: Edge, rank: Int) {
      require(rank in 0..size(root))
      val node = Node(edge)
      nodes[edge.index] = node
      var current = root
      var parent: Node? = null
      var offset = rank
      var insertLeft = false
      while (current != null) {
        parent = current
        insertLeft = offset <= size(current.left)
        if (insertLeft) current = current.left
        else {
          offset -= size(current.left) + 1
          current = current.right
        }
      }
      node.parent = parent
      if (parent == null) root = node
      else if (insertLeft) parent.left = node else parent.right = node
      rebalance(parent)
    }

    fun remove(edge: Edge) {
      var node = nodes.remove(edge.index) ?: return
      if (node.left != null && node.right != null) {
        var successor = node.right!!
        while (successor.left != null) successor = successor.left!!
        node.edge = successor.edge
        nodes[successor.edge.index] = node
        node = successor
      }
      val child = node.left ?: node.right
      val parent = node.parent
      child?.parent = parent
      if (parent == null) root = child
      else if (parent.left === node) parent.left = child else parent.right = child
      rebalance(parent)
    }

    fun at(point: Point, precision: DoubleEquivalence): List<Edge> {
      var n = root
      var match: Edge? = null
      while (n != null) {
        val height = n.edge.height(point.x)
        if (precision.eq(height, point.y)) {
          match = n.edge
          break
        }
        n = if (height < point.y) n.right else n.left
      }
      val center = match ?: return emptyList()
      val result = mutableListOf(center)
      var next = lower(center)
      while (next != null && precision.eq(next.height(point.x), point.y)) {
        result += next
        next = lower(next)
      }
      next = higher(center)
      while (next != null && precision.eq(next.height(point.x), point.y)) {
        result += next
        next = higher(next)
      }
      return result
    }
  }

  private inner class EventHeap {
    private val heap = mutableListOf<Event>()

    fun peek(): Event? = heap.firstOrNull()

    fun add(event: Event) {
      heap += event
      var i = heap.lastIndex
      while (i > 0) {
        val p = (i - 1) / 2
        if (heap[p].point <= heap[i].point) break
        val tmp = heap[p]
        heap[p] = heap[i]
        heap[i] = tmp
        i = p
      }
    }

    fun remove(): Event {
      val result = heap.first()
      val last = heap.removeAt(heap.lastIndex)
      if (heap.isNotEmpty()) {
        heap[0] = last
        var i = 0
        while (2 * i + 1 < heap.size) {
          val left = 2 * i + 1
          val right = left + 1
          val child = if (right < heap.size && heap[right].point < heap[left].point) right else left
          if (heap[i].point <= heap[child].point) break
          val tmp = heap[i]
          heap[i] = heap[child]
          heap[child] = tmp
          i = child
        }
      }
      return result
    }
  }
}
