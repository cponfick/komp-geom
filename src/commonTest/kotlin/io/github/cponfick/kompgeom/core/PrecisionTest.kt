package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PrecisionTest {
  private val equivalence = TestDoubleEquivalence()

  @Test
  fun `a equal to b`() {
    val a = 1.0
    val b = 1.0
    equivalence.eq(a, b) shouldBe true
  }

  @Test
  fun `a not equal to b`() {
    val a = 1.0
    val b = 2.0
    equivalence.eq(a, b) shouldBe false
  }

  @Test
  fun `a equal to zero`() {
    val a = 0.0
    equivalence.eqZero(a) shouldBe true
  }

  @Test
  fun `a not equal to zero`() {
    val a = 1.0
    equivalence.eqZero(a) shouldBe false
  }

  @Test
  fun `a is less than b`() {
    val a = 1.0
    val b = 2.0
    equivalence.lt(a, b) shouldBe true
  }

  @Test
  fun `a is not less than b`() {
    val a = 2.0
    val b = 1.0
    equivalence.lt(a, b) shouldBe false
  }

  @Test
  fun `a is less than or equal to b`() {
    val a = 1.0
    val b = 2.0
    equivalence.lte(a, b) shouldBe true
  }

  @Test
  fun `a is not less than or equal to b`() {
    val a = 2.0
    val b = 1.0
    equivalence.lte(a, b) shouldBe false
  }

  @Test
  fun `a is greater than b`() {
    val a = 2.0
    val b = 1.0
    equivalence.gt(a, b) shouldBe true
  }

  @Test
  fun `a is not greater than b`() {
    val a = 1.0
    val b = 2.0
    equivalence.gt(a, b) shouldBe false
  }

  @Test
  fun `a is greater than or equal to b`() {
    val a = 2.0
    val b = 1.0
    equivalence.gte(a, b) shouldBe true
  }

  @Test
  fun `a is not greater than or equal to b`() {
    val a = 1.0
    val b = 2.0
    equivalence.gte(a, b) shouldBe false
  }

  @Test
  fun `a is greater than b with equal a and b`() {
    val a = 1.0
    val b = 1.0
    equivalence.gte(a, b) shouldBe true
  }

  @Test
  fun `a is less than b with equal a and b`() {
    val a = 1.0
    val b = 1.0
    equivalence.lte(a, b) shouldBe true
  }

  @Test
  fun `signum of a is zero`() {
    val a = 0.0
    equivalence.signum(a) shouldBe 0.0
  }

  @Test
  fun `signum of a is positive`() {
    val a = 1.0
    equivalence.signum(a) shouldBe 1.0
  }

  @Test
  fun `signum of a is negative`() {
    val a = -1.0
    equivalence.signum(a) shouldBe -1.0
  }

  @Test
  fun `signum of a is NaN`() {
    val a = Double.NaN
    equivalence.signum(a).isNaN() shouldBe true
  }

  @Test
  fun `signum of a is negative zero`() {
    val a = -0.0
    equivalence.signum(a) shouldBe -0.0
  }

  @Test
  fun `equal infinities compare equal`() {
    val epsilonEquivalence = EpsilonDoubleEquivalence()
    epsilonEquivalence.eq(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) shouldBe true
    epsilonEquivalence.eq(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY) shouldBe true
    epsilonEquivalence.lt(Double.NEGATIVE_INFINITY, 0.0) shouldBe true
    epsilonEquivalence.gt(Double.POSITIVE_INFINITY, 0.0) shouldBe true
  }

  @Test
  fun `NaN has a deterministic ordering`() {
    val epsilonEquivalence = EpsilonDoubleEquivalence()
    epsilonEquivalence.eq(Double.NaN, Double.NaN) shouldBe true
    epsilonEquivalence.gt(Double.NaN, Double.POSITIVE_INFINITY) shouldBe true
    epsilonEquivalence.lt(1.0, Double.NaN) shouldBe true
  }

  @Test
  fun `approximate compare is not a sorted collection comparator`() {
    val tolerance = EpsilonDoubleEquivalence(1e-6)
    tolerance.compare(0.0, 0.75e-6) shouldBe 0
    tolerance.compare(0.75e-6, 1.5e-6) shouldBe 0
    tolerance.compare(0.0, 1.5e-6) shouldBe -1
  }

  @Test
  fun `epsilon must be finite and nonnegative`() {
    assertFailsWith<IllegalArgumentException> { EpsilonDoubleEquivalence(-1.0) }
    assertFailsWith<IllegalArgumentException> { EpsilonDoubleEquivalence(Double.NaN) }
    assertFailsWith<IllegalArgumentException> { EpsilonDoubleEquivalence(Double.POSITIVE_INFINITY) }
  }

  private companion object {
    private class TestDoubleEquivalence : EpsilonDoubleEquivalence() {
      override fun compare(a: Double, b: Double): Int {
        return when {
          a < b -> -1
          a > b -> 1
          else -> 0
        }
      }
    }
  }
}
