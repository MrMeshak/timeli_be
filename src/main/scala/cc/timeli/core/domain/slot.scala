package cc.timeli.core.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*

import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec, Decoder => SkunkDecoder}

import java.util.UUID
import java.time.LocalDate

object slot {

  final case class Slot(
      id: UUID,
      slotDate: LocalDate,
      slotIndex: Int,
      status: String,
      roomId: UUID,
      bookingId: UUID,
      userId: UUID,
  )

  object Slot {
    given Decoder[Slot] = deriveDecoder[Slot]
    given Encoder[Slot] = deriveEncoder[Slot]
  }

  val slotCodec: SkunkCodec[Slot] = (
    uuid,
    date,
    int4,
    varchar(255),
    uuid,
    uuid,
    uuid,
  ).tupled.imap({
    case (id, slotDate, slotIndex, status, roomId, bookingId, userId) =>
      Slot(id, slotDate, slotIndex, status, roomId, bookingId, userId)
  })(s => (s.id, s.slotDate, s.slotIndex, s.status, s.roomId, s.bookingId, s.userId))

  enum SlotStatus {
    case PENDING, BOOKED
  }
}
