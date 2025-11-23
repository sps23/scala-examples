package io.github.sps23.parcollection

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.Tag

class ParCollectionBenchTest extends AnyFunSuiteLike {

  object BenchmarkTag extends Tag("io.github.sps23.Benchmark")

  private val sizes       = Seq(100000, 300000, 500000)
  private val parallelism = 2

  test("benchmark: sumOfSquares (sequential)", BenchmarkTag) {
    sizes.foreach { size =>
      val values  = (0 until size).toVector
      val start   = System.nanoTime()
      val result  = ParCollectionBenchFunc.sumOfSquares(values)
      val elapsed = (System.nanoTime() - start) / 1000000.0
      println(f"Sequential (size=$size%7d): $elapsed%8.2f ms, result=$result")
      assert(result > 0)
    }
  }

  test("benchmark: sumOfSquaresPar (default parallel)", BenchmarkTag) {
    sizes.foreach { size =>
      val values  = (0 until size).toVector
      val start   = System.nanoTime()
      val result  = ParCollectionBenchFunc.sumOfSquaresPar(values)
      val elapsed = (System.nanoTime() - start) / 1000000.0
      println(f"Parallel default (size=$size%7d): $elapsed%8.2f ms, result=$result")
      assert(result > 0)
    }
  }

  test("benchmark: sumOfSquaresParFixed (fixed thread pool)", BenchmarkTag) {
    sizes.foreach { size =>
      val values  = (0 until size).toVector
      val start   = System.nanoTime()
      val result  = ParCollectionBenchFunc.sumOfSquaresParFixed(values, fixedNumberOfThreads = parallelism)
      val elapsed = (System.nanoTime() - start) / 1000000.0
      println(f"Parallel fixed (size=$size%7d, threads=$parallelism): $elapsed%8.2f ms, result=$result")
      assert(result > 0)
    }
  }

  test("benchmark: sumOfSquaresParFork (work-stealing pool)", BenchmarkTag) {
    sizes.foreach { size =>
      val values  = (0 until size).toVector
      val start   = System.nanoTime()
      val result  = ParCollectionBenchFunc.sumOfSquaresParFork(values, parallelism = parallelism)
      val elapsed = (System.nanoTime() - start) / 1000000.0
      println(f"Parallel fork (size=$size%7d, parallelism=$parallelism): $elapsed%8.2f ms, result=$result")
      assert(result > 0)
    }
  }

  test("benchmark: compare all strategies") {
    val size   = 500000
    val values = (0 until size).toVector

    println(s"\n=== Benchmark Comparison (size=$size) ===")

    val seqStart  = System.nanoTime()
    val seqResult = ParCollectionBenchFunc.sumOfSquares(values)
    val seqTime   = (System.nanoTime() - seqStart) / 1000000.0
    println(f"Sequential:        $seqTime%8.2f ms")

    val parStart  = System.nanoTime()
    val parResult = ParCollectionBenchFunc.sumOfSquaresPar(values)
    val parTime   = (System.nanoTime() - parStart) / 1000000.0
    println(f"Parallel default:  $parTime%8.2f ms (speedup: ${seqTime / parTime}%.2fx)")

    val fixedStart  = System.nanoTime()
    val fixedResult = ParCollectionBenchFunc.sumOfSquaresParFixed(values, parallelism)
    val fixedTime   = (System.nanoTime() - fixedStart) / 1000000.0
    println(f"Parallel fixed:    $fixedTime%8.2f ms (speedup: ${seqTime / fixedTime}%.2fx)")

    val forkStart  = System.nanoTime()
    val forkResult = ParCollectionBenchFunc.sumOfSquaresParFork(values, parallelism)
    val forkTime   = (System.nanoTime() - forkStart) / 1000000.0
    println(f"Parallel fork:     $forkTime%8.2f ms (speedup: ${seqTime / forkTime}%.2fx)")

    // Verify all produce the same result
    assert(seqResult == parResult)
    assert(seqResult == fixedResult)
    assert(seqResult == forkResult)
  }
}
