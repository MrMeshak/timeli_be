package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object booking {

  final case class Booking(
      id: UUID,
      status: BookingStatus,
      userId: UUID,
  )

  val bookingCodec: Codec[Booking] = (uuid, varchar(255), uuid).tupled.imap({
    case (id, status, userId) => Booking(id, BookingStatus.valueOf(status), userId)
  })(b => (b.id, b.status.toString, b.userId))

  enum BookingStatus {
    case PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED
  }
}
