package cc.timeli.core.db

import cats.effect.{Resource, Temporal}
import cats.effect.std.Console
import fs2.io.net.Network
import natchez.Trace
import skunk.Session

import cc.timeli.core.config.DbConfig

object Db {

  def single[F[_]: Temporal: Network: Trace: Console](
      dbConfig: DbConfig,
  ): Resource[F, Session[F]] = {
    Session.single(
      host = dbConfig.host,
      port = dbConfig.port,
      user = dbConfig.username,
      password = Some(dbConfig.password),
      database = dbConfig.database,
    )
  }

  def pooled[F[_]: Temporal: Network: Trace: Console](
      dbConfig: DbConfig,
  ): Resource[F, Resource[F, Session[F]]] = {
    Session.pooled(
      host = dbConfig.host,
      port = dbConfig.port,
      user = dbConfig.username,
      password = Some(dbConfig.password),
      database = dbConfig.database,
      max = 10,
    )
  }
}
