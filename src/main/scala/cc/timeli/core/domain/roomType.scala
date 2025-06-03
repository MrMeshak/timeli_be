package cc.timeli.core.domain

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.{Codec => SkunkCodec}
import skunk.codec.all.*
import java.util.UUID

object roomType {

  final case class RoomType(
      id: String,
      name: String,
  ) {}

  object RoomType {
    given Decoder[RoomType] = deriveDecoder[RoomType]
    given Encoder[RoomType] = deriveEncoder[RoomType]
  }

  val roomTypeCodec: SkunkCodec[RoomType] = (varchar(255), varchar(255)).tupled.imap({
    case (id, name) => RoomType(id, name)
  })(r => (r.id, r.name))
}
