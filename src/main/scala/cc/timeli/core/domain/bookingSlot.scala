package cc.timeli.core.domain

import io.circe.{Decoder, Encoder}
import io.circe.syntax.*
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*

import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec}

import java.util.UUID
import java.time.LocalDate

import cc.timeli.core.shared.enums.BookingSlotStatus
import cc.timeli.core.domain.user.UserWithRole

object bookingSlot {

  final case class BookingSlot(
      id: UUID,
      slotDate: LocalDate,
      slotIndex: Int,
      status: BookingSlotStatus,
      roomId: UUID,
      bookingId: UUID,
      userId: UUID,
  )

  object BookingSlot {}

  val bookingSlotCodec: SkunkCodec[BookingSlot] = (
    uuid,
    date,
    int4,
    varchar(255),
    uuid,
    uuid,
    uuid,
  ).tupled.imap({
    case (id, slotDate, slotIndex, status, roomId, bookingId, userId) =>
      BookingSlot(id, slotDate, slotIndex, BookingSlotStatus.fromStringUnsafe(status), roomId, bookingId, userId)
  })(bs => (bs.id, bs.slotDate, bs.slotIndex, bs.status.value, bs.roomId, bs.bookingId, bs.userId))

  // BookingSlotWithU - BookingSlot with [U]ser
  final case class BookingSlotWithU(
      id: UUID,
      slotDate: LocalDate,
      slotIndex: Int,
      status: BookingSlotStatus,
      roomId: UUID,
      bookingId: UUID,
      userId: UUID,
      user: UserWithRole,
  )

  object BookingSlotWithU {
    given Decoder[BookingSlotWithU] = deriveDecoder[BookingSlotWithU]
    given Encoder[BookingSlotWithU] = deriveEncoder[BookingSlotWithU]
  }

}
