package cc.timeli.app

import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*

import cats.effect.Concurrent
import cats.implicits.*
import org.typelevel.log4cats.LoggerFactory
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.server.Router

import cc.timeli.core.validation.syntax.*
import cc.timeli.core.guard.syntax.*
import cc.timeli.middleware.{AuthMP, AuthContext}

class RoomRoutes[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F]) extends HttpValidationDsl[F] with HttpGuardDsl[F] {

  private val roomGridRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F] {
    case req @ POST -> Root / "roomGrid" as authContext => {
      Ok("roomGrid")
    }
  }

  val routes: HttpRoutes[F] = Router(
    "room" -> (authMP.middleware(roomGridRoute)),
  )

}
