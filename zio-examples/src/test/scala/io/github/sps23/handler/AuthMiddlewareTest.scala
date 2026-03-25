package io.github.sps23.handler

import zio.http._
import zio.test.{ZIOSpecDefault, _}
import zio.{Scope, ZIO, ZLayer}

object AuthMiddlewareTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("AuthMiddlewareTest")(
    test("should provide auth context") {
      val app     = routeSimple.provide(AuthProviderImpl)
      val request = Request.get(URL(Path("/context/Mister")))
      for {
        response <- app.run(request)
        body     <- response.body.asString
      } yield assertTrue(
        response.status == Status.Ok &&
          body.contains("Hello, Mister testUser! You have roles: user, admin. You requested title: Mister")
      )
    },
    test("should provide auth context and greeting service") {
      val app     = routeGreeting.provide(GreetingServiceImpl)
      val request = Request.get(URL(Path("/greet/Mister"))).addHeader(Header.Authorization.Basic("user", "pass"))
      for {
        response <- app.run(request)
        body     <- response.body.asString
      } yield assertTrue(
        response.status == Status.Ok &&
          body.contains("Hello, Mister user! You are authenticated as user with roles: user.")
      )
    },
    suite("TestServer integration tests")(
      test("routeGreeting with valid auth header should return 200 with greeting") {
        for {
          server <- ZIO.service[TestServer]
          port   <- server.port
          _      <- server.install(routeGreeting)
          resp <- makeHttpRequest(
            "GET",
            s"http://localhost:$port/greet/Mister",
            Some(Header.Authorization.Basic("user", "pass")),
          )
          body <- resp.body.asString
        } yield assertTrue(
          resp.status == Status.Ok &&
            body.contains("Hello, Mister user!") &&
            body.contains("You are authenticated as user with roles: user.")
        )
      },
      test("routeGreeting without auth header should return 401 Unauthorized") {
        for {
          server <- ZIO.service[TestServer]
          port   <- server.port
          _      <- server.install(routeGreeting)
          resp   <- makeHttpRequest("GET", s"http://localhost:$port/greet/Mister", None)
        } yield assertTrue(resp.status == Status.Unauthorized)
      },
      test("routeGreeting with invalid credentials should return 401 Unauthorized") {
        for {
          server <- ZIO.service[TestServer]
          port   <- server.port
          _      <- server.install(routeGreeting)
          resp <- makeHttpRequest(
            "GET",
            s"http://localhost:$port/greet/Mister",
            Some(Header.Authorization.Basic("user", "wrongpass")),
          )
        } yield assertTrue(resp.status == Status.Unauthorized)
      },
    ).provide(TestServer.default, ZLayer.succeed(GreetingServiceImpl), Client.default, Scope.default),
  )

  private def makeHttpRequest(
      method: String,
      url: String,
      authHeader: Option[Header.Authorization],
  ): ZIO[Client with Scope, Throwable, Response] = {
    val request = method match {
      case "GET" => Request.get(URL.decode(url).toOption.get)
      case _     => Request.get(URL.decode(url).toOption.get)
    }
    val withAuth = authHeader.fold(request)(h => request.addHeader(h))
    Client.batched(withAuth)
  }

  val privateRoute: Routes[AuthProvider, Nothing] =
    Routes(
      Method.GET / "private" -> handler { _: Request =>
        Response.text(s"Hello, you")
      }
    ) @@ AuthMiddleware.auth

  val routeSimple: Routes[AuthProvider, Nothing] =
    Routes(
      Method.GET / "context" / string("title") ->
        handler { (title: String, _: Request) =>
          withContext { (authContext: AuthContext) =>
            Response.text(
              s"Hello, $title ${authContext.username}! You have roles: ${authContext.roles.mkString(", ")}. You requested title: $title"
            )
          }
        }
    ) @@ AuthMiddleware.withAuthContext

  val routeGreeting: Routes[AuthProvider with GreetingService, Nothing] =
    Routes(
      Method.GET / "greet" / string("title") ->
        handler { (title: String, _: Request) =>
          for {
            authContext <- RequestStore.get[AuthContext].someOrFail(new RuntimeException("AuthContext not found")).orDie
            greetingService <- ZIO.service[GreetingService]
            greeting        <- greetingService.greet(authContext.username, title)
          } yield Response.text(
            s"$greeting You are authenticated as ${authContext.username} with roles: ${authContext.roles.mkString(", ")}."
          )
        }
    ) @@ AuthMiddleware.withHeaderAuthContextStore
}
