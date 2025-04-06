package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object slot {

  final case class Slot(
      id: UUID,
      status: SlotStatus,
      slotMask: BigInt,
      scheduleId: UUID,
      bookingId: UUID,
  )

  object Slot {}

  val slotCodec: Codec[Slot] = (uuid, varchar(255), text, uuid, uuid).tupled.imap({
    case (id, status, slotMask, scheduleId, bookingId) =>
      Slot(id, SlotStatus.valueOf(status), BigInt(slotMask), scheduleId, bookingId)
  })(s => (s.id, s.status.toString, s.slotMask.toString, s.scheduleId, s.bookingId))

  enum SlotStatus {
    case BOOKED, PENDING
  }

}
