package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID
import java.time.LocalTime

object availability {

  final case class Availability(
      id: UUID,
      dayOfWeek: Int,
      mask: BigInt,
      roomId: UUID,
  )

  val availabilityCodec: Codec[Availability] =
    (uuid, int4, text, uuid).tupled.imap({
      case (id, dayOfWeek, mask, roomId) => Availability(id, dayOfWeek, BigInt(mask), roomId)
    })(a => (a.id, a.dayOfWeek, a.mask.toString, a.roomId))

}
