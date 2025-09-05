package io.github.cponfick.kompgeom.euclidean.threed

/**
 * Represents different rotation sequences for Euler and Tait-Bryan angles.
 *
 * Euler angles use the same axis twice (e.g., ZYZ), while Tait-Bryan angles use three different
 * axes (e.g., XYZ).
 */
public enum class RotationSequence {
  /** Rotate around X, then Y, then Z (Tait-Bryan angles) */
  XYZ,

  /** Rotate around X, then Z, then Y (Tait-Bryan angles) */
  XZY,

  /** Rotate around Y, then X, then Z (Tait-Bryan angles) */
  YXZ,

  /** Rotate around Y, then Z, then X (Tait-Bryan angles) */
  YZX,

  /** Rotate around Z, then X, then Y (Tait-Bryan angles) */
  ZXY,

  /** Rotate around Z, then Y, then X (Tait-Bryan angles) */
  ZYX,

  /** Rotate around Z, then Y, then Z (Euler angles) */
  ZYZ,

  /** Rotate around Z, then X, then Z (Euler angles) */
  ZXZ,

  /** Rotate around Y, then Z, then Y (Euler angles) */
  YZY,

  /** Rotate around Y, then X, then Y (Euler angles) */
  YXY,

  /** Rotate around X, then Z, then X (Euler angles) */
  XZX,

  /** Rotate around X, then Y, then X (Euler angles) */
  XYX,
}
