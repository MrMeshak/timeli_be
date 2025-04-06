package cc.timeli.app

import cats.effect.{Concurrent}
import cats.implicits.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import dev.profunktor.redis4cats.RedisCommands

import org.typelevel.log4cats.LoggerFactory
import skunk.Session

import cc.timeli.core.utils.JwtUtils
import cc.timeli.app.HealthRoutes
import cc.timeli.app.AuthRoutes
import cc.timeli.algebra.auth.AuthAlgebraLive
import cc.timeli.core.utils.RedisUtils
import cc.timeli.middleware.AuthMP
import cc.timeli.algebra.user.UserAlgebraLive

class AppRoutes[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
    redisUtils: RedisUtils[F],
    jwtUtils: JwtUtils[F],
) {

  private val authMP      = AuthMP(jwtUtils, redisUtils)
  private val authAlgebra = AuthAlgebraLive[F](session, redisUtils, jwtUtils)
  private val userAlgebra = UserAlgebraLive[F](session)

  private val healthRoutes = HealthRoutes[F].routes
  private val authRoutes   = AuthRoutes[F](authMP, authAlgebra).routes
  private val userRoutes   = UserRoutes[F](authMP, userAlgebra).routes

  val routes = Router(
    "api" -> (healthRoutes <+> authRoutes <+> userRoutes),
  )
}

object AppRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](
      session: Session[F],
      redisUtils: RedisUtils[F],
      jwtUtils: JwtUtils[F],
  ) =
    new AppRoutes[F](session, redisUtils, jwtUtils)
}
