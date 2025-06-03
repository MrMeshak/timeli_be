package cc.timeli.core.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object location {

  final case class Location(
      id: UUID,
      name: String,
      description: String,
      street: String,
      city: String,
      state: String,
      country: String,
      postCode: String,
  ) {}

  object Location {
    given Encoder[Location] = deriveEncoder[Location]
    given Decoder[Location] = deriveDecoder[Location]
  }

  val locationCodec: Codec[Location] =
    (uuid, varchar(255), varchar(255), varchar(255), varchar(255), varchar(255), varchar(255), varchar(255)).tupled
      .imap({
        case (id, name, description, street, city, state, country, postCode) =>
          Location(id, name, description, street, city, state, country, postCode)
      })(l => (l.id, l.name, l.description, l.street, l.city, l.state, l.country, l.postCode))

}
