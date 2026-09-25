package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.algorithms.Algorithm
import io.github.cponfick.kompgeom.core.collections.MutableRedBlackTreeMap
import io.github.cponfick.kompgeom.core.equivalence.DEFAULT_DOUBLE_EQUIVALENCE
import io.github.cponfick.kompgeom.core.equivalence.DoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.Segment2
import kotlin.math.max
import kotlin.math.min

/**
 * Detects whether any pair of two-dimensional segments intersects.
 *
 * This is the Shamos-Hoey sweep-line algorithm. The event queue is sorted by the left endpoint of
 * each segment and the sweep status is a red-black tree ordered by the segment's height at the
 * current sweep position. Only neighboring segments need to be tested: if two segments intersect,
 * they become neighbors immediately before their first intersection.
 *
 * [ignoredPair] can be used by polygon algorithms to exclude pairs which are allowed to meet (for
 * example, consecutive polygon edges). Its arguments are the zero-based input indices in ascending
 * order. The predicate is not used to alter the sweep status; excluded pairs are simply not
 * reported.
 *
 * Degenerate (zero-length) segments and collinear overlaps are supported by the segment
 * intersection implementation used by this algorithm.
 *
 * @param segments The segments to inspect.
 * @param precision The equivalence used for geometric comparisons.
 * @param ignoredPair Predicate for pairs that should not count as intersections.
 */
public class SweepLineSegmentIntersection(
  segments: Collection<Segment2<*>>,
  private val precision: DoubleEquivalence = DEFAULT_DOUBLE_EQUIVALENCE,
  private val ignoredPair: (Int, Int) -> Boolean = { _, _ -> false },
) : Algorithm<Boolean> {
  private val input = segments.toList()

  override fun execute(): Boolean {
    if (input.size < 2) return false

    val sweepSegments = input.mapIndexed { index, segment -> SweepSegment(index, segment) }
    val events =
      sweepSegments
        .flatMap { segment ->
          listOf(Event(segment.left, true, segment), Event(segment.right, false, segment))
        }
        .sortedWith(
          compareBy<Event> { it.point.x }.thenBy { it.point.y }.thenByDescending { it.start }
        )

    var sweepX = events.first().point.x
    val status =
      MutableRedBlackTreeMap<SweepSegment, Unit> { first, second ->
        val heightComparison = compareHeights(first, second, sweepX)
        if (heightComparison != 0) heightComparison else first.index.compareTo(second.index)
      }

    for (event in events) {
      sweepX = event.point.x
      val segment = event.segment
      if (event.start) {
        status[segment] = Unit
        val lower = status.lower(segment)
        val upper = status.higher(segment)
        if (
          (lower != null && intersects(lower, segment)) ||
            (upper != null && intersects(segment, upper))
        ) {
          return true
        }
      } else {
        // Test the two segments which become neighbors after this one leaves the status.
        val lower = status.lower(segment)
        val upper = status.higher(segment)
        if (lower != null && upper != null && intersects(lower, upper)) return true
        status.remove(segment)
      }
    }
    return false
  }

  private fun intersects(first: SweepSegment, second: SweepSegment): Boolean {
    val firstIndex = min(first.index, second.index)
    val secondIndex = max(first.index, second.index)
    return !ignoredPair(firstIndex, secondIndex) &&
      first.segment.intersection(second.segment, precision).type !=
        io.github.cponfick.kompgeom.core.shapes.IntersectionType.NONE
  }

  private fun compareHeights(first: SweepSegment, second: SweepSegment, x: Double): Int {
    val firstHeight = heightAt(first, x)
    val secondHeight = heightAt(second, x)
    return when {
      precision.lt(firstHeight, secondHeight) -> -1
      precision.gt(firstHeight, secondHeight) -> 1
      else -> {
        // At a shared event x, order segments as they appear immediately to the
        // right of the event. This avoids endpoint ties hiding an intersection.
        val firstSlope = slope(first)
        val secondSlope = slope(second)
        when {
          precision.lt(firstSlope, secondSlope) -> -1
          precision.gt(firstSlope, secondSlope) -> 1
          else -> 0
        }
      }
    }
  }

  private fun slope(segment: SweepSegment): Double {
    val dx = segment.segment.end.x - segment.segment.start.x
    return if (precision.eqZero(dx)) 0.0 else (segment.segment.end.y - segment.segment.start.y) / dx
  }

  private fun heightAt(segment: SweepSegment, x: Double): Double {
    val start = segment.segment.start
    val end = segment.segment.end
    val dx = end.x - start.x
    return if (precision.eqZero(dx)) min(start.y, end.y)
    else start.y + (x - start.x) * (end.y - start.y) / dx
  }

  private class SweepSegment(val index: Int, val segment: Segment2<*>) {
    val left =
      if (
        segment.start.x < segment.end.x ||
          (segment.start.x == segment.end.x && segment.start.y <= segment.end.y)
      )
        segment.start
      else segment.end
    val right = if (left === segment.start) segment.end else segment.start
  }

  private data class Event(
    val point: io.github.cponfick.kompgeom.core.Vector2<*>,
    val start: Boolean,
    val segment: SweepSegment,
  )

  public companion object : Algorithm.AlgorithmInfo {
    override fun getGroup(): String = "Intersection"

    override fun getName(): String = "Sweep Line Segment Intersection"

    override fun getTimeComplexity(): String = "O(n log n)"

    override fun getSpaceComplexity(): String = "O(n)"
  }
}
