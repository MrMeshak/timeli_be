package cc.timeli.core.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID
import java.time.LocalDate

object pricePolicy {

  final case class PricePolicy(
      id: UUID,
      startDate: LocalDate,
      dayOfWeek: Int,
      price: Int,
      mask: BigInt,
      roomId: UUID,
  ) {}

  object PricePolicy {
    given Encoder[BigInt]      = Encoder.encodeString.contramap[BigInt](_.toString)
    given Decoder[PricePolicy] = deriveDecoder[PricePolicy]
    given Encoder[PricePolicy] = deriveEncoder[PricePolicy]
  }

  val pricePolicyCodec: Codec[PricePolicy] =
    (uuid, date, int4, int4, text, uuid).tupled.imap({
      case (id, startDate, dayOfWeek, price, mask, roomId) =>
        PricePolicy(id, startDate, dayOfWeek, price, BigInt(mask), roomId)
    })(p => (p.id, p.startDate, p.dayOfWeek, p.price, p.mask.toString, p.roomId))
}
