package io.github.sps23.parcollection

import org.scalameter.CurveData
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.scalameter.utils.Tree

import scala.collection.mutable

/**
 * ScalaMeter benchmark for parallel collection operations with aggregated summary.
 *
 * This benchmark compares the performance of sequential and parallel implementations of sum of squares computation
 * across different input sizes. Results are aggregated and displayed in a comparative summary table at the end.
 *
 * Run with: sbt "pureScalaExamples/testOnly io.github.sps23.parcollection.ParCollectionScalaMeterBench"
 */
object ParCollectionScalaMeterBench extends Bench.LocalTime {

  private val defaultParallelism = math.max(1, Runtime.getRuntime.availableProcessors())

  // Store benchmark results for aggregation
  private val results = mutable.Map[String, mutable.Map[Int, Double]]()

  /**
   * Custom reporter that collects results and displays an aggregated summary.
   */
  class AggregatingReporter extends Reporter[Double] {
    def report(result: CurveData[Double], persistor: Persistor): Unit = {
      // Extract method name and measurements
      val methodName = result.context.scope

      result.measurements.foreach { measurement =>
        val size = measurement.params("size").asInstanceOf[Int]
        val mean = measurement.value

        results.getOrElseUpdate(methodName, mutable.Map[Int, Double]()).put(size, mean)
      }
    }

    def report(results: Tree[CurveData[Double]], persistor: Persistor): Boolean = {
      results.foreach(cd => report(cd, persistor))
      true
    }
  }

  /**
   * Summary reporter that prints aggregated results in a table format.
   */
  class SummaryReporter extends Reporter[Double] {
    def report(result: CurveData[Double], persistor: Persistor): Unit = {}

    def report(results: Tree[CurveData[Double]], persistor: Persistor): Boolean = {
      printAggregatedSummary()
      true
    }
  }

  // Configuration with 2 warmup runs
  override def measurer: Measurer[Double] = new Measurer.Default

  override def executor: Executor[Double] = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.min[Double],
    measurer,
  )

  override def persistor: Persistor = Persistor.None

  override def reporter: Reporter[Double] = Reporter.Composite(
    new RegressionReporter(
      RegressionReporter.Tester.Accepter(),
      RegressionReporter.Historian.Window(1),
    ),
    new AggregatingReporter(),
    HtmlReporter(embedDsv = true),
    new SummaryReporter(),
  )

  // Generator for input sizes
  val sizes: Gen[Int] = Gen.range("size")(from = 100000, upto = 500000, hop = 100000)

  // Generator for input ranges
  val ranges: Gen[Vector[Int]] = for {
    size <- sizes
  } yield (0 until size).toVector

  // Custom configuration with warmups
  override def defaultConfig: Context = Context(
    exec.minWarmupRuns      := 2,
    exec.maxWarmupRuns      := 3,
    exec.benchRuns          := 10,
    exec.independentSamples := 1,
  )

  performance of "ParCollectionBenchFunc" in {

    measure method "sumOfSquares (sequential)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquares(r)
      }
    }

    measure method "sumOfSquaresPar (default parallel)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquaresPar(r)
      }
    }

    measure method "sumOfSquaresParFixed (fixed thread pool)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquaresParFixed(r, fixedNumberOfThreads = 2)
      }
    }

    measure method "sumOfSquaresParFork (work-stealing pool)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquaresParFork(r, parallelism = 2)
      }
    }

    measure method "sumOfSquaresParFixed (max parallelism)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquaresParFixed(r, fixedNumberOfThreads = defaultParallelism)
      }
    }

    measure method "sumOfSquaresParFork (max parallelism)" in {
      using(ranges).config(defaultConfig) in { r =>
        ParCollectionBenchFunc.sumOfSquaresParFork(r, parallelism = defaultParallelism)
      }
    }
  }

  /**
   * Prints an aggregated summary table comparing all benchmark results.
   */
  private def printAggregatedSummary(): Unit = {
    println("\n" + "=" * 120)
    println("AGGREGATED BENCHMARK SUMMARY")
    println("=" * 120)

    if (results.isEmpty) {
      println("No results collected.")
      return
    }

    // Get all sizes and sort them
    val allSizes = results.values.flatMap(_.keys).toSet.toSeq.sorted

    // Get baseline (sequential) results for speedup calculation
    val baselineKey     = results.keys.find(_.contains("sequential"))
    val baselineResults = baselineKey.map(results(_)).getOrElse(mutable.Map[Int, Double]())

    // Print header
    print(f"${"Method"}%-45s")
    allSizes.foreach(size => print(f" | ${s"Size $size"}%15s"))
    println(" |")
    println("-" * 120)

    // Print results for each method
    results.toSeq.sortBy(_._1).foreach { case (methodName, sizeResults) =>
      val shortName = methodName
        .replace("ParCollectionBenchFunc.", "")
        .replace("Test-0.", "")
        .replace("Test-1.", "")
        .replace("Test-2.", "")
        .replace("Test-3.", "")
        .replace("Test-4.", "")
        .replace("Test-5.", "")

      print(f"$shortName%-45s")

      allSizes.foreach { size =>
        sizeResults.get(size) match {
          case Some(time) =>
            val speedup = baselineResults.get(size).map(_ / time).getOrElse(1.0)
            if (methodName.contains("sequential")) {
              print(f" | $time%8.2f ms      ")
            } else {
              print(f" | $time%8.2f ms ($speedup%.2fx)")
            }
          case None =>
            print(f" | ${"N/A"}%15s")
        }
      }
      println(" |")
    }

    println("-" * 120)

    // Print average speedups
    println("\nAVERAGE SPEEDUP SUMMARY:")
    println("-" * 60)

    results.toSeq.sortBy(_._1).filterNot(_._1.contains("sequential")).foreach { case (methodName, sizeResults) =>
      val speedups = allSizes.flatMap { size =>
        for {
          baseline <- baselineResults.get(size)
          time     <- sizeResults.get(size)
        } yield baseline / time
      }

      if (speedups.nonEmpty) {
        val avgSpeedup = speedups.sum / speedups.size
        val shortName = methodName
          .replace("ParCollectionBenchFunc.", "")
          .replace("Test-0.", "")
          .replace("Test-1.", "")
          .replace("Test-2.", "")
          .replace("Test-3.", "")
          .replace("Test-4.", "")
          .replace("Test-5.", "")

        println(f"$shortName%-45s: $avgSpeedup%.2fx")
      }
    }

    println("=" * 120)
    println()
  }
}
