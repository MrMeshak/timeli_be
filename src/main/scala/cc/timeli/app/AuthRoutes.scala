package cc.timeli.app

import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*

import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import org.typelevel.log4cats.LoggerFactory
import skunk.Session
import dev.profunktor.redis4cats.RedisCommands

import cc.timeli.core.validation.authValidators.given
import cc.timeli.core.validation.syntax.*
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.algebra.auth.authDtos.{LoginDto, SignupDto}
import cc.timeli.algebra.auth.AuthAlgebra
import cc.timeli.core.responses.responses.FailureRes
import cc.timeli.core.utils.JwtUtils

class AuthRoutes[F[_]: Concurrent: LoggerFactory](
    authAlgebra: AuthAlgebra[F],
) extends HttpValidationDsl[F] {

  private val loginRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "login" =>
      req.validate[LoginDto](loginDto =>
        authAlgebra
          .login(loginDto)
          .value
          .flatMap({
            case Right(loginData) => {
              Ok().map(_.addCookie(loginData.accessTokenCookie).addCookie(loginData.refreshTokenCookie))
            }
            case Left(error: InvalidCredentialsError) =>
              Forbidden(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
            case Left(error) =>
              BadRequest(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
          }),
      )
  })

  private val signupRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "signup" =>
      req.validate[SignupDto](signupDto =>
        authAlgebra
          .signup(signupDto)
          .value
          .flatMap({
            case Right(_) => Ok()
            case Left(error: InvalidCredentialsError) =>
              Forbidden(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
            case Left(error) =>
              BadRequest(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
          }),
      )
  })

  val routes: HttpRoutes[F] = Router(
    "auth" -> (loginRoute <+> signupRoute),
  )

}

object AuthRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](
      authAlgebra: AuthAlgebra[F],
  ) =
    new AuthRoutes(authAlgebra)
}
