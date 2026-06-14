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

## Copilot Skill Playbook (ZIO)

Use this prompt template when asking Copilot for ZIO or ZIO HTTP changes so responses are grounded in the installed skills and current docs.

```text
Use the `zio-knowledge` and `zio-http-knowledge` skills before writing code.

Project constraints:
- Scala 2.13.17
- sbt 1.11.7
- Keep changes scoped to the requested module (for ZIO work: `zio-examples`)

Task:
<describe what to build/change>

Requirements:
1) Consult the skill workflow and fetch the relevant ZIO/ZIO HTTP docs pages first.
2) Generate code that matches this repository's structure and coding style.
3) Cite the documentation URLs used for API/type decisions.
4) Explain any version-sensitive API choices.
5) Add/update tests when behavior changes.
6) Run compile/tests for touched modules and report results.
```

Use this follow-up prompt as a quality gate after code generation:

```text
Perform a strict self-review of your solution:
- identify behavioral risks/regressions
- verify ZIO/ZIO HTTP imports and type signatures
- verify ZIO idioms (error channel, interruption, scope/resource safety, logging)
- propose minimal fixes and apply them
- re-run compile/tests for touched modules
```

Practical tips:
- Ask for `Scaladoc` on new public APIs.
- Ask for graceful shutdown and interruption handling when adding long-running effects.
- Ask for explicit test updates (route tests, stream tests, integration tests) instead of code-only changes.

### Reusable prompt files

Prompt templates are stored in `.github/prompts/` so you can reuse them without rewriting instructions each time.

- Start with `.github/prompts/zio-default.md`.
- Use specialized templates for endpoint, stream, ZLayer, and test work.
- See `.github/prompts/README.md` for usage details.
- Use `PROMPTS.md` for quick copy-ready shortcuts.

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
