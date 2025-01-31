package cc.timeli.app

import cats.Monad
import cats.implicits.*
import cats.syntax.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import org.typelevel.log4cats.LoggerFactory
import skunk.Session

class AuthRoutes[F[_]: Monad: LoggerFactory](session: Session[F]) extends Http4sDsl[F] {

  private val loginRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "login" => ???
  })

  private val signupRoute: HttpRoutes[F] = HttpRoutes.of[F]({
    case req @ POST -> Root / "signup" => ???
  })

  val routes: HttpRoutes[F] = Router(
    "auth" -> (loginRoute <+> signupRoute),
  )

}

object AuthRoutes {
  def apply[F[_]: Monad: LoggerFactory](session: Session[F]) = new AuthRoutes(session)
}
