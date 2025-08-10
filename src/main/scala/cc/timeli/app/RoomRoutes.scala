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
import cc.timeli.core.validation.roomValidators.given
import cc.timeli.core.guard.syntax.*
import cc.timeli.middleware.{AuthMP, AuthContext}
import cc.timeli.algebra.room.RoomAlgebra
import cc.timeli.core.shared.Permission
import cc.timeli.algebra.room.roomDtos.RoomGridDto
import cc.timeli.core.responses.responses.FailureRes

class RoomRoutes[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F], roomAlgebra: RoomAlgebra[F])
    extends HttpValidationDsl[F]
    with HttpGuardDsl[F] {

  private val roomGridRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F] {
    case req @ POST -> Root / "roomGrid" as authContext => {
      authContext.guard(Permission.READ_ROOM_GRID) {
        req.req.validate[RoomGridDto](roomGridDto =>
          roomAlgebra
            .roomGrid(roomGridDto)
            .value
            .flatMap({
              case Right(roomGridData) => Ok(roomGridData)
              case Left(error)         => BadRequest(FailureRes(error.name, error.message, List()))
            }),
        )
      }
    }
  }

  val routes: HttpRoutes[F] = Router(
    "room" -> (authMP.middleware(roomGridRoute)),
  )

}
