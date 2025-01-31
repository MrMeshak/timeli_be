package cc.timeli.app

import cats.effect.Concurrent
import cats.implicits.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*

import org.typelevel.log4cats.LoggerFactory
import skunk.Session

import cc.timeli.app.HealthRoutes

class AppRoutes[F[_]: Concurrent: LoggerFactory](session: Session[F]) {

  private val healthRoutes = HealthRoutes[F].routes

  val routes = Router(
    "api" -> healthRoutes,
  )
}

object AppRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](session: Session[F]) = new AppRoutes[F](session)
}
