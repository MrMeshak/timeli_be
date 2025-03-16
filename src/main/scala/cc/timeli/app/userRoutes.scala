package cc.timeli.app

import cats.effect.Concurrent
import cats.implicits.*
import org.typelevel.log4cats.LoggerFactory
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.server.Router

import cc.timeli.core.validation.syntax.*
import cc.timeli.middleware.{AuthMP, AuthContext}

class UserRoutes[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F]) extends HttpValidationDsl[F] {

  private val infoRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F] {
    case req @ GET -> Root / "info" as authContext => {
      Ok("user info route")
    }
  }

  val routes: HttpRoutes[F] = Router(
    "user" -> (authMP.middleware(infoRoute)),
  )
}

object UserRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F]) = new UserRoutes(authMP)
}
