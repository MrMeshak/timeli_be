package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object user {

  final case class User(
      id: UUID,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
  ) {}

  val userCodec: Codec[User] = (uuid, varchar, varchar, varchar, varchar).tupled.imap({
    case (id, email, password, firstName, lastName) => User(id, email, password, firstName, lastName)
  })({ user =>
    (user.id, user.email, user.password, user.firstName, user.lastName)
  })

}
