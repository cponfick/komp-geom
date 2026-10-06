package io.github.cponfick.kompgeom.core.numeric

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ExactRationalLawsTest {
  @Test
  fun `small rational arithmetic laws hold`() {
    val values =
      (-5L..5L)
        .flatMap { numerator ->
          (-5L..5L)
            .filter { it != 0L }
            .map { denominator ->
              ExactRational.fromLong(numerator) / ExactRational.fromLong(denominator)
            }
        }
        .distinct()
        .take(15)
    for (a in values) {
      a.compareTo(a) shouldBe 0
      for (b in values) {
        (a.compareTo(b) == 0) shouldBe (a == b)
        (a + b) shouldBe (b + a)
        (a * b) shouldBe (b * a)
        for (c in values.take(5)) {
          (a + (b + c)) shouldBe ((a + b) + c)
          (a * (b + c)) shouldBe (a * b + a * c)
        }
      }
    }
  }

  @Test
  fun `order is transitive and positive scaling preserves it`() {
    val values = listOf(-2L, -1L, 0L, 1L, 2L).map(ExactRational::fromLong)
    for (a in values) for (b in values) for (c in values) {
      if (b in a..c) (a <= c) shouldBe true
      (a + ExactRational.ONE).compareTo(b + ExactRational.ONE) shouldBe a.compareTo(b)
      (a * ExactRational.fromLong(3)).compareTo(b * ExactRational.fromLong(3)) shouldBe
        a.compareTo(b)
    }
  }
}
