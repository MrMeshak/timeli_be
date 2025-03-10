package cc.timeli.core.utils

import scala.concurrent.duration.*

import cats.effect.Async
import dev.profunktor.redis4cats.{RedisCommands}

import java.util.UUID

import cc.timeli.core.config.JwtConfig

trait RedisUtils[F[_]] {
  def setRefreshToken(userId: UUID, token: String, exp: FiniteDuration): F[Unit]
  def getRefreshToken(userId: UUID): F[Option[String]]
  def deleteRefreshToken(userId: UUID): F[Long]
  def cacheRefreshToken(userId: UUID, token: String): F[Unit]
  def getCacheRefreshToken(userId: UUID): F[Option[String]]
}

final class RedisUtilsLive[F[_]: Async](redis: RedisCommands[F, String, String]) extends RedisUtils[F] {

  override def setRefreshToken(userId: UUID, token: String, exp: FiniteDuration): F[Unit] =
    redis.setEx(s"userId:${userId}:refreshToken", token, exp)

  override def getRefreshToken(userId: UUID): F[Option[String]] = redis.get(s"userId:${userId}:refreshToken")

  override def deleteRefreshToken(userId: UUID): F[Long] = redis.del(s"userId:${userId}:refreshToken")

  override def cacheRefreshToken(userId: UUID, token: String): F[Unit] =
    redis.setEx(s"userId:${userId}:refreshTokenCache", token, 7.seconds)

  override def getCacheRefreshToken(userId: UUID): F[Option[String]] =
    redis.get(s"userId:${userId}:refreshTokenCache")
}

object RedisUtilsLive {
  def apply[F[_]: Async](redis: RedisCommands[F, String, String]) = new RedisUtilsLive[F](redis)
}
