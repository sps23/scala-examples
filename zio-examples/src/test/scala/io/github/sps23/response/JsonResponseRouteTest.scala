package io.github.sps23.response

import zio.Scope
import zio.http._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object JsonResponseRouteTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("JsonResponseRouteTest")(
      test("returns jsonStringResponse as-is") {
        val request = Request.get(URL(Path("/json-response")))
        for {
          response <- JsonResponseRoute.route.run(request)
          body     <- response.body.asString
          contentType = response.header(Header.ContentType).map(_.mediaType)
        } yield assertTrue(
          response.status == Status.Ok,
          contentType.contains(MediaType.application.`json`),
          body == JsonResponseRoute.jsonStringResponse,
        )
      }
    )
}
