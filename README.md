# Scala Examples

A multi-module Scala project demonstrating various frameworks and libraries, with a focus on parallel collections and ZIO-based applications.

## Project Structure

```
scala-examples/
├── pure-scala-examples/      # Pure Scala examples (parallel collections, benchmarks)
├── zio-examples/             # ZIO framework examples (HTTP server, streams)
└── src/site/                 # GitHub Pages documentation
```

## Modules

### pure-scala-examples

Demonstrates Scala's parallel collections with comprehensive benchmarking using ScalaMeter.

**Key Features:**
- Sequential vs. parallel collection processing
- Custom thread pool configurations (fixed and work-stealing)
- **ScalaMeter benchmarks with aggregated result summaries**
- Performance comparison with speedup calculations

**Run benchmarks:**
```bash
sbt "pureScalaExamples/testOnly io.github.sps23.parcollection.ParCollectionScalaMeterBench"
```

**Run tests:**
```bash
sbt "pureScalaExamples/test"
```

See [SCALAMETER_BENCHMARK.md](SCALAMETER_BENCHMARK.md) for detailed benchmark documentation.

### zio-examples

Demonstrates ZIO 2.x with HTTP server, streaming, and structured logging.

**Key Features:**
- ZIO HTTP 3.x server with OpenAPI/Swagger documentation
- ZIO Streams with backpressure
- Structured logging with SLF4J backend
- Graceful shutdown handling

**Run the server:**
```bash
sbt "zioExamples/run"
```

Server endpoints:
- `GET /healthcheck` - Health check endpoint
- `GET /docs/openapi` - SwaggerUI documentation

## Technology Stack

- **Scala**: 2.13.17
- **Build Tool**: sbt 1.11.7
- **ZIO**: 2.1.22
- **ZIO HTTP**: 3.5.1
- **ScalaMeter**: 0.21
- **ScalaTest**: 3.2.19

## Quick Start

### Prerequisites
- JDK 11 or higher
- sbt 1.11.7

### Build and Compile
```bash
# Compile all modules
sbt compile

# Compile specific module
sbt "pureScalaExamples/compile"
sbt "zioExamples/compile"
```

### Run Tests
```bash
# Run all tests
sbt test

# Run tests for specific module
sbt "pureScalaExamples/test"
sbt "zioExamples/test"
```

### Generate Documentation
```bash
# Generate unified Scaladoc
sbt unidoc

# Generate complete site
sbt makeSite

# Publish to GitHub Pages
sbt ghpagesPushSite
```

## ScalaMeter Benchmark Features

The `ParCollectionScalaMeterBench` provides:

- **Automated warmup runs**: 2 warmup iterations before measurement
- **Statistical analysis**: 10 benchmark runs with confidence intervals
- **Multiple input sizes**: Tests from 100K to 500K elements
- **Custom reporters**: Aggregates results into a comparative summary table
- **Speedup calculations**: Automatic comparison against sequential baseline
- **HTML reports**: Visual performance charts

### Example Aggregated Summary

```
========================================================================================================================
AGGREGATED BENCHMARK SUMMARY
========================================================================================================================
Method                                        |     Size 100000 |     Size 200000 |     Size 300000 |     Size 400000 |     Size 500000 |
------------------------------------------------------------------------------------------------------------------------
sumOfSquares (sequential)                     |     6.51 ms     |     3.67 ms     |     7.09 ms     |     9.36 ms     |     6.05 ms     |
sumOfSquaresPar (default parallel)            |     1.53 ms (4.26x) |     2.12 ms (1.73x) |     3.07 ms (2.31x) |     4.23 ms (2.21x) |     6.29 ms (0.96x) |
sumOfSquaresParFixed (max parallelism)        |     1.44 ms (4.52x) |     2.23 ms (1.65x) |     3.43 ms (2.07x) |     5.67 ms (1.65x) |     9.81 ms (0.62x) |
sumOfSquaresParFork (max parallelism)         |     1.43 ms (4.55x) |     2.33 ms (1.58x) |     3.33 ms (2.13x) |     5.49 ms (1.70x) |     8.69 ms (0.70x) |
------------------------------------------------------------------------------------------------------------------------

AVERAGE SPEEDUP SUMMARY:
------------------------------------------------------------
sumOfSquaresPar (default parallel)            : 2.29x
sumOfSquaresParFixed (max parallelism)        : 2.10x
sumOfSquaresParFork (max parallelism)         : 2.13x
========================================================================================================================
```

## Documentation

- **API Documentation**: [https://sylwesterstocki.github.io/scala-examples/api/](https://sylwesterstocki.github.io/scala-examples/api/)
- **GitHub Pages**: [https://sylwesterstocki.github.io/scala-examples/](https://sylwesterstocki.github.io/scala-examples/)
- **ScalaMeter Benchmark Details**: [SCALAMETER_BENCHMARK.md](SCALAMETER_BENCHMARK.md)
- **GitHub Pages Setup**: [GITHUB_PAGES.md](GITHUB_PAGES.md)

## Code Quality

- **Automatic formatting**: scalafmt runs on compile
- **Format check**: Pre-compile formatting validation
- **Scaladoc**: Comprehensive API documentation for all public interfaces

Format code manually:
```bash
sbt scalafmt
sbt Test/scalafmt
```

## Contributing

This project follows functional programming best practices:
- Pure functions and immutability
- Type-safe APIs
- Comprehensive error handling
- Proper resource management with automatic cleanup

## License

See LICENSE file for details.

## Author

Sylwester Stocki - [GitHub Profile](https://github.com/sylwesterstocki)

