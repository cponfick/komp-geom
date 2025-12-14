package io.github.cponfick.kompgeom.algorithms

/**
 * Interface for all algorithms in the KompGeom library.
 *
 * @param Output The type of output produced by the algorithm.
 */
public fun interface Algorithm<Output> {

  /**
   * Execute the algorithm and returns the output.
   *
   * @return The output of the algorithm.
   */
  public fun execute(): Output

  /**
   * Companion interface that defines static-like functions that implementations should provide.
   * Implementing classes should have a companion object that implements this interface. To provide
   * the user with useful meta-information about the algorithm, such as its name, group, time
   * complexity, and space complexity.
   */
  public interface AlgorithmInfo {
    /**
     * Returns the group to which the algorithm belongs. This is used for categorization and
     * organization of algorithms.
     *
     * @return The group name of the algorithm.
     */
    public fun getGroup(): String

    /** Returns the static name of the algorithm implementation. */
    public fun getName(): String

    /**
     * Returns a unique identifier for the algorithm, combining its group and name. The identifier
     * is formatted as "group:name", with both group and name in lowercase and spaces replaced by
     * hyphens.
     *
     * @return A unique identifier for the algorithm.
     */
    public fun getId(): String =
      "${getGroup().lowercase().replace(" ", "-")}:${getName().lowercase().replace(" ", "-")}"

    /** Returns the time complexity of the algorithm. */
    public fun getTimeComplexity(): String

    /** Returns the space complexity of the algorithm. */
    public fun getSpaceComplexity(): String
  }
}
