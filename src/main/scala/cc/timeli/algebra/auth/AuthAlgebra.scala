package cc.timeli.algebra.auth

import cats.Monad
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.typelevel.log4cats.LoggerFactory

import cc.timeli.algebra.auth.AuthDto.*

trait AuthAlgebra {
  def login(signupDto: SignupDto): LoginData
  def signup(loginDto: LoginDto): Unit
}

final class AuthAlgebraLive[F[_]: Monad: LoggerFactory](session: Session[F]) extends AuthAlgebra {
  override def login(signupDto: SignupDto): LoginData = ???
  override def signup(loginDto: LoginDto): Unit       = ???
}

object AuthAlgebraLive {
  def apply[F[_]: Monad: LoggerFactory](session: Session[F]) = new AuthAlgebraLive[F](session)
}
