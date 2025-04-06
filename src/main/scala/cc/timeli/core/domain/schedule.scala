package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID
import java.time.{LocalDate, LocalTime}

object schedule {

  final case class Schedule(
      id: UUID,
      date: LocalDate,
      slotSize: Int,
      scheduleMask: BigInt,
      roomId: UUID,
  )

  val scheduleCodec: Codec[Schedule] = (uuid, date, int4, text, uuid).tupled.imap({
    case (id, date, slotSize, scheduleMask, roomId) =>
      Schedule(id, date, slotSize, BigInt(scheduleMask), roomId)
  })(s => (s.id, s.date, s.slotSize, s.scheduleMask.toString(), s.roomId))
}
