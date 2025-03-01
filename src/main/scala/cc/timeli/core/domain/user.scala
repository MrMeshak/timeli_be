package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import io.circe.{Encoder, Decoder, Json}
import io.circe.generic.semiauto.*

import java.util.UUID

object user {

  final case class User(
      id: UUID,
      email: String,
      password: String,
      firstName: String,
      lastName: String,
  ) {}

  object User {
    given Encoder[User] = deriveEncoder[User].mapJsonObject(_.remove("password"))
  }

  val userCodec: Codec[User] = (uuid, varchar(255), varchar(255), varchar(255), varchar(255)).tupled.imap({
    case (id, email, password, firstName, lastName) => User(id, email, password, firstName, lastName)
  })({ user =>
    (user.id, user.email, user.password, user.firstName, user.lastName)
  })

}
