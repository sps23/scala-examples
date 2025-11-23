package io.github.sps23.parcollection

import org.scalameter.api._
import org.scalameter.picklers.Implicits._

/**
 * ScalaMeter benchmark for parallel collection operations.
 *
 * This benchmark compares the performance of sequential and parallel implementations of sum of squares computation
 * across different input sizes.
 *
 * Run with: sbt "pureScalaExamples/testOnly io.github.sps23.parcollection.ParCollectionScalaMeterBench"
 */
object ParCollectionScalaMeterBench extends Bench.LocalTime {

  private val defaultParallelism = math.max(1, Runtime.getRuntime.availableProcessors())

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
    HtmlReporter(embedDsv = true),
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
    exec.maxWarmupRuns      := 2,
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
}
