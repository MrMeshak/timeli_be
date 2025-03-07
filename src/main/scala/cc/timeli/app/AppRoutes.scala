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

class AppRoutes[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
    redis: RedisCommands[F, String, String],
    jwtUtils: JwtUtils[F],
) {

  private val authAlgebra = AuthAlgebraLive[F](session, redis, jwtUtils)

  private val healthRoutes = HealthRoutes[F].routes
  private val authRoutes   = AuthRoutes[F](authAlgebra).routes

  val routes = Router(
    "api" -> (healthRoutes <+> authRoutes),
  )
}

object AppRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](
      session: Session[F],
      redis: RedisCommands[F, String, String],
      jwtUtils: JwtUtils[F],
  ) =
    new AppRoutes[F](session, redis, jwtUtils)
}
