package cc.timeli.core.utils

import cats.effect.Sync
import cats.implicits.*
import tsec.jws.mac.JWTMac
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import tsec.jwt.{JWTClaims}

import java.time.Instant
import java.util.UUID

import cc.timeli.core.config.{JwtConfig}

trait JwtUtils[F[_]] {
  def config: JwtConfig
  def createAccessToken(id: UUID): F[String]
  def createRefreshToken(accessToken: String): F[String]
  def parseUnverifiedAccessToken(token: String): F[Option[JWTMac[HMACSHA256]]]
  def verifyAndParseAccessToken(token: String): F[Option[JWTMac[HMACSHA256]]]
  def verifyAndParseRefreshToken(token: String): F[Option[JWTMac[HMACSHA256]]]
}

final class JwtUtilsLive[F[_]: Sync](jwtConfig: JwtConfig) extends JwtUtils[F] {

  override def config: JwtConfig = jwtConfig;

  override def createAccessToken(id: UUID): F[String] = for {
    secret <- HMACSHA256.buildKey[F](jwtConfig.accessTokenSecret.getBytes())
    claim <- JWTClaims(
      issuedAt = Some(Instant.now()),
      expiration = Some(Instant.now().plusSeconds(jwtConfig.accessTokenExpTime)),
      subject = Some(id.toString()),
    ).pure
    token <- JWTMac.buildToString[F, HMACSHA256](claim, secret)
  } yield token

  override def createRefreshToken(accessToken: String): F[String] = for {
    secret <- HMACSHA256.buildKey[F](jwtConfig.refreshTokenSecret.getBytes())
    claim <- JWTClaims(
      issuedAt = Some(Instant.now()),
      expiration = Some(Instant.now().plusSeconds(jwtConfig.refreshTokenExpTime)),
      subject = Some(accessToken),
    ).pure
    token <- JWTMac.buildToString[F, HMACSHA256](claim, secret)
  } yield token

  override def parseUnverifiedAccessToken(token: String): F[Option[JWTMac[HMACSHA256]]] = for {
    secret       <- HMACSHA256.buildKey[F](jwtConfig.accessTokenSecret.getBytes())
    decodedToken <- JWTMac.parseUnverified[F, HMACSHA256](token).attempt
  } yield decodedToken.toOption

  override def verifyAndParseAccessToken(token: String): F[Option[JWTMac[HMACSHA256]]] = for {
    secret       <- HMACSHA256.buildKey[F](jwtConfig.accessTokenSecret.getBytes())
    decodedToken <- JWTMac.verifyAndParse[F, HMACSHA256](token, secret).attempt
  } yield decodedToken.toOption

  override def verifyAndParseRefreshToken(token: String): F[Option[JWTMac[HMACSHA256]]] = for {
    secret       <- HMACSHA256.buildKey[F](jwtConfig.refreshTokenSecret.getBytes())
    decodedToken <- JWTMac.verifyAndParse[F, HMACSHA256](token, secret).attempt
  } yield decodedToken.toOption

}

object JwtUtilsLive {
  def apply[F[_]: Sync](jwtConfig: JwtConfig): JwtUtilsLive[F] = new JwtUtilsLive(jwtConfig)
}
