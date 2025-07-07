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
import org.typelevel.log4cats.Logger
import skunk.Session
import dev.profunktor.redis4cats.RedisCommands

import cc.timeli.core.logging.syntax.*
import cc.timeli.middleware.{AuthMP, AuthContext}
import cc.timeli.core.validation.authValidators.given
import cc.timeli.core.validation.syntax.*
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.algebra.auth.authDtos.{LoginDto, SignupDto, LogoutDto, PasswordForgotDto, PasswordResetDto}
import cc.timeli.algebra.auth.AuthAlgebra
import cc.timeli.core.responses.responses.FailureRes
import cc.timeli.core.utils.JwtUtils

class AuthRoutes[F[_]: Concurrent: LoggerFactory](
    authMP: AuthMP[F],
    authAlgebra: AuthAlgebra[F],
) extends HttpValidationDsl[F] {
  given Logger[F] = LoggerFactory[F].getLogger()

  private val loginRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "login" =>
      req.validate[LoginDto](loginDto =>
        authAlgebra
          .login(loginDto)
          .value
          .flatMap({
            case Right(loginData) => {
              Ok(loginData.permissionsData).map(
                _.addCookie(loginData.accessTokenCookie)
                  .addCookie(loginData.refreshTokenCookie),
              )
            }
            case Left(error: InvalidCredentialsError) =>
              Forbidden(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
            case Left(error) =>
              BadRequest(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
          }),
      )
  })

  private val mLoginRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "mlogin" =>
      req.validate[LoginDto](loginDto =>
        authAlgebra
          .mLogin(loginDto)
          .value
          .flatMap({
            case Right(loginData) => {
              Ok(loginData.permissionsData).map(
                _.addCookie(loginData.accessTokenCookie)
                  .addCookie(loginData.refreshTokenCookie),
              )
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

  private val logoutRoute: AuthedRoutes[AuthContext, F] = AuthedRoutes.of[AuthContext, F]({
    case req @ POST -> Root / "logout" as authContext =>
      authAlgebra
        .logout(LogoutDto(authContext.userId))
        .value
        .flatMap({
          case Right(logoutData) =>
            Ok().map(_.addCookie(logoutData.accessTokenCookieEmpty).addCookie(logoutData.refreshTokenCookieEmpty))
          case Left(error) =>
            BadRequest(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
        })
  })

  private val passwordForgotRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "passwordForgot" =>
      req.validate[PasswordForgotDto](passwordForgotDto =>
        authAlgebra
          .passwordForgot(passwordForgotDto)
          .value
          .flatTap({
            case Right(_)    => ().pure
            case Left(error) => logger.info(error.getClass().getSimpleName().replace("$", "") + " : " + error.message)
          })
          .flatMap({
            case Right(_) => Ok()
            case Left(error: RateLimitedError) =>
              BadRequest(FailureRes(error.getClass().getSimpleName().replace("$", ""), error.message, List()))
            case Left(_) => Ok()
          }),
      )
  })

  private val passwordResetRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "passwordReset" =>
      req.validate[PasswordResetDto](passwordResetDto => {
        authAlgebra
          .passwordReset(passwordResetDto)
          .value
          .flatMap({
            case Right(_) => Ok()
            case Left(error) =>
              BadRequest(FailureRes(error.getClass().getSimpleName.replace("$", ""), error.message, List()))
          })
      })
  })

  val routes: HttpRoutes[F] = Router(
    "auth" -> (loginRoute <+> mLoginRoute <+> signupRoute <+> passwordForgotRoute <+> passwordResetRoute <+> authMP
      .middleware(
        logoutRoute,
      )),
  )
}

object AuthRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](
      authMP: AuthMP[F],
      authAlgebra: AuthAlgebra[F],
  ) =
    new AuthRoutes(authMP, authAlgebra)
}
