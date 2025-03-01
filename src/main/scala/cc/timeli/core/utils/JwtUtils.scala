package cc.timeli.core.utils

import cats.Monad
import cats.effect.Sync
import cats.implicits.*
import tsec.jws.mac.JWTMac
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import tsec.jwt.{JWTClaims}

import java.time.Instant
import java.util.UUID

import cc.timeli.core.config.{JwtConfig}

trait JwtUtils[F[_]] {
  def createAccessToken(id: UUID): F[String]
  def createRefreshToken(id: UUID): F[String]
}

final class JwtUtilsLive[F[_]: Sync](jwtConfig: JwtConfig) extends JwtUtils[F] {

  override def createAccessToken(id: UUID): F[String] = for {
    secret <- HMACSHA256.buildKey[F](jwtConfig.accessTokenSecret.getBytes())
    claim <- JWTClaims(
      issuedAt = Some(Instant.now()),
      expiration = Some(Instant.now().plusSeconds(jwtConfig.accessTokenExpTime)),
      subject = Some(id.toString()),
    ).pure
    token <- JWTMac.buildToString[F, HMACSHA256](claim, secret)
  } yield token

  override def createRefreshToken(id: UUID): F[String] = for {
    secret <- HMACSHA256.buildKey[F](jwtConfig.refreshTokenSecret.getBytes())
    claim <- JWTClaims(
      issuedAt = Some(Instant.now()),
      expiration = Some(Instant.now().plusSeconds(jwtConfig.refreshTokenExpTime)),
      subject = Some(id.toString()),
    ).pure
    token <- JWTMac.buildToString[F, HMACSHA256](claim, secret)
  } yield token
}

object JwtUtilsLive {
  def apply[F[_]: Sync](jwtConfig: JwtConfig): JwtUtilsLive[F] = new JwtUtilsLive(jwtConfig)
}
