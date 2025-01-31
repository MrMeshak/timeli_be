package cc.timeli

import cats.effect.{IO, IOApp}
import cats.effect.implicits.*
import cats.effect.kernel.Resource
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig.ConfigSource
import skunk.Session
import natchez.Trace.Implicits.noop
import org.http4s.ember.server.EmberServerBuilder

import cc.timeli.core.config.syntax.*
import cc.timeli.core.config.{DbConfig, ServerConfig}
import cc.timeli.core.db.Db
import cc.timeli.app.AppRoutes

object Main extends IOApp.Simple {

  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run: IO[Unit] = {

    val serverR = for {
      serverConfig <- Resource.eval(ConfigSource.default.at("server").loadF[IO, ServerConfig])
      dbConfig     <- Resource.eval(ConfigSource.default.at("db").loadF[IO, DbConfig])
      session      <- Db.single[IO](dbConfig)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(serverConfig.host)
        .withPort(serverConfig.port)
        .withHttpApp(AppRoutes[IO](session).routes.orNotFound)
        .build
    } yield server

    serverR.use(_ => IO.println("server started") *> IO.never)
  }
}
