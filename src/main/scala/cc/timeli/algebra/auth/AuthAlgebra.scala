package cc.timeli.algebra.auth

import scala.concurrent.duration.*

import cats.effect.Concurrent
import cats.data.{EitherT, OptionT}
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.http4s.{ResponseCookie, SameSite}
import org.typelevel.log4cats.{Logger, LoggerFactory}
import pencil.{Client => MailClient}
import pencil.data.Email
import pencil.data.Mailbox
import pencil.protocol.Replies

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.UUID

import cc.timeli.core.config.BaseConfig
import cc.timeli.algebra.auth.authDtos.*
import cc.timeli.core.errors.BaseError
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.core.domain.user.*
import cc.timeli.core.utils.JwtUtils
import cc.timeli.core.utils.RedisUtils
import cc.timeli.core.logging.syntax.*
import cc.timeli.core.mail.templates

trait AuthAlgebra[F[_]] {
  def login(loginDto: LoginDto): EitherT[F, BaseError, LoginData]
  def mLogin(loginDto: LoginDto): EitherT[F, BaseError, LoginData]
  def signup(signupDto: SignupDto): EitherT[F, BaseError, Unit]
  def logout(logoutDto: LogoutDto): EitherT[F, BaseError, LogoutData]
  def passwordForgot(passwordForgotDto: PasswordForgotDto): EitherT[F, BaseError, Replies]
  def passwordReset(passwordResetDto: PasswordResetDto): EitherT[F, BaseError, Unit]
}

