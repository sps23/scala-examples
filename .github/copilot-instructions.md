# GitHub Copilot Instructions for scala-examples

This file provides context to GitHub Copilot about the project structure, versions, and conventions.

## Project Overview

This is a multi-module Scala project demonstrating various frameworks and libraries.
The project uses sbt as the build tool and is organized into separate submodules.

## Technology Stack & Versions

### Build Tool
- **sbt**: 1.11.7
- **Scala**: 2.13.17

### Core Dependencies

#### ZIO Ecosystem (zio-examples module)
- **ZIO Core**: 2.1.9
- **ZIO Streams**: 2.1.9
- **ZIO HTTP**: 3.0.1
- **ZIO Logging**: 2.3.1
- **ZIO Logging SLF4J2**: 2.3.1

#### Logging
- **Logback Classic**: 1.5.13

### SBT Plugins
- **sbt-unidoc**: 0.5.0 (com.github.sbt)
- **sbt-ghpages**: 0.8.0 (com.github.sbt)
- **sbt-site**: 1.6.0 (com.github.sbt)

## Project Structure

```
scala-examples/
├── build.sbt                    # Root build configuration
├── project/
│   ├── build.properties         # SBT version
│   └── plugins.sbt              # SBT plugins
├── src/
│   └── site/
│       └── index.html           # GitHub Pages landing page
└── zio-examples/                # ZIO examples submodule
    └── src/
        └── main/
            ├── scala/
            │   └── io/github/sps23/zstream/
            │       ├── HttpServer.scala
            │       ├── HealthCheckRoute.scala
            │       ├── SwaggerRoute.scala
            │       └── NumberStream.scala
            └── resources/
                └── logback.xml
```

## Module: zio-examples

### Package Structure
- **Base package**: `io.github.sps23.zstream`

### Key Components

#### HttpServer.scala
- Main application entry point using `ZIOAppDefault`
- Runs HTTP server and number stream in parallel using `zipPar`
- Port: 8080
- Includes graceful shutdown with logging
- Uses SLF4J logging backend

#### HealthCheckRoute.scala
- Defines a simple healthcheck endpoint
- Path: `GET /healthcheck`
- Returns: "OK" (plain text)

#### SwaggerRoute.scala
- Generates OpenAPI v3 documentation
- Creates SwaggerUI routes
- Path: `/docs/openapi`

#### NumberStream.scala
- ZIO Stream that emits numbers 1-100 repeatedly
- 100ms delay between each number
- Throws exception after 1000 numbers (for demonstration)
- Demonstrates stream processing with backpressure

### Logging Configuration
- Uses Logback with SLF4J2 backend
- Console appender with pattern: `%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
- Root level: INFO
- ZIO HTTP level: INFO
- Application level: DEBUG

## Coding Conventions

### Imports
When working with ZIO HTTP 3.0.1:
```scala
import zio._
import zio.http._
import zio.http.endpoint._
import zio.http.endpoint.openapi._
import zio.stream._
```

### ZIO HTTP Patterns
- Use `Endpoint` for defining API endpoints
- Use `Routes` for combining multiple routes
- Use `Middleware.requestLogging()` for logging
- Use `.implementHandler()` for implementing endpoint handlers
- Use `Server.serve(app).provide(Server.defaultWithPort(port))` for serving

### ZIO Streams
- Use `ZStream.fromIterable().forever` for infinite streams
- Use `.zipWithIndex` for tracking element count
- Use `.tap()` for side effects (logging, etc.)
- Use `ZIO.sleep(duration.millis)` for delays

### Parallel Execution
- Use `.zipPar()` to run multiple ZIO effects in parallel
- Both effects will stop if one fails or is interrupted
- Use `.onInterrupt()` for cleanup on interruption
- Use `.onExit()` for handling both success and failure cases

### Logging
- Use `ZIO.logInfo()`, `ZIO.logDebug()`, etc. for structured logging
- Bootstrap with: `Runtime.removeDefaultLoggers >>> SLF4J.slf4j`

## Build Commands

### Running the application
```bash
sbt "zioExamples/run"
```

### Generating documentation
```bash
sbt unidoc           # Generate unified Scaladoc
sbt makeSite         # Generate complete site
sbt ghpagesPushSite  # Publish to GitHub Pages
```

### Other useful commands
```bash
sbt compile          # Compile all modules
sbt zioExamples/compile  # Compile specific module
sbt clean            # Clean build artifacts
sbt reload           # Reload build configuration
```

## GitHub Pages

- Documentation is generated using sbt-unidoc (aggregates all submodules)
- Custom landing page at `src/site/index.html`
- API docs are placed under `/api/`
- Published to `gh-pages` branch
- URL: https://sylwesterstocki.github.io/scala-examples/

## Best Practices for This Project

1. **Always use the specified versions** - Don't suggest upgrades without checking compatibility
2. **Follow the package structure** - Keep ZIO examples under `io.github.sps23.zstream`
3. **Add Scaladoc comments** - All public APIs should have documentation
4. **Use ZIO idioms** - Prefer ZIO effect types over side effects
5. **Handle errors properly** - Use ZIO error channel, not exceptions (except for demos)
6. **Add logging** - Use ZIO logging for observability
7. **Test shutdown behavior** - Ensure proper cleanup with `.onInterrupt()` and `.onExit()`

## Common Patterns

### Creating a new ZIO HTTP endpoint
```scala
val myEndpoint = Endpoint(Method.GET / "path")
  .out[ResponseType]
  
val myRoute: Route[Any, Nothing] =
  myEndpoint.implementHandler(Handler.succeed(response))
```

### Creating a ZIO Stream with delays
```scala
ZStream.fromIterable(collection)
  .tap(item => ZIO.sleep(delay.millis) *> processItem(item))
  .runDrain
```

### Running effects in parallel
```scala
val effect1 = ZIO.logInfo("Starting 1") *> doWork1
val effect2 = ZIO.logInfo("Starting 2") *> doWork2

effect1.zipPar(effect2)
```

## Notes for Copilot

- When suggesting code, use the exact versions listed above
- Follow ZIO 2.x patterns (not ZIO 1.x)
- Use ZIO HTTP 3.0.1 API (significantly different from 2.x)
- Suggest type-safe, functional solutions
- Prefer immutability and pure functions
- Use ZIO's built-in operators over manual implementations

