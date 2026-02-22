package io.github.cponfick.kompgeom.euclidean.internal

import io.github.cponfick.kompgeom.core.equivalence.EpsilonDoubleEquivalence
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class VectorUtilTest {
  @Test
  fun `norm calculates the norm of two components`() {
    VectorUtil.norm(3.0, 4.0) shouldBe 5.0
  }

  @Test
  fun `norm calculates the norm of three components`() {
    VectorUtil.norm(1.0, 2.0, 2.0) shouldBe 3.0
  }

  @Test
  fun `linear combination of four components`() {
    VectorUtil.linearCombination(3.0, 4.0, 5.0, 6.0) shouldBe 42.0
  }

  @Test
  fun `linear combination of 6 components`() {
    VectorUtil.linearCombination(1.0, 2.0, 3.0, 4.0, 5.0, 6.0) shouldBe 44.0
  }

  @Test
  fun `inverseNorm calculates the inverse norm of two components`() {
    VectorUtil.inverseNorm(3.0, 4.0) shouldBe 0.2
  }

  @Test
  fun `inverseNorm throws exception on zero vector`() {
    shouldThrow<ArithmeticException> { VectorUtil.inverseNorm(0.0, 0.0) }
  }

  @Test
  fun `inverseNorm calculates the inverse norm of three components`() {
    VectorUtil.inverseNorm(2.0, 2.0, 1.0) shouldBe (1.0 / 3.0)
  }

  @Test
  fun `inverseNorm throws exception on zero 3d vector`() {
    shouldThrow<ArithmeticException> { VectorUtil.inverseNorm(0.0, 0.0, 0.0) }
  }

  @Test
  fun `inverseNorm 2d throws exception on near-zero vector with custom equivalence`() {
    val loosePrecision = EpsilonDoubleEquivalence(1.0)
    shouldThrow<ArithmeticException> { VectorUtil.inverseNorm(1e-10, 1e-10, loosePrecision) }
  }

  @Test
  fun `inverseNorm 3d throws exception on near-zero vector with custom equivalence`() {
    val loosePrecision = EpsilonDoubleEquivalence(1.0)
    shouldThrow<ArithmeticException> { VectorUtil.inverseNorm(1e-10, 1e-10, 1e-10, loosePrecision) }
  }
}