final class AuthAlgebraLive[F[_]: Concurrent: LoggerFactory](
    baseConfig: BaseConfig,
    session: Session[F],
    mailer: MailClient[F],
    redisUtils: RedisUtils[F],
    jwtUtils: JwtUtils[F],
) extends AuthAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def login(loginDto: LoginDto): EitherT[F, BaseError, LoginData] = {
    for {
      query <- EitherT.right(
        session
          .prepare(
            sql"""SELECT u.id, u.email, u.password, u.firstName, u.lastName, r.id, r.name, r.mask 
                FROM users u 
                INNER JOIN roles r ON u.roleId = r.id 
                WHERE email = $varchar
                """
              .query(userWithRoleCodec),
          ),
      )
      userWithRole <- EitherT.fromOptionF(query.option(loginDto.email), InvalidCredentialsError())
      _ <- EitherT.cond(
        BCrypt.verifyer().verify(loginDto.password.toCharArray(), userWithRole.user.password).verified,
        (),
        InvalidCredentialsError(),
      )
      accessToken  <- EitherT.right(jwtUtils.createAccessToken(userWithRole.user.id, userWithRole.role.mask))
      refreshToken <- EitherT.right(jwtUtils.createRefreshToken(accessToken))
      _ <- EitherT.right(
        redisUtils.setRefreshToken(userWithRole.user.id, refreshToken, jwtUtils.config.refreshTokenExpTime.seconds),
      )
      accessTokenCookie <- EitherT.rightT(
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
      refreshTokenCookie <- EitherT.rightT(
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
    } yield LoginData(
      accessTokenCookie,
      refreshTokenCookie,
      PermissionsData(userWithRole.role.mask),
    )
  }

  override def mLogin(loginDto: LoginDto): EitherT[F, BaseError, LoginData] = {
    for {
      query <- EitherT.right(
        session
          .prepare(
            sql"""SELECT u.id, u.email, u.password, u.firstName, u.lastName, r.id, r.name, r.mask 
                FROM users u 
                INNER JOIN roles r ON u.roleId = r.id 
                WHERE u.email = $varchar AND r.name NOT IN ('GUEST', 'USER')
                """
              .query(userWithRoleCodec),
          ),
      )
      userWithRole <- EitherT.fromOptionF(query.option(loginDto.email), InvalidCredentialsError())
      _ <- EitherT.cond(
        BCrypt.verifyer().verify(loginDto.password.toCharArray(), userWithRole.user.password).verified,
        (),
        InvalidCredentialsError(),
      )
      accessToken  <- EitherT.right(jwtUtils.createAccessToken(userWithRole.user.id, userWithRole.role.mask))
      refreshToken <- EitherT.right(jwtUtils.createRefreshToken(accessToken))
      _ <- EitherT.right(
        redisUtils.setRefreshToken(userWithRole.user.id, refreshToken, jwtUtils.config.refreshTokenExpTime.seconds),
      )
      accessTokenCookie <- EitherT.rightT(
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
      refreshTokenCookie <- EitherT.rightT(
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
    } yield LoginData(
      accessTokenCookie,
      refreshTokenCookie,
      PermissionsData(userWithRole.role.mask),
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
      command <- EitherT.right(
        session.prepare(
          sql"""INSERT INTO users VALUES ($userCodec, DEFAULT, (SELECT id FROM roles WHERE name = 'USER'))""".command,
        ),
      )
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

  override def logout(logoutDto: LogoutDto): EitherT[F, BaseError, LogoutData] = {
    for {
      _ <- EitherT.right(redisUtils.deleteRefreshToken(logoutDto.userId))
      accessTokenCookieEmpty <- EitherT.rightT(
        ResponseCookie(
          name = "accessToken",
          content = "",
          path = Some("/"),
          httpOnly = true,
          secure = true,
          sameSite = Some(SameSite.Strict),
          maxAge = Some(0),
        ),
      )
      refreshTokenCookieEmpty <- EitherT.rightT(
        ResponseCookie(
          name = "refreshToken",
          content = "",
          path = Some("/"),
          httpOnly = true,
          secure = true,
          sameSite = Some(SameSite.Strict),
          maxAge = Some(0),
        ),
      )
    } yield LogoutData(accessTokenCookieEmpty, refreshTokenCookieEmpty)
  }

  override def passwordForgot(
      passwordForgotDto: PasswordForgotDto,
  ): EitherT[F, BaseError, Replies] = {
    for {
      _ <- EitherT(
        redisUtils
          .isWithinRateLimitPasswordForget(passwordForgotDto.email, 3, 10.minutes)
          .map(Either.cond(_, (), RateLimitedError("Password forgot request limit reached, please try again later"))),
      )
      query <- EitherT.right(
        session.prepare(
          sql"""SELECT id, email, password, firstName, lastName FROM users WHERE email = $varchar""".query(userCodec),
        ),
      )
      user <- EitherT.fromOptionF(query.option(passwordForgotDto.email), NotFoundError("User could not be found"))
      passwordResetToken <- EitherT.right(jwtUtils.createPasswordResetToken(user.id))
      _ <- EitherT.right(
        redisUtils.setPasswordResetToken(
          user.id,
          passwordResetToken,
          jwtUtils.config.passwordResetTokenExpTime.seconds,
        ),
      )
      toMailbox <- EitherT.fromEither(
        Mailbox.fromString(passwordForgotDto.email).leftMap(_ => InvalidInputError("Invalid email format")),
      )
      replies <- EitherT.right(
        mailer.send(
          templates.passwordResetEmail(
            toMailbox,
            baseConfig.timeliFeUrl + s"/auth/passwordReset?token=${passwordResetToken}",
          ),
        ),
      )
    } yield replies
  }

  override def passwordReset(passwordResetDto: PasswordResetDto): EitherT[F, BaseError, Unit] = {
    for {
      decodedToken <- EitherT.fromOptionF(
        jwtUtils.verifyAndParsePasswordResetToken(passwordResetDto.token),
        InvalidTokenError("Password reset link is expired or invalid"),
      )
      userId <- EitherT.fromOption(
        decodedToken.body.subject.map(UUID.fromString),
        InvalidTokenError("Password reset link is expired or invalid"),
      )
      _ <- EitherT.fromOptionF(
        OptionT(redisUtils.getPasswordResetToken(userId))
          .filter(_ == passwordResetDto.token)
          .semiflatTap(_ => redisUtils.deletePasswordResetToken(userId))
          .value,
        InvalidTokenError("Password reset link is expired or invalid"),
      )

      command <- EitherT.right(
        session.prepare(sql"""UPDATE users SET password = $text WHERE id = $uuid""".command),
      )
      _ <- EitherT.right(
        command.execute(
          BCrypt.withDefaults().hashToString(12, passwordResetDto.password.toCharArray()),
          userId,
        ),
      )
    } yield ()
  }
}

object AuthAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](
      baseConfig: BaseConfig,
      session: Session[F],
      mailer: MailClient[F],
      redisUtils: RedisUtils[F],
      jwtUtils: JwtUtils[F],
  ) =
    new AuthAlgebraLive[F](baseConfig, session, mailer, redisUtils, jwtUtils)
}
