package io.github.cponfick.kompgeom.euclidean.utils

import io.github.cponfick.kompgeom.euclidean.utils.VectorUtil.norm
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class VectorUtilTest {
  @Test
  fun `norm calculates the norm of two components`() {
    norm(3.0, 4.0) shouldBe 5.0
  }

  @Test
  fun `norm calculates the norm of three components`() {
    norm(1.0, 2.0, 2.0) shouldBe 3.0
  }

  @Test
  fun `linear combination of four components`() {
    VectorUtil.linearCombination(3.0, 4.0, 5.0, 6.0) shouldBe 42
  }

  @Test
  fun `linear combination of 6 components`() {
    VectorUtil.linearCombination(1.0, 2.0, 3.0, 4.0, 5.0, 6.0) shouldBe 44
  }
}
