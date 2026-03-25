package io.github.sps23.handler

import zio._
import zio.http._

object AuthMiddleware {

  val auth: HandlerAspect[AuthProvider, Unit] =
    Middleware.customAuthZIO[AuthProvider] { request =>
      request.header(Header.Authorization).collect { case auth: Header.Authorization => auth } match {
        case None => ZIO.fail(Response.status(Status.Unauthorized))
        case Some(authorization) =>
          ZIO
            .attempt(ZIO.serviceWith[AuthProvider](_.provideAuthContext(authorization)))
            .flatten
            .catchAll(_ => ZIO.fail(Response.status(Status.InternalServerError)))
            .flatMap {
              case Right(_) => ZIO.succeed(true)
              case Left(_)  => ZIO.fail(Response.status(Status.Unauthorized))
            }
      }
    }

  /**
   * Creates an authentication aspect that derives [[AuthContext]] from the incoming [[Request]] using
   * [[AuthProvider.provideAuthContext(Request)]], then injects it as handler context.
   *
   * After applying this aspect (`routes @@ withAuthContext`), handlers can access auth data via `withContext { (auth:
   * AuthContext) => ... }`.
   *
   * Environment requirement:
   *   - `AuthProvider`
   *
   * Context produced:
   *   - `AuthContext`
   *
   * Notes:
   *   - This aspect always maps provider output to `Some(authContext)`, so it does not implement an explicit
   *     unauthorized branch by itself.
   *   - If strict 401/500 behavior is required, use `withRequiredAuthContext` or `withRequiredHeaderAuthContext`.
   */
  val withAuthContext: HandlerAspect[AuthProvider, AuthContext] =
    HandlerAspect.customAuthProvidingZIO[AuthProvider, AuthContext] { request =>
      ZIO.serviceWith[AuthProvider](_.provideAuthContext(request)).map(Some(_))
    }

  /**
   * Strict variant of auth middleware:
   *   - stores AuthContext in RequestStore when available
   *   - fails request with 401 when AuthContext is missing
   *   - fails request with 500 when AuthContext retrieval throws
   */
  val withAuthContextStore: HandlerAspect[AuthProvider, Unit] =
    HandlerAspect.interceptIncomingHandler(
      Handler.fromFunctionZIO[Request] { request =>
        ZIO
          .attempt(ZIO.serviceWith[AuthProvider](_.provideAuthContext(request)))
          .flatten
          .catchAll(_ => ZIO.fail(Response.status(Status.InternalServerError)))
          .flatMap {
            case null        => ZIO.fail(Response.status(Status.Unauthorized))
            case authContext => RequestStore.set[AuthContext](authContext).as((request, ()))
          }
      }
    )

  /**
   * Header-based strict auth middleware:
   *   - reads Authorization header from request
   *   - resolves AuthContext via AuthProvider.provideAuthContext(header)
   *   - stores AuthContext in RequestStore on success
   *   - fails with 401 when header is missing or provider returns Left
   *   - fails with 500 when AuthContext could not be obtained due to unexpected failure
   */
  val withHeaderAuthContextStore: HandlerAspect[AuthProvider, Unit] =
    HandlerAspect.interceptIncomingHandler(
      Handler.fromFunctionZIO[Request] { request =>
        request.header(Header.Authorization).collect { case auth: Header.Authorization => auth } match {
          case None => ZIO.fail(Response.status(Status.Unauthorized))
          case Some(authorization) =>
            ZIO
              .attempt(ZIO.serviceWith[AuthProvider](_.provideAuthContext(authorization)))
              .flatten
              .catchAll(_ => ZIO.fail(Response.status(Status.InternalServerError)))
              .flatMap {
                case Right(authContext) => RequestStore.set[AuthContext](authContext).as((request, ()))
                case Left(_)            => ZIO.fail(Response.status(Status.Unauthorized))
              }
        }
      }
    )
}
