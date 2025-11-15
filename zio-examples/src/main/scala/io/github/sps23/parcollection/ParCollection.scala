package io.github.sps23.parcollection

import java.util.concurrent.Executors
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ExecutionContextTaskSupport

object ParCollection extends App {

  // List of all European countries
  val europeanCountries: List[String] = List(
    "Albania",
    "Andorra",
    "Armenia",
    "Austria",
    "Azerbaijan",
    "Belarus",
    "Belgium",
    "Bosnia and Herzegovina",
    "Bulgaria",
    "Croatia",
    "Cyprus",
    "Czech Republic",
    "Denmark",
    "Estonia",
    "Finland",
    "France",
    "Georgia",
    "Germany",
    "Greece",
    "Hungary",
    "Iceland",
    "Ireland",
    "Italy",
    "Kazakhstan",
    "Kosovo",
    "Latvia",
    "Liechtenstein",
    "Lithuania",
    "Luxembourg",
    "Malta",
    "Moldova",
    "Monaco",
    "Montenegro",
    "Netherlands",
    "North Macedonia",
    "Norway",
    "Poland",
    "Portugal",
    "Romania",
    "Russia",
    "San Marino",
    "Serbia",
    "Slovakia",
    "Slovenia",
    "Spain",
    "Sweden",
    "Switzerland",
    "Turkey",
    "Ukraine",
    "United Kingdom",
    "Vatican City",
  )

  private def toUpper(country: String): String = {
    // Simulate a computationally intensive task
    val upper = country.toUpperCase
    println(s"[${Thread.currentThread.getName}] $country -> $upper")
    // simulate work so parallelism is observable
    Thread.sleep(50)
    upper
  }

  def applyF[In,Out](list: List[In], f: In => Out): Seq[Out] =
    list.map(f)

  def applyParF[In,Out](list: List[In], f: In => Out): Seq[Out] =
    list.par.map(f).seq

  def applyParF[In,Out](list: List[In], f: In => Out, maxThreads: Int): Seq[Out] = {
    val fixedThreadPool = Executors.newFixedThreadPool(maxThreads)
    val ec              = scala.concurrent.ExecutionContext.fromExecutor(fixedThreadPool)
    val parCollection   = list.par
    parCollection.tasksupport = new ExecutionContextTaskSupport(ec)
    parCollection.map(f).seq
  }

  // Sequential processing
  println("Sequential Result:")
  val sequentialResult = applyF(europeanCountries, toUpper)

  // Parallel processing (Scala 2.13 parallel collections)
  println("\nParallel Result:")
  val parallelResult = applyF(europeanCountries, toUpper)

  println("\nParallel Result with Limited Threads:")
  val parallelResultWithLimitedThreads = applyParF(europeanCountries, toUpper, 5)
  //
}
