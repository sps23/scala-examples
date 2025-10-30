package io.github.sps23.zstream

import zio.http._
import zio.http.endpoint._
import zio.http.endpoint.openapi.OpenAPIGen

object HealthCheckRoute {

  // Define the healthcheck endpoint with OpenAPI documentation
  val endpoint =
    Endpoint(Method.GET / "healthcheck")
      .out[String]

  val openApi =
    OpenAPIGen.fromEndpoints(endpoint)

  // Define the route handler
  val route: Route[Any, Nothing] =
    endpoint.implementHandler(Handler.succeed("OK"))
}
