package io.github.sps23.handler

import zio.http.{Header, Request}

trait AuthProvider {

  def provideAuthContext(request: Request): AuthContext

  def provideAuthContext(header: Header.Authorization): Either[String, AuthContext]
}

trait AuthProviderImpl extends AuthProvider {

  override def provideAuthContext(request: Request): AuthContext =
    // For demonstration, we return a fixed AuthContext.
    // In a real implementation, you would extract authentication information from the request.
    AuthContext(username = "testUser", roles = List("user", "admin"))

  override def provideAuthContext(header: Header.Authorization): Either[String, AuthContext] =
    header match {
      case Header.Authorization.Basic(user, password) =>
        // For demonstration, we assume any Basic auth header is valid and return a fixed AuthContext.
        if (user == "user" && password.stringValue == "pass") {
          Right(AuthContext(username = user, roles = List("user")))
        } else {
          Left("Invalid username or password")
        }
//      case Header.Authorization.Bearer(token) =>
//        Right(AuthContext(username = "testUser", roles = List("user", "admin")))
      case _ =>
        Left("Unsupported authorization method")
    }

}

object AuthProviderImpl extends AuthProviderImpl
