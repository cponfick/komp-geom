package io.github.cponfick.kompgeom.core

import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

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
