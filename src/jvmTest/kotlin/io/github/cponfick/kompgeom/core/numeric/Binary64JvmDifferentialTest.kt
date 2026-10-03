package io.github.cponfick.kompgeom.core.numeric

import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Checks the common decoder against an independently written raw-field decoder. */
class Binary64JvmDifferentialTest {
  @Test
  fun `decodes deterministic randomized finite bit patterns`() {
    val random = Random(0x64dec0deL)
    repeat(500) {
      var bits = random.nextLong()
      if (((bits ushr 52) and 0x7ffL) == 0x7ffL) {
        bits = (bits and Long.MIN_VALUE) or (0x7feL shl 52) or (bits and ((1L shl 52) - 1L))
      }
      val expected = independentDecode(bits)
      val actual = Binary64.decode(Double.fromBits(bits))
      assertEquals(expected.first, actual.signedSignificand)
      assertEquals(expected.second, actual.binaryExponent)
    }
  }

  @Test
  fun `rejects randomized nonfinite patterns`() {
    val random = Random(0x188L)
    repeat(50) {
      val bits = (random.nextLong() and ((1L shl 52) - 1L)) or (0x7ffL shl 52)
      assertFailsWith<IllegalArgumentException> { Binary64.decode(Double.fromBits(bits)) }
    }
  }

  private fun independentDecode(bits: Long): Pair<Long, Int> {
    val fraction = bits and ((1L shl 52) - 1L)
    val exponent = ((bits ushr 52) and 0x7ffL).toInt()
    if (fraction == 0L && exponent == 0) return 0L to 0
    val significand = if (exponent == 0) fraction else fraction or (1L shl 52)
    val signed = if (bits < 0L) -significand else significand
    return signed to if (exponent == 0) -1074 else exponent - 1075
  }
}
