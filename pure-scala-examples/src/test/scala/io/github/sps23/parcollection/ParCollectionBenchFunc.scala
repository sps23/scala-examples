package io.github.sps23.parcollection

/**
 * Benchmark functions for comparing sequential and parallel collection processing.
 *
 * This object provides several implementations of sum of squares computation:
 *   - Sequential processing using standard Scala collections
 *   - Parallel processing using Scala's default parallel collections
 *   - Parallel processing with custom fixed thread pools
 *   - Parallel processing with work-stealing pools
 *
 * These functions are designed to be benchmarked using ScalaMeter.
 *
 * @see
 *   [[ParCollectionScalaMeterBench]] for the ScalaMeter benchmark suite
 * @see
 *   [[ParCollection]] for the underlying parallel collection utilities
 */
object ParCollectionBenchFunc {

  private val defaultParallelism = math.max(1, Runtime.getRuntime.availableProcessors())

  private def square(value: Int): Long = value.toLong * value.toLong

  /**
   * Computes the sum of squares sequentially.
   *
   * @param values
   *   the input sequence of integers
   * @return
   *   the sum of squares of all values
   */
  def sumOfSquares(values: Seq[Int]): Long =
    ParCollection.applyF(values, square).sum

  /**
   * Computes the sum of squares using Scala's default parallel collections pool.
   *
   * Uses the global ForkJoinPool for parallelism.
   *
   * @param values
   *   the input sequence of integers
   * @return
   *   the sum of squares of all values
   */
  def sumOfSquaresPar(values: Seq[Int]): Long =
    ParCollection.applyParF(values, square).sum

  /**
   * Computes the sum of squares using a work-stealing pool with configurable parallelism.
   *
   * @param values
   *   the input sequence of integers
   * @param parallelism
   *   the desired parallelism level (default: number of processors)
   * @return
   *   the sum of squares of all values
   */
  def sumOfSquaresParFork(values: Seq[Int], parallelism: Int = defaultParallelism): Long =
    ParCollection.applyParForkF(values, square, parallelism).sum

  /**
   * Computes the sum of squares using a fixed thread pool.
   *
   * @param values
   *   the input sequence of integers
   * @param fixedNumberOfThreads
   *   the fixed number of threads in the pool (default: number of processors)
   * @return
   *   the sum of squares of all values
   */
  def sumOfSquaresParFixed(values: Seq[Int], fixedNumberOfThreads: Int = defaultParallelism): Long =
    ParCollection.applyParFixedF(values, square, fixedNumberOfThreads).sum
}

// Legacy ScalaMeter-based benchmark kept for reference only.
// object ParCollectionBench extends Bench.Group {

//   implicit val doubleNumeric: Numeric[Double] = implicitly[Numeric[Double]]

//   val sizes: Gen[Int] = Gen.range("size")(100000, 500000, 100000)

//   val ranges: Gen[Vector[Int]] = for {
//     size <- sizes
//   } yield (0 until size).toVector

//   override def persistor: Persistor.None.type = Persistor.None

//   override def reporter = Reporter.Composite(
//     new RegressionReporter(
//       RegressionReporter.Tester.Accepter(),
//       RegressionReporter.Historian.Window(1),
//     ),
//     HtmlReporter(embedDsv = true),
//   )

//   include(new Bench.LocalTime {
//     performance of "sumOfSquares" in {
//       using(ranges) in {
//         r => ParCollectionBenchFunc.sumOfSquares(r)
//       }
//     }
//   })

//   include(new Bench.LocalTime {
//     performance of "sumOfSquaresPar" in {
//       using(ranges) in {
//         r => ParCollectionBenchFunc.sumOfSquaresPar(r)
//       }
//     }
//   })

//   include(new Bench.LocalTime {
//     performance of "sumOfSquaresParFixed" in {
//       using(ranges) in {
//         r => ParCollectionBenchFunc.sumOfSquaresParFixed(r, fixedNumberOfThreads = 2)
//       }
//     }
//   })

//   include(new Bench.LocalTime {
//     performance of "sumOfSquaresParFork" in {
//       using(ranges) in {
//         r => ParCollectionBenchFunc.sumOfSquaresParFork(r, parallelism = 2)
//       }
//     }
//   })
// }
