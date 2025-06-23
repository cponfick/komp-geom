package io.github.cponfick.kompgeom.euclidean

internal fun Double.assertIsFiniteAndNotZero(): Double {
  if (!isFinite() || this == 0.0) {
    throw IllegalArgumentException("Value is not finite or is zero: $this")
  }
  return this
}
