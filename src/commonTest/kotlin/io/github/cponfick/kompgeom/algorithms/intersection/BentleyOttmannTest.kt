package io.github.cponfick.kompgeom.algorithms.intersection

import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.github.cponfick.kompgeom.core.shapes.IntersectionType
import io.github.cponfick.kompgeom.euclidean.twod.Seg2
import io.github.cponfick.kompgeom.euclidean.twod.Vec2
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test

class BentleyOttmannTest {
  @Test
  fun `reports all crossing intersections`() {
    val result =
      BentleyOttmann(
          listOf(
            Seg2(Vec2(0.0, 0.0), Vec2(3.0, 3.0)),
            Seg2(Vec2(0.0, 3.0), Vec2(3.0, 0.0)),
            Seg2(Vec2(1.5, -1.0), Vec2(1.5, 4.0)),
          )
        )
        .execute()
    result.size shouldBe 3
    result.all { it.intersection.type == IntersectionType.POINT } shouldBe true
  }

  @Test
  fun `reports intersections with vertical segments at their endpoint`() {
    val segments =
      listOf(
        Seg2(Vec2(15.0, 15.0), Vec2(11.0, 13.0)),
        Seg2(Vec2(1.0, 8.0), Vec2(12.0, 0.0)),
        Seg2(Vec2(17.0, 12.0), Vec2(17.0, 0.0)),
        Seg2(Vec2(13.0, 3.0), Vec2(2.0, 12.0)),
        Seg2(Vec2(8.0, 16.0), Vec2(9.0, 14.0)),
        Seg2(Vec2(4.0, 10.0), Vec2(15.0, 9.0)),
        Seg2(Vec2(0.0, 5.0), Vec2(17.0, 11.0)),
        Seg2(Vec2(19.0, 8.0), Vec2(4.0, 4.0)),
      )

    BentleyOttmann(segments).execute().map { it.first to it.second }.toSet() shouldBe
      setOf(1 to 6, 1 to 7, 2 to 6, 2 to 7, 3 to 5, 3 to 6, 3 to 7, 5 to 6)
  }

  @Test
  fun `reports every pair at a concurrent intersection`() {
    val segments =
      listOf(
        Seg2(Vec2(2.0, 1.0), Vec2(1.0, 0.0)),
        Seg2(Vec2(4.0, 5.0), Vec2(1.0, 4.0)),
        Seg2(Vec2(2.0, 3.0), Vec2(3.0, 7.0)),
        Seg2(Vec2(0.0, 4.0), Vec2(3.0, 1.0)),
        Seg2(Vec2(3.0, 1.0), Vec2(3.0, 4.0)),
        Seg2(Vec2(5.0, 2.0), Vec2(3.0, 0.0)),
        Seg2(Vec2(3.0, 2.0), Vec2(5.0, 0.0)),
        Seg2(Vec2(4.0, 1.0), Vec2(7.0, 3.0)),
      )

    BentleyOttmann(segments).execute().map { it.first to it.second }.toSet() shouldBe
      setOf(1 to 2, 3 to 4, 4 to 6, 5 to 6, 5 to 7, 6 to 7)
  }

  @Test
  fun `disjoint vertical segments remain sparse`() {
    val segments =
      (0 until 1500).map { x -> Seg2(Vec2(x.toDouble(), 0.0), Vec2(x.toDouble(), 1.0)) }
    BentleyOttmann(segments).execute() shouldBe emptyList()
  }

  @Test
  fun `handles points shared endpoints overlaps and events at equal x`() {
    val segments =
      listOf(
        Seg2(Vec2(0.0, 0.0), Vec2(4.0, 0.0)),
        Seg2(Vec2(2.0, 0.0), Vec2(5.0, 0.0)),
        Seg2(Vec2(2.0, 0.0), Vec2(2.0, 0.0)),
        Seg2(Vec2(2.0, -2.0), Vec2(2.0, 2.0)),
        Seg2(Vec2(2.0, 0.0), Vec2(3.0, 2.0)),
        Seg2(Vec2(2.0, 3.0), Vec2(2.0, 4.0)),
      )
    val expected =
      segments.indices
        .flatMap { i ->
          (i + 1 until segments.size).mapNotNull { j ->
            if (segments[i].intersection(segments[j]).type == IntersectionType.NONE) null
            else i to j
          }
        }
        .toSet()
    BentleyOttmann(segments).execute().map { it.first to it.second }.toSet() shouldBe expected
  }

