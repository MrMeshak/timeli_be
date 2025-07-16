package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import io.circe.{Encoder, Decoder, Json}
import io.circe.generic.semiauto.*

import java.util.UUID

import cc.timeli.core.domain.role.*
import cc.timeli.core.shared.enums.*

object user {

  final case class User(
      id: UUID,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      status: UserStatus,
  ) {}

  object User {
    given Encoder[User] = deriveEncoder[User].mapJsonObject(_.remove("password"))
  }

  val userCodec: Codec[User] = (uuid, varchar(255), varchar(255), varchar(255), varchar(255), varchar(50)).tupled.imap({
    case (id, email, password, firstName, lastName, status) =>
      User(
        id,
        email,
        password,
        firstName,
        lastName,
        UserStatus.fromStringUnsafe(status),
      )
  })({ user =>
    (user.id, user.email, user.password, user.firstName, user.lastName, user.status.value)
  })

  final case class UserWithRole(user: User, role: Role) {}

  val userWithRoleCodec: Codec[UserWithRole] = (userCodec, roleCodec).tupled.imap({
    case (user, role) => UserWithRole(user, role)
  })(uwr => (uwr.user, uwr.role))
}
