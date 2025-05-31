package io.github.cponfick.kompgeom.algorithms

/**
 * Interface for all algorithms in the KompGeom library.
 *
 * @param Output The type of output produced by the algorithm.
 */
public interface IAlgorithm<Output> {

  /**
   * Runs the algorithm and returns the output.
   *
   * @return The output of the algorithm.
   */
  public fun run(): Output

  /**
   * Returns the name of the algorithm.
   *
   * @return The name of the algorithm.
   */
  public fun getName(): String

  /**
   * Returns the group to which the algorithm belongs. This is used for categorization and
   * organization of algorithms.
   *
   * @return The group name of the algorithm.
   */
  public fun getGroup(): String

  /**
   * Returns a unique identifier for the algorithm, combining its group and name. The identifier is
   * formatted as "group:name", with both group and name in lowercase and spaces replaced by
   * hyphens.
   *
   * @return A unique identifier for the algorithm.
   */
  public fun getId(): String =
    "${getGroup().lowercase().replace(" ","-")}:${getName().lowercase().replace(" ","-")}"
}
