package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*
import java.util.UUID

object roomType {

  final case class RoomType(
      id: UUID,
      name: String,
  ) {}

  val roomTypeCodec: Codec[RoomType] = (uuid, varchar(255)).tupled.imap({
    case (id, name) => RoomType(id, name)
  })(r => (r.id, r.name))
}
