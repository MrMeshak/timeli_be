package cc.timeli.algebra.booking

import io.circe.{Decoder, Encoder}
import io.circe.syntax.*
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

  object BookingMatrixData {
    given Encoder[BookingMatrixData] = deriveEncoder[BookingMatrixData]
  }

  case class BookingMatrix(
      rooms: List[BookingRoom],
  )

  object BookingMatrix {
    given Encoder[BookingMatrix] = deriveEncoder[BookingMatrix]
  }

  case class BookingRoom(
      id: UUID,
      name: String,
      displayName: String,
      slotSize: Int,
      capacity: Int,
      slots: List[BookingSlot],
  )

  object BookingRoom {
    given Encoder[BookingRoom] = deriveEncoder[BookingRoom]
  }

  case class BookingSlot(
      status: BookingSlotStatus,
      price: Int,
      startMins: Int,
  )

  object BookingSlot {
    given Encoder[BookingSlot] = deriveEncoder[BookingSlot]
  }

  enum BookingSlotStatus(val id: String) {
    case BOOKED      extends BookingSlotStatus("BOOKED")
    case AVAILABLE   extends BookingSlotStatus("AVAILABLE")
    case UNAVAILABLE extends BookingSlotStatus("UNAVAILABLE")
    case PENDING     extends BookingSlotStatus("PENDING")
    case PERMANENT   extends BookingSlotStatus("PERMANENT")
  }

  object BookingSlotStatus {
    given Encoder[BookingSlotStatus] = Encoder[String].contramap(_.id)
  }

}
