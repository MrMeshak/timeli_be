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

import cc.timeli.core.validation.authValidators.given
import cc.timeli.core.validation.syntax.*
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.algebra.auth.authDtos.{LoginDto, SignupDto}
import cc.timeli.algebra.auth.AuthAlgebra
import cc.timeli.core.responses.responses.FailureRes

class AuthRoutes[F[_]: Concurrent: LoggerFactory](session: Session[F], authAlgebra: AuthAlgebra[F])
    extends HttpValidationDsl[F] {

  private val loginRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "login" =>
      req.validate[LoginDto](loginDto => ???)

  })

  private val signupRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "signup" =>
      req.validate[SignupDto](signupDto =>
        authAlgebra
          .signup(signupDto)
          .value
          .flatMap({
            case Right(_) => Ok()
            case Left(AlreadyExistsError(message)) =>
              BadRequest(FailureRes(AlreadyExistsError.getClass().getName(), message, List()))
          }),
      )
  })

  val routes: HttpRoutes[F] = Router(
    "auth" -> (loginRoute <+> signupRoute),
  )

}

object AuthRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](session: Session[F], authAlgebra: AuthAlgebra[F]) =
    new AuthRoutes(session, authAlgebra)

  // validators

}
