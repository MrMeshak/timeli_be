package cc.timeli.core.domain

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID
import java.time.LocalTime
import java.time.LocalDate

object availability {

  final case class Availability(
      id: UUID,
      startDate: LocalDate,
      dayOfWeek: Int,
      mask: BigInt,
      roomId: UUID,
  )

  object Availability {
    given Encoder[BigInt]       = Encoder.encodeString.contramap[BigInt](_.toString)
    given Encoder[Availability] = deriveEncoder[Availability]
    given Decoder[Availability] = deriveDecoder[Availability]
  }

  val availabilityCodec: Codec[Availability] =
    (uuid, date, int4, text, uuid).tupled.imap({
      case (id, startDate, dayOfWeek, mask, roomId) => Availability(id, startDate, dayOfWeek, BigInt(mask), roomId)
    })(a => (a.id, a.startDate, a.dayOfWeek, a.mask.toString, a.roomId))

}
