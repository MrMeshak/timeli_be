package cc.timeli.algebra.booking

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import java.util.UUID
import java.time.LocalDate

object bookingDtos {
  case class BookingMatrixDto(
      roomTypeId: String,
      date: LocalDate,
  )
  object BookingMatrixDto {
    given Decoder[BookingMatrixDto] = deriveDecoder[BookingMatrixDto]
    given Encoder[BookingMatrixDto] = deriveEncoder[BookingMatrixDto]
  }

  case class BookingMatrixData(
      bookingMatrix: BookingMatrix,
  )

  case class BookingMatrix(
      timeInterval: BookingTimeInterval,
      rooms: List[BookingRoom],
  )

  case class BookingTimeInterval(
      startHr: Int,
      endHr: Int,
  )

  case class BookingRoom(
      id: UUID,
      name: String,
      displayName: String,
      slotSize: Int,
      capacity: Int,
      slots: List[BookingSlot],
  )

  case class BookingSlot(
      status: String,
      price: Int,
  )
}
