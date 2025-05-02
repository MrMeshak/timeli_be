package cc.timeli.middleware

import cats.effect.{Concurrent}
import cats.data.{Kleisli, OptionT}
import cats.implicits.*
import org.http4s.{Request, Response, AuthedRequest, AuthedRoutes, ResponseCookie, SameSite}
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats.{Logger, LoggerFactory}
import io.circe.syntax.*
import io.circe.generic.semiauto.*

import java.util.UUID

import cc.timeli.core.utils.JwtUtils
import cc.timeli.core.utils.RedisUtils
import cc.timeli.core.logging.syntax.*

//AuthMiddlewareProvider

class AuthMP[F[_]: Concurrent: LoggerFactory](jwtUtils: JwtUtils[F], redisUtils: RedisUtils[F]) {
  given Logger[F] = LoggerFactory.getLogger()

  private val authContextK: Kleisli[[A] =>> OptionT[F, A], Request[F], AuthContext] = Kleisli(req => {

    val authContextFromAccessToken = for {
      accessToken        <- OptionT.fromOption(req.cookies.find(_.name == "accessToken").map(_.content))
      decodedAccessToken <- OptionT(jwtUtils.verifyAndParseAccessToken(accessToken))
      userId             <- OptionT.fromOption(decodedAccessToken.body.subject).map(UUID.fromString(_))
      permissions <- OptionT.fromOption(
        decodedAccessToken.body.getCustom[String]("permissions").toOption.map(BigInt(_)),
      )
      authContext <- OptionT.some(AuthContext(userId, permissions, false))
    } yield authContext

    val authContextFromRefreshToken = for {
      accessToken         <- OptionT.fromOption(req.cookies.find(c => c.name == "accessToken").map(_.content))
      refreshToken        <- OptionT.fromOption(req.cookies.find(c => c.name == "refreshToken").map(_.content))
      decodedRefreshToken <- OptionT(jwtUtils.verifyAndParseRefreshToken(refreshToken))
      refreshTokenSub     <- OptionT.fromOption(decodedRefreshToken.body.subject)
      _                   <- OptionT.when(refreshTokenSub == accessToken)(())
      decodedAccessToken  <- OptionT(jwtUtils.parseUnverifiedAccessToken(refreshTokenSub))
      userId              <- OptionT.fromOption(decodedAccessToken.body.subject).map(UUID.fromString(_))
      permissions <- OptionT.fromOption(
        decodedAccessToken.body.getCustom[String]("permissions").toOption.map(BigInt(_)),
      )
      setTokens <- OptionT(redisUtils.getRefreshToken(userId))
        .filter(_ == refreshToken)
        .semiflatTap(redisUtils.cacheRefreshToken(userId, _) *> redisUtils.deleteRefreshToken(userId))
        .map(_ => true)
        .orElse(
          OptionT(redisUtils.getCacheRefreshToken(userId))
            .filter(_ == refreshToken)
            .map(_ => false),
        )
      authContext <- OptionT.some(AuthContext(userId, permissions, setTokens))
    } yield authContext

    authContextFromAccessToken.orElse(authContextFromRefreshToken)
  })

  private def cookieInterceptor(authedRoutes: AuthedRoutes[AuthContext, F]): AuthedRoutes[AuthContext, F] = Kleisli {
    req =>
      authedRoutes(req).flatMap(resp =>
        if (!req.authInfo.setTokens) OptionT.some(resp)
        else {
          for {
            accessToken  <- OptionT.liftF(jwtUtils.createAccessToken(req.authInfo.userId, req.authInfo.permissions))
            refreshToken <- OptionT.liftF(jwtUtils.createRefreshToken(accessToken))
            accessTokenCookie <- OptionT.some(
              ResponseCookie(
                name = "accessToken",
                content = accessToken,
                path = Some("/"),
                httpOnly = true,
                secure = true,
                sameSite = Some(SameSite.Strict),
                maxAge = Some(jwtUtils.config.accessTokenExpTime),
              ),
            )
            refreshTokenCookie <- OptionT.some(
              ResponseCookie(
                name = "refreshToken",
                content = refreshToken,
                path = Some("/"),
                httpOnly = true,
                secure = true,
                sameSite = Some(SameSite.Strict),
                maxAge = Some(jwtUtils.config.refreshTokenExpTime),
              ),
            )
          } yield resp.addCookie(accessTokenCookie).addCookie(refreshTokenCookie)
        },
      )
  }

  def middleware(routes: AuthedRoutes[AuthContext, F]) =
    AuthMiddleware.withFallThrough(authContextK).apply(cookieInterceptor(routes))
}

object AuthMP {
  def apply[F[_]: Concurrent: LoggerFactory](jwtUtils: JwtUtils[F], redisUtils: RedisUtils[F]) =
    new AuthMP(jwtUtils, redisUtils)
}

case class AuthContext(userId: UUID, permissions: BigInt, setTokens: Boolean) {}
