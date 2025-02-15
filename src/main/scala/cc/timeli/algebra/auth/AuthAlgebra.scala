package cc.timeli.algebra.auth

import scala.util.Try

import cats.MonadThrow
import cats.data.EitherT
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.typelevel.log4cats.LoggerFactory

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.UUID

import cc.timeli.algebra.auth.authDtos.*
import cc.timeli.core.errors.BaseError
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.core.domain.user.*

trait AuthAlgebra[F[_]] {
  def login(signupDto: LoginDto): EitherT[F, BaseError, LoginData]
  def signup(loginDto: SignupDto): EitherT[F, BaseError, Unit]
}

final class AuthAlgebraLive[F[_]: MonadThrow: LoggerFactory](session: Session[F]) extends AuthAlgebra[F] {
  val logger = LoggerFactory.getLogger()

  override def login(login: LoginDto): EitherT[F, BaseError, LoginData] = ???

  override def signup(signupDto: SignupDto): EitherT[F, BaseError, Unit] = {
    for {
      query <- EitherT.right(session.prepare(sql"""SELECT * FROM users WHERE email = $varchar""".query(userCodec)))
      _ <- EitherT(
        query
          .option(signupDto.email)
          .map(u => if (!u.isDefined) Right(()) else Left(AlreadyExistsError("user already exists, please login"))),
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
      _ <- EitherT.right(logger.info("inserted user into DB"))
    } yield Right(())
  }
}

object AuthAlgebraLive {
  def apply[F[_]: MonadThrow: LoggerFactory](session: Session[F]) = new AuthAlgebraLive[F](session)

}
