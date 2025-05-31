package io.github.cponfick.kompgeom.algorithms

public interface IAlgorithm<Output> {
  public fun run(): Output

  public fun getName(): String

  public fun getGroup(): String

  public fun getId(): String =
    "${getGroup().lowercase().replace(" ","-")}:${getName().lowercase().replace(" ","-")}"
}
