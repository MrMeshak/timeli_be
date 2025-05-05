package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object room {

  final case class Room(
      id: UUID,
      name: String,
      description: String,
      capacity: Int,
      defaultPrice: BigDecimal,
      locationId: UUID,
  )

  val roomCodec: Codec[Room] = (uuid, varchar(255), text, int4, numeric(10, 2), uuid).tupled.imap({
    case (id, name, description, capacity, defaultPrice, locationId) =>
      Room(id, name, description, capacity, defaultPrice, locationId)
  })(r => (r.id, r.name, r.description, r.capacity, r.defaultPrice, r.locationId))
}
