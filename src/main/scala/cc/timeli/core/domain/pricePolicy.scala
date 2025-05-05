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
      startTime: LocalTime,
      endTime: LocalTime,
      price: BigDecimal,
      roomId: UUID,
  ) {}

  val pricePolicyCodec: Codec[PricePolicy] =
    (uuid, int4, time, time, numeric(10, 2), uuid).tupled.imap({
      case (id, dayOfWeek, startTime, endTime, price, roomId) =>
        PricePolicy(id, dayOfWeek, startTime, endTime, price, roomId)
    })(p => (p.id, p.dayOfWeek, p.startTime, p.endTime, p.price, p.roomId))
}
