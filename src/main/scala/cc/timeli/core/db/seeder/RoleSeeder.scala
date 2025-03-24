package cc.timeli.core.db.seeder

import cats.effect.{IOApp, IO}
import cats.effect.kernel.Resource
import cats.syntax.*
import cats.implicits.*
import pureconfig.ConfigSource
import natchez.Trace.Implicits.noop
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

import java.util.UUID

import cc.timeli.core.config.DbConfig
import cc.timeli.core.db.Db
import cc.timeli.core.config.syntax.*
import cc.timeli.core.domain.role.roleCodec
import cc.timeli.core.shared.Permission
import cc.timeli.core.domain.role.Role

object RoleSeeder extends IOApp.Simple {

  override def run: IO[Unit] = {

    val roles: List[Role] = List(
      Role(UUID.fromString("52f39797-a765-43e8-9473-accf7f34a6a7"), "USER", Permission.READ_USER_INFO.mask),
      Role(UUID.fromString("0c4def54-0d0c-4b3c-8dda-5ad58a394cd0"), "ADMIN", Permission.READ_USER_INFO.mask),
    )

    val sessionR = for {
      dbConfig <- Resource.eval(ConfigSource.default.at("db").loadF[IO, DbConfig])
      session  <- Db.single[IO](dbConfig)
    } yield session

    sessionR.use(session => {
      for {
        command <- session.prepare(
          sql"""
          INSERT INTO roles VALUES ($roleCodec)
          ON CONFLICT(id) DO UPDATE SET 
            name = EXCLUDED.name,
            mask = EXCLUDED.mask
          """.command,
        )
        _ <- roles.traverse(r => command.execute(r))
      } yield ()
    })
  }
}
