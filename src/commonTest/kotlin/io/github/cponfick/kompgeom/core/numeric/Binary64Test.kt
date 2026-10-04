package io.github.cponfick.kompgeom.core.numeric

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Binary64Test {
  @Test
  fun `decodes zeros canonically`() {
    Binary64.decode(0.0) shouldBeDecoded (0L to 0)
    Binary64.decode(-0.0) shouldBeDecoded (0L to 0)
  }

  @Test
  fun `decodes normal values with the implicit bit`() {
    Binary64.decode(1.0) shouldBeDecoded ((1L shl 52) to -52)
    Binary64.decode(-1.5) shouldBeDecoded (-(3L shl 51) to -52)
    Binary64.decode(Double.MIN_VALUE) shouldBeDecoded (1L to -1074)
  }

  @Test
  fun `decodes subnormal and normal boundary`() {
    Binary64.decode(Double.fromBits((1L shl 52) - 1L)) shouldBeDecoded (((1L shl 52) - 1L) to -1074)
    Binary64.decode(Double.fromBits(1L shl 52)) shouldBeDecoded ((1L shl 52) to -1074)
  }

  @Test
  fun `decodes signs and extreme finite values`() {
    Binary64.decode(Double.fromBits(0x7fefffffffffffffL)) shouldBeDecoded (0x1fffffffffffffL to 971)
    Binary64.decode(Double.fromBits(Long.MIN_VALUE or 0x7fefffffffffffffL)) shouldBeDecoded
      (-0x1fffffffffffffL to 971)
  }

  @Test
  fun `rejects infinities and nan`() {
    assertFailsWith<IllegalArgumentException> { Binary64.decode(Double.POSITIVE_INFINITY) }
    assertFailsWith<IllegalArgumentException> { Binary64.decode(Double.NEGATIVE_INFINITY) }
    assertFailsWith<IllegalArgumentException> {
      Binary64.decode(Double.fromBits(0x7ff0000000000001L))
    }
  }

  @Test
  fun `validates decoded value invariants and equality`() {
    assertFailsWith<IllegalArgumentException> { Binary64(0L, 1) }
    assertFailsWith<IllegalArgumentException> { Binary64(1L shl 53, 0) }

    val value = Binary64(3, -2)
    assertEquals(true, value.equals(value))
    assertEquals(value, Binary64(3, -2))
    assertEquals(false, value.equals(Binary64(-3, -2)))
    assertEquals(false, value.equals("3/4"))
    assertEquals(value.hashCode(), Binary64(3, -2).hashCode())
  }

  private infix fun Binary64.shouldBeDecoded(expected: Pair<Long, Int>) {
    assertEquals(expected.first, signedSignificand)
    assertEquals(expected.second, binaryExponent)
  }
}
