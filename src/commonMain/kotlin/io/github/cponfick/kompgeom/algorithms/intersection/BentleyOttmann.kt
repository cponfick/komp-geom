package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.collections.MutableRedBlackTreeMap
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionData
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.core.shapes.Segment2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import kotlin.math.min

/** A pair of input segments and their intersection returned by [BentleyOttmann]. */
public data class SegmentIntersection(
  public val first: Int,
  public val second: Int,
  public val intersection: IntersectionData<Vec2>,
)

/**
 * Reports all intersections among a collection of two-dimensional segments.
 *
 * This is the Bentley-Ottmann sweep-line algorithm. Unlike [ShamosHoey], which stops at the first
 * intersection, this algorithm schedules discovered crossing events and reports every intersection.
 * Its running time is O((n + k) log n), where k is the number of reported intersections, and its
 * space complexity is O(n + k).
 *
 * Collinear overlaps are reported when neighboring segments enter the sweep status. A pair is
 * reported only once, even when it meets at an endpoint and later participates in another event.
 */
public class BentleyOttmann(
  segments: Collection<Segment2<*>>,
  private val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
) : Algorithm<List<SegmentIntersection>> {
  private val input = segments.toList()

  @Suppress("kotlin:S3776")
  override fun execute(): List<SegmentIntersection> {
    if (input.size < 2) return emptyList()
    val segments = input.mapIndexed { index, segment -> SweepSegment(index, segment) }
    val reported = mutableSetOf<Pair<Int, Int>>()
    val result = mutableListOf<SegmentIntersection>()

    // A vertical segment has no open interval to the right of its left endpoint. It can therefore
    // intersect several segments at that event without ever becoming adjacent to all of them in
    // the sweep status. Handle these degenerate sweep objects explicitly before processing the
    // ordinary Bentley-Ottmann events.
    segments
      .filter { it.isVertical }
      .forEach { vertical ->
        segments
          .filter { !it.isVertical }
          .forEach { other ->
            val pair = minOf(vertical.index, other.index) to maxOf(vertical.index, other.index)
            val data = vertical.segment.intersection(other.segment, precision)
            if (data.type != IntersectionType.NONE && reported.add(pair)) {
              result += SegmentIntersection(pair.first, pair.second, data)
            }
          }
      }
    segments
      .filter { it.isVertical }
      .forEachIndexed { offset, first ->
        segments
          .filter { it.isVertical }
          .drop(offset + 1)
          .forEach { second ->
            val pair = minOf(first.index, second.index) to maxOf(first.index, second.index)
            val data = first.segment.intersection(second.segment, precision)
            if (data.type != IntersectionType.NONE && reported.add(pair)) {
              result += SegmentIntersection(pair.first, pair.second, data)
            }
          }
      }

    val queue = EventQueue()
    val sweepSegments = segments.filterNot { it.isVertical }
    sweepSegments.forEach {
      queue.add(Event(it.left.x, it.left.y, EventKind.START, it))
      queue.add(Event(it.right.x, it.right.y, EventKind.END, it))
    }
    var sweepX = queue.peek()?.x ?: return result
    val active =
      MutableRedBlackTreeMap<SweepSegment, Unit> { a, b ->
        val ay = heightAt(a, sweepX)
        val by = heightAt(b, sweepX)
        when {
          precision.lt(ay, by) -> -1
          precision.gt(ay, by) -> 1
          else -> {
            val aslope = slope(a)
            val bslope = slope(b)
            when {
              precision.lt(aslope, bslope) -> -1
              precision.gt(aslope, bslope) -> 1
              else -> a.index.compareTo(b.index)
            }
          }
        }
      }
    val scheduled = mutableSetOf<Triple<Int, Int, Long>>()

    fun report(a: SweepSegment, b: SweepSegment, data: IntersectionData<Vec2>) {
      val pair = minOf(a.index, b.index) to maxOf(a.index, b.index)
      if (data.type != IntersectionType.NONE && reported.add(pair))
        result += SegmentIntersection(pair.first, pair.second, data)
    }
    fun check(a: SweepSegment?, b: SweepSegment?) {
      if (a == null || b == null) return
      val data = a.segment.intersection(b.segment, precision)
      if (data.type == IntersectionType.NONE) return
      if (data.type == IntersectionType.OVERLAP) {
        report(a, b, data)
        return
      }
      val point = data.point!!
      if (precision.lt(point.x, sweepX)) return
      val key = minOf(a.index, b.index) to maxOf(a.index, b.index)
      val eventKey = Triple(key.first, key.second, point.x.toBits() xor point.y.toBits())
      if (scheduled.add(eventKey))
        queue.add(Event(point.x, point.y, EventKind.INTERSECTION, a, b, data))
    }

    while (queue.peek() != null) {
      val event = queue.remove()
      sweepX = event.x
      when (event.kind) {
        EventKind.START -> {
          active[event.first] = Unit
          check(active.lower(event.first), event.first)
          check(event.first, active.higher(event.first))
        }
        EventKind.END -> {
          val lower = active.lower(event.first)
          val upper = active.higher(event.first)
          check(lower, upper)
          active.remove(event.first)
        }
        EventKind.INTERSECTION -> {
          report(event.first, event.second!!, event.data!!)
          active.remove(event.first)
          active.remove(event.second)
          active[event.second] = Unit
          active[event.first] = Unit
          check(active.lower(event.second), event.second)
          check(event.first, active.higher(event.first))
        }
      }
    }
    return result
  }

  private fun heightAt(s: SweepSegment, x: Double): Double {
    val start = s.segment.start
    val dx = s.segment.end.x - start.x
    return if (precision.eqZero(dx)) min(start.y, s.segment.end.y)
    else start.y + (x - start.x) * (s.segment.end.y - start.y) / dx
  }

  private fun slope(s: SweepSegment): Double {
    val dx = s.segment.end.x - s.segment.start.x
    return if (precision.eqZero(dx)) 0.0 else (s.segment.end.y - s.segment.start.y) / dx
  }

  private class SweepSegment(val index: Int, val segment: Segment2<*>) {
    val isVertical = segment.start.x == segment.end.x
    val left =
      if (
        segment.start.x < segment.end.x ||
          segment.start.x == segment.end.x && segment.start.y <= segment.end.y
      )
        Vec2.from(segment.start)
      else Vec2.from(segment.end)
    val right =
      if (left.x == segment.start.x && left.y == segment.start.y) Vec2.from(segment.end)
      else Vec2.from(segment.start)
  }

  private enum class EventKind {
    START,
    INTERSECTION,
    END,
  }

  private data class Event(
    val x: Double,
    val y: Double,
    val kind: EventKind,
    val first: SweepSegment,
    val second: SweepSegment? = null,
    val data: IntersectionData<Vec2>? = null,
  )

  private class EventQueue {
    private val heap = mutableListOf<Event>()

    fun peek(): Event? = heap.firstOrNull()

    fun add(event: Event) {
      heap += event
      var index = heap.lastIndex
      while (index > 0) {
        val parent = (index - 1) / 2
        if (compare(heap[parent], heap[index]) <= 0) break
        val temporary = heap[parent]
        heap[parent] = heap[index]
        heap[index] = temporary
        index = parent
      }
    }

    fun remove(): Event {
      val result = heap.first()
      val last = heap.removeAt(heap.lastIndex)
      if (heap.isNotEmpty()) {
        heap[0] = last
        var index = 0
        while (true) {
          val left = index * 2 + 1
          if (left >= heap.size) break
          val right = left + 1
          val child = if (right < heap.size && compare(heap[right], heap[left]) < 0) right else left
          if (compare(heap[index], heap[child]) <= 0) break
          val temporary = heap[index]
          heap[index] = heap[child]
          heap[child] = temporary
          index = child
        }
      }
      return result
    }

    private fun compare(a: Event, b: Event): Int =
      compareValuesBy(a, b, { it.x }, { it.y }, { it.kind.ordinal }, { it.first.index })
  }

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Intersection"

    override fun getName(): String = "Bentley-Ottmann"

    override fun getTimeComplexity(): String = "O((n + k) log n)"

    override fun getSpaceComplexity(): String = "O(n + k)"
  }
}
