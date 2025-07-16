package cc.timeli.algebra.user

import cats.effect.Concurrent
import cats.data.EitherT
import org.typelevel.log4cats.{Logger, LoggerFactory}
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

import cc.timeli.core.domain.user.*
import cc.timeli.core.errors.BaseError
import cc.timeli.algebra.user.userDtos.*
import cc.timeli.core.errors.baseErrors.*

trait UserAlgebra[F[_]] {
  def me(meDto: MeDto): EitherT[F, BaseError, MeData]
  def table(tableDto: TableDto): EitherT[F, BaseError, TableData]
}

final class UserAlgebraLive[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
) extends UserAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def me(meDto: MeDto): EitherT[F, BaseError, MeData] = {
    for {
      query <- EitherT.right(
        session.prepare(
          sql"""SELECT id, email, password, firstName, lastName, status FROM users WHERE id = $uuid""".query(userCodec),
        ),
      )
      user <- EitherT.fromOptionF(query.option(meDto.id), NotFoundError("User could not be found"))
    } yield MeData(
      id = user.id,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      status = user.status,
    )
  }

  override def table(tableDto: TableDto): EitherT[F, BaseError, TableData] = ???
}

object UserAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](session: Session[F]) = new UserAlgebraLive(session)
}
