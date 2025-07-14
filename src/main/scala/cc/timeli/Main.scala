package cc.timeli

import cats.effect.{IO, IOApp}
import cats.effect.implicits.*
import cats.effect.kernel.Resource
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import pureconfig.ConfigSource
import skunk.Session
import natchez.Trace.Implicits.noop
import org.http4s.server.middleware.Logger
import org.http4s.ember.server.EmberServerBuilder
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout.given

import cc.timeli.core.config.syntax.*
import cc.timeli.core.config.{BaseConfig, DbConfig, ServerConfig, JwtConfig, RedisConfig, MailConfig}
import cc.timeli.core.db.Db
import cc.timeli.core.mail.Mail
import cc.timeli.core.utils.{JwtUtils, JwtUtilsLive}
import cc.timeli.core.utils.RedisUtilsLive
import cc.timeli.app.AppRoutes

object Main extends IOApp.Simple {

  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run: IO[Unit] = {
    val serverR = for {
      baseConfig   <- Resource.eval(ConfigSource.default.at("base").loadF[IO, BaseConfig])
      serverConfig <- Resource.eval(ConfigSource.default.at("server").loadF[IO, ServerConfig])
      dbConfig     <- Resource.eval(ConfigSource.default.at("db").loadF[IO, DbConfig])
      jwtConfig    <- Resource.eval(ConfigSource.default.at("jwt").loadF[IO, JwtConfig])
      redisConfig  <- Resource.eval(ConfigSource.default.at("redis").loadF[IO, RedisConfig])
      mailConfig   <- Resource.eval(ConfigSource.default.at("mail").loadF[IO, MailConfig])
      session      <- Db.single[IO](dbConfig)
      redis        <- Redis[IO].utf8(redisConfig.url)
      mailer       <- Resource.eval(Mail.mailer[IO](mailConfig))
      jwtUtils     <- Resource.eval(IO.pure(JwtUtilsLive[IO](jwtConfig)))
      redisUtils   <- Resource.eval(IO.pure(RedisUtilsLive[IO](redis)))
      server <- EmberServerBuilder
        .default[IO]
        .withHost(serverConfig.host)
        .withPort(serverConfig.port)
        .withHttpApp(
          Logger.httpApp(logHeaders = false, logBody = true)(
            AppRoutes[IO](baseConfig, session, mailer, redisUtils, jwtUtils).routes.orNotFound,
          ),
        )
        .build
    } yield server

    serverR.use(_ => IO.println("server started") *> IO.never)
  }
}
