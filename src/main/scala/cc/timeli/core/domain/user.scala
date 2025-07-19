package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import io.circe.{Encoder, Decoder, Json}
import io.circe.generic.semiauto.*
import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec, Decoder => SkunkDecoder}

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

  val userCodec: SkunkCodec[User] =
    (uuid, varchar(255), varchar(255), varchar(255), varchar(255), varchar(50)).tupled.imap({
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

  final case class UserWithRole(
      id: UUID,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      status: UserStatus,
      role: Role,
  ) {}

  object UserWithRole {
    given Decoder[UserWithRole] = deriveDecoder[UserWithRole]
    given Encoder[UserWithRole] = deriveEncoder[UserWithRole].mapJsonObject(_.remove("password"))
  }

  val userWithRoleCodec: SkunkCodec[UserWithRole] =
    (uuid, varchar(255), varchar(255), varchar(255), varchar(255), varchar(50), roleCodec).tupled.imap({
      case (id, email, password, firstName, lastName, status, role) =>
        UserWithRole(id, email, password, firstName, lastName, UserStatus.fromStringUnsafe(status), role)
    })(uwr => (uwr.id, uwr.email, uwr.password, uwr.firstName, uwr.lastName, uwr.status.value, uwr.role))

  final case class UserTable(
      rowCount: Int,
      rowData: List[UserWithRole],
  )

  object UserTable {
    given Decoder[UserTable] = deriveDecoder[UserTable]
    given Encoder[UserTable] = deriveEncoder[UserTable]
  }

  val userTableCodec: SkunkCodec[UserTable] = jsonb[UserTable]
}
