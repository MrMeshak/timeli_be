package cc.timeli.algebra.auth

import cats.MonadThrow
import cats.data.EitherT
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.typelevel.log4cats.LoggerFactory

import cc.timeli.algebra.auth.authDtos.*
import cc.timeli.core.errors.authErrors.*
import cc.timeli.core.domain.user.*

trait AuthAlgebra[F[_]] {
  def login(signupDto: SignupDto): EitherT[F, AuthError, LoginData]
  def signup(loginDto: LoginDto): EitherT[F, AuthError, Unit]
}

final class AuthAlgebraLive[F[_]: MonadThrow: LoggerFactory](session: Session[F]) extends AuthAlgebra[F] {
  override def login(signupDto: SignupDto): EitherT[F, AuthError, LoginData] = EitherT.leftT(InvalidCredentials("test"))

  override def signup(loginDto: LoginDto): EitherT[F, AuthError, Unit] = EitherT.leftT(InvalidCredentials("test"))

}

object AuthAlgebraLive {
  def apply[F[_]: MonadThrow: LoggerFactory](session: Session[F]) = new AuthAlgebraLive[F](session)
}
