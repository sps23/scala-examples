package io.github.sps23.zstream

import zio.http.endpoint.openapi._

object SwaggerRoute {

  val openAPI = HealthCheckRoute.openApi
    .title("Sample API")
    .version("1.0.0")

  // Create OpenAPI documentation routes
  val routes = {
    SwaggerUI.routes("docs/openapi", openAPI)
  }
}

