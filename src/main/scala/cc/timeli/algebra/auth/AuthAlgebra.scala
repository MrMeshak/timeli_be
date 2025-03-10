package cc.timeli.algebra.auth

import scala.concurrent.duration.*

import cats.effect.Concurrent
import cats.data.EitherT
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.http4s.{ResponseCookie, SameSite}
import org.typelevel.log4cats.{Logger, LoggerFactory}
import dev.profunktor.redis4cats.RedisCommands

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.UUID

import cc.timeli.algebra.auth.authDtos.*
import cc.timeli.core.errors.BaseError
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.core.domain.user.*
import cc.timeli.core.utils.JwtUtils
import cc.timeli.core.utils.RedisUtils
import cc.timeli.core.logging.syntax.*

trait AuthAlgebra[F[_]] {
  def login(loginDto: LoginDto): EitherT[F, BaseError, LoginData]
  def signup(signupDto: SignupDto): EitherT[F, BaseError, Unit]
}

final class AuthAlgebraLive[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
    redisUtils: RedisUtils[F],
    jwtUtils: JwtUtils[F],
) extends AuthAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def login(loginDto: LoginDto): EitherT[F, BaseError, LoginData] = {
    for {
      query <- EitherT.right(
        session
          .prepare(
            sql"""SELECT * FROM users WHERE email = $varchar"""
              .query(userCodec),
          ),
      )
      user <- EitherT.fromOptionF(query.option(loginDto.email), InvalidCredentialsError())
      _ <- EitherT.cond(
        BCrypt.verifyer().verify(loginDto.password.toCharArray(), user.password).verified,
        (),
        InvalidCredentialsError(),
      )
      accessToken  <- EitherT.right(jwtUtils.createAccessToken(user.id))
      refreshToken <- EitherT.right(jwtUtils.createRefreshToken(accessToken))
      _ <- EitherT.right(redisUtils.setRefreshToken(user.id, refreshToken, jwtUtils.config.refreshTokenExpTime.seconds))
      accessTokenCookie <- EitherT.rightT(
        ResponseCookie(
          name = "accessToken",
          content = accessToken,
          httpOnly = true,
          secure = true,
          sameSite = Some(SameSite.Strict),
          maxAge = Some(jwtUtils.config.accessTokenExpTime),
        ),
      )
      refreshTokenCookie <- EitherT.rightT(
        ResponseCookie(
          name = "refreshToken",
          content = refreshToken,
          httpOnly = true,
          secure = true,
          sameSite = Some(SameSite.Strict),
          maxAge = Some(jwtUtils.config.refreshTokenExpTime),
        ),
      )
    } yield LoginData(
      accessTokenCookie,
      refreshTokenCookie,
    )
  }

  override def signup(signupDto: SignupDto): EitherT[F, BaseError, Unit] = {
    for {
      query <- EitherT.right(
        session.prepare(
          sql"""SELECT id, email, password, firstName, lastName FROM users WHERE email = $varchar""".query(userCodec),
        ),
      )
      _ <- EitherT(
        query
          .option(signupDto.email)
          .map({
            case Some(_) => Left(AlreadyExistsError("User already exists, please login"))
            case None    => Right(())
          }),
      )
      command <- EitherT.right(session.prepare(sql"""INSERT INTO users VALUES ($userCodec)""".command))
      _ <- EitherT.right(
        command.execute(
          User(
            id = UUID.randomUUID(),
            email = signupDto.email,
            password = BCrypt.withDefaults().hashToString(12, signupDto.password.toCharArray()),
            firstName = signupDto.firstName,
            lastName = signupDto.lastName,
          ),
        ),
      )
    } yield ()
  }
}

object AuthAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](
      session: Session[F],
      redisUtils: RedisUtils[F],
      jwtUtils: JwtUtils[F],
  ) =
    new AuthAlgebraLive[F](session, redisUtils, jwtUtils)

}
