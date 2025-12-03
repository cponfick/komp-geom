package io.github.cponfick.kompgeom.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DoubleUtilTest {
  @Test
  fun `assertIsFiniteAndNotZero throws exception for non-finite or zero values`() {
    val nonFiniteValues =
      listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0)
    for (value in nonFiniteValues) {
      assertFailsWith<IllegalArgumentException> { value.assertIsFiniteAndNotZero() }
    }
  }

  @Test
  fun `assertIsFiniteAndNotZero returns value for finite non-zero values`() {
    val finiteValues = listOf(1.0, -1.0, 3.14, -2.718)
    for (value in finiteValues) {
      assertEquals(value, value.assertIsFiniteAndNotZero())
    }
  }

  @Test
  fun `assertIsFinite throws exception for non-finite values`() {
    val nonFiniteValues = listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
    for (value in nonFiniteValues) {
      assertFailsWith<IllegalArgumentException> { value.assertIsFinite() }
    }
  }

  @Test
  fun `assertIsFinite returns value for finite values`() {
    val finiteValues = listOf(1.0, -1.0, 3.14, -2.718)
    for (value in finiteValues) {
      assertEquals(value, value.assertIsFinite())
    }
  }
}
