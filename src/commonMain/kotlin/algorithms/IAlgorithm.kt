package algorithms

interface IAlgorithm<Output> {
  fun run(): Output

  fun getName(): String

  fun getGroup(): String

  fun getId(): String =
    "${getGroup().lowercase().replace(" ","-")}:${getName().lowercase().replace(" ","-")}"
}
