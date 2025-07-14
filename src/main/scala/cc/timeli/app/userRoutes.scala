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
import cc.timeli.middleware.{AuthMP, AuthContext}
import cc.timeli.algebra.user.UserAlgebra
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.algebra.user.userDtos.*
import cc.timeli.core.responses.responses.FailureRes
import cc.timeli.core.guard.syntax.*
import cc.timeli.core.shared.Permission

class UserRoutes[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F], userAlgebra: UserAlgebra[F])
    extends HttpValidationDsl[F]
    with HttpGuardDsl[F] {

  private val meRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F] {
    case req @ GET -> Root / "me" as authContext => {
      userAlgebra
        .me(MeDto(authContext.userId))
        .value
        .flatMap({
          case Right(userInfoData) => Ok(userInfoData)
          case Left(error: NotFoundError) =>
            NotFound(FailureRes(error.name, error.message, List()))
          case Left(error) =>
            BadRequest(FailureRes(error.name, error.message, List()))
        })
    }
  }

  private val tableRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F] {
    case req @ GET -> Root / "table" as authContext => {
      authContext.guard(Permission.READ_USER_TABLE) {
        Ok()
      }
    }
  }

  val routes: HttpRoutes[F] = Router(
    "user" -> (authMP.middleware(meRoute <+> tableRoute)),
  )
}

object UserRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](authMP: AuthMP[F], userAlgebra: UserAlgebra[F]) =
    new UserRoutes(authMP, userAlgebra)
}
