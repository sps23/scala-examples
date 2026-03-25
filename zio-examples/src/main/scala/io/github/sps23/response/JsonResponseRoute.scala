package io.github.sps23.response

import zio.ZNothing
import zio.http._
import zio.http.codec.HttpCodec
import zio.http.endpoint.AuthType.None
import zio.http.endpoint._

object JsonResponseRoute {

  val jsonStringResponse: String =
    """{
      |  "name": "John Doe",
      |  "age": 30,
      |  "email": "jdoe@mail.com",
      |  "address": {
      |    "street": "123 Main St",
      |    "city": "Anytown",
      |    "state": "CA",
      |    "zip": "12345"
      |  },
      |  "phoneNumbers": [
      |    {
      |      "type": "home",
      |      "number": "555-1234"
      |    },
      |    {
      |      "type": "work",
      |      "number": "555-5678"
      |    }
      |  ]
      |}""".stripMargin

  // Endpoint with String response body.
  val endpoint: Endpoint[Unit, Unit, ZNothing, (String, Header.ContentType), None] =
    Endpoint(Method.GET / "json-response")
      .out[String](MediaType.text.`plain`)
      .outHeader(HttpCodec.contentType)

  // Route implementation that returns the JSON string as-is.
  val route: Route[Any, Nothing] =
    endpoint.implementHandler(
      Handler.succeed((jsonStringResponse, Header.ContentType(MediaType.application.`json`)))
    )
}
