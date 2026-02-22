package io.github.cponfick.kompgeom.core

internal fun Double.assertIsFiniteAndNotZero(): Double {
  require(isFinite() && this != 0.0) { "Value is not finite or is zero: $this" }
  return this
}

internal fun Double.assertIsFinite(): Double {
  require(isFinite()) { "Value is not finite: $this" }
  return this
}