  @Test
  fun `status order remains consistent across nontransitive epsilon heights`() {
    val precision = EpsilonDoubleEquivalence(1e-6)
    val segments =
      listOf(
        Seg2(Vec2(0.0, 0.0), Vec2(8.0, 0.0)),
        Seg2(Vec2(0.0, 0.75e-6), Vec2(8.0, 0.75e-6)),
        Seg2(Vec2(0.0, 1.5e-6), Vec2(8.0, 1.5e-6)),
        Seg2(Vec2(1.0, -2.0), Vec2(5.0, 2.0)),
        Seg2(Vec2(1.0, 2.0), Vec2(5.0, -2.0)),
      )
    val expected =
      segments.indices
        .flatMap { i ->
          (i + 1 until segments.size).mapNotNull { j ->
            if (segments[i].intersection(segments[j], precision).type == IntersectionType.NONE) null
            else i to j
          }
        }
        .toSet()
    BentleyOttmann(segments, precision).execute().map { it.first to it.second }.toSet() shouldBe
      expected
  }

  // The pairwise epsilon intersection predicate can classify nearby but distinct events as
  // intersections that a floating-point sweep cannot order consistently. This is separate from
  // the (now transitive) AVL status ordering; it needs robust geometric event predicates.
  // Seed 804 first fails at iteration 8 (near-coincident endpoints); see issue #184.
  @Ignore
  @Test
  fun `perturbed event heights agree with pairwise intersections`() {
    val precision = EpsilonDoubleEquivalence(1e-8)
    val random = Random(804)
    repeat(80) {
      val segments =
        (0 until 16).map {
          fun point() =
            Vec2(
              random.nextInt(-4, 5).toDouble() + random.nextInt(-2, 3) * 0.6e-8,
              random.nextInt(-4, 5).toDouble() + random.nextInt(-2, 3) * 0.6e-8,
            )
          Seg2(point(), point())
        }
      val expected =
        segments.indices
          .flatMap { i ->
            (i + 1 until segments.size).mapNotNull { j ->
              if (segments[i].intersection(segments[j], precision).type == IntersectionType.NONE)
                null
              else i to j
            }
          }
          .toSet()
      BentleyOttmann(segments, precision).execute().map { it.first to it.second }.toSet() shouldBe
        expected
    }
  }

  @Test
  fun `randomized differential against pairwise intersection`() {
    val random = Random(1043)
    repeat(200) {
      val segments =
        (0 until 22).map {
          fun coordinate() = random.nextInt(-8, 9).toDouble()
          val a = Vec2(coordinate(), coordinate())
          val b = if (random.nextInt(6) == 0) a else Vec2(coordinate(), coordinate())
          Seg2(a, b)
        }
      val expected =
        segments.indices
          .flatMap { i ->
            (i + 1 until segments.size).mapNotNull { j ->
              if (segments[i].intersection(segments[j]).type == IntersectionType.NONE) null
              else i to j
            }
          }
          .toSet()
      BentleyOttmann(segments).execute().map { it.first to it.second }.toSet() shouldBe expected
    }
  }

  @Test
  fun `dense output reports each pair once`() {
    val segments = (1..60).map { i -> Seg2(Vec2(-i.toDouble(), -1.0), Vec2(i.toDouble(), 1.0)) }
    BentleyOttmann(segments).execute().size shouldBe 60 * 59 / 2
  }

  @Test
  fun `many distinct segments concurrent at one point`() {
    val segments = (1..70).map { i -> Seg2(Vec2(-1.0, -i.toDouble()), Vec2(1.0, i.toDouble())) }
    BentleyOttmann(segments).execute().size shouldBe 70 * 69 / 2
  }

  @Test
  fun `reports overlap once`() {
    val result =
      BentleyOttmann(
          listOf(Seg2(Vec2(0.0, 0.0), Vec2(3.0, 0.0)), Seg2(Vec2(1.0, 0.0), Vec2(2.0, 0.0)))
        )
        .execute()
    result.size shouldBe 1
    result.single().intersection.type shouldBe IntersectionType.OVERLAP
  }
}
