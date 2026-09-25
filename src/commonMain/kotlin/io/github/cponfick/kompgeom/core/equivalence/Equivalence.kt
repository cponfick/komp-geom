package io.github.cponfick.kompgeom.core.equivalence

/** The default epsilon for geometric computations. */
public const val GEOMETRIC_EPSILON: Double = 1e-10

/**
 * The default double equivalence used in geometric computations.
 *
 * This immutable instance uses [GEOMETRIC_EPSILON]. Create an [EpsilonDoubleEquivalence] directly
 * when an operation needs a different precision.
 */
public val DEFAULT_DOUBLE_EQUIVALENCE: DoubleEquivalence =
  EpsilonDoubleEquivalence(GEOMETRIC_EPSILON)
