package io.github.sps23.parcollection

import org.scalatest.funsuite.AnyFunSuiteLike

class ParCollectionTest extends AnyFunSuiteLike {

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

  private val limitedParallelThreads = 5
  private val parallelism            = 4

  test("applyParF processes all countries in parallel (default threads)") {
    val result = ParCollection.applyParF(europeanCountries, toUpper)
    assert(result.size === europeanCountries.size)
  }

  test("applyParF with fixed parallelism processes all countries") {

    val result = ParCollection.applyParForkF(europeanCountries, toUpper, parallelism)
    assert(result.size === europeanCountries.size)
  }

  test("applyParFixedF processes all countries with limited threads") {
    val result = ParCollection.applyParFixedF(europeanCountries, toUpper, limitedParallelThreads)
    assert(result.size === europeanCountries.size)
  }

  test("applyF processes all countries sequentially") {
    val result = ParCollection.applyF(europeanCountries, toUpper)
    assert(result.size === europeanCountries.size)
  }
}
