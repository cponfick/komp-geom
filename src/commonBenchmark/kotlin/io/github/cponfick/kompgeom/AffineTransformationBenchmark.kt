package io.github.cponfick.kompgeom

import io.github.cponfick.kompgeom.euclidean.threed.AffineTransformationMatrix3
import io.github.cponfick.kompgeom.euclidean.threed.MutableVec3
import io.github.cponfick.kompgeom.euclidean.threed.RotationSequence
import io.github.cponfick.kompgeom.euclidean.threed.Vec3
import kotlin.math.PI
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
open class AffineTransformationBenchmark {
  @Param("10000", "100000", "1000000") var count: Int = 0

  private lateinit var immutableVertices: List<Vec3>
  private lateinit var mutableVertices: List<MutableVec3>
  private lateinit var transformation: AffineTransformationMatrix3

  @Setup
  fun setup() {
    immutableVertices =
      List(count) { i -> Vec3(i.toDouble(), (i * 2).toDouble(), (i * 3).toDouble()) }
    mutableVertices =
      List(count) { i -> MutableVec3(i.toDouble(), (i * 2).toDouble(), (i * 3).toDouble()) }
    transformation =
      AffineTransformationMatrix3.createScaling(2.0, 3.0, 4.0)
        .rotate(PI / 4, PI / 2, PI / 3, RotationSequence.XYZ)
        .translate(1.0, 2.0, 3.0)
  }

  @Benchmark
  fun immutableTransformation(bh: Blackhole) {
    val transformed = immutableVertices.map { transformation.apply(it) }
    val sum = transformed.fold(Vec3.ZERO) { acc, v -> acc + v }
    bh.consume(sum)
  }

  @Benchmark
  fun mutableTransformation(bh: Blackhole) {
    val transformed = mutableVertices.map { transformation.apply(it) }
    val sum = transformed.fold(MutableVec3.zero()) { acc, v -> acc + v }
    bh.consume(sum)
  }
}
