package cc.timeli.algebra.room

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import java.util.UUID
import java.time.LocalDate

import cc.timeli.core.domain.room.RoomWithTAP_S

object roomDtos {
  final case class RoomGridDto(
      date: LocalDate,
  )

  final case class RoomGridData(
      rooms: List[RoomWithTAP_S],
  )
}
