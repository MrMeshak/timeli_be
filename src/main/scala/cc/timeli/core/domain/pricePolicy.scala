package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID
import java.time.LocalTime

object pricePolicy {

  final case class PricePolicy(
      id: UUID,
      dayOfWeek: Int,
      price: Int,
      mask: BigInt,
      roomId: UUID,
  ) {}

  val pricePolicyCodec: Codec[PricePolicy] =
    (uuid, int4, int4, text, uuid).tupled.imap({
      case (id, dayOfWeek, price, mask, roomId) =>
        PricePolicy(id, dayOfWeek, price, BigInt(mask), roomId)
    })(p => (p.id, p.dayOfWeek, p.price, p.mask.toString, p.roomId))
}
