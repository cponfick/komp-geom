package io.github.cponfick.kompgeom.euclidean.utils

internal fun Double.assertIsFiniteAndNotZero(): Double {
  if (!isFinite() || this == 0.0) {
    throw IllegalArgumentException("Value is not finite or is zero: $this")
  }
  return this
}

internal fun Double.assertIsFinite(): Double {
  if (!isFinite()) {
    throw IllegalArgumentException("Value is not finite: $this")
  }
  return this
}
