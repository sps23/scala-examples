package io.github.sps23.zstream

import zio._
import zio.http._
import zio.logging.backend.SLF4J

object HttpServer extends ZIOAppDefault {

  // Combine all routes
  private val routes = Routes(HealthCheckRoute.route) ++ SwaggerRoute.routes

  // Logging middleware with enhanced logging
  private val loggingMiddleware =
    Middleware.requestLogging(
      logRequestBody = true,
      logResponseBody = true,
    ) ++ Middleware.debug

  // Apply middleware to routes
  private val app = routes @@ loggingMiddleware

  // Server fiber
  private val serverFiber: ZIO[Any, Throwable, Unit] =
    ZIO.logInfo("Starting HTTP server on port 8080...") *>
      Server.serve(app)
        .provide(Server.defaultWithPort(8080))
        .onInterrupt(ZIO.logInfo("HTTP server stopped!"))
        .onExit {
          case Exit.Success(_)     => ZIO.logInfo("HTTP server completed successfully")
          case Exit.Failure(cause) => ZIO.logInfo(s"HTTP server failed: ${cause.prettyPrint}")
        }

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- ZIO.logInfo("=" * 60)
      _ <- ZIO.logInfo("Starting application...")
      _ <- ZIO.logInfo("Healthcheck endpoint: http://localhost:8080/healthcheck")
      _ <- ZIO.logInfo("Swagger UI: http://localhost:8080/docs/openapi")
      _ <- ZIO.logInfo("=" * 60)
      // Run both the stream and server in parallel, racing them
      // If one fails or is interrupted, both will stop
      _ <- NumberStream.streamFiber.zipPar(serverFiber)
      _ <- ZIO.logInfo("Application shutdown complete")
    } yield ()
}
