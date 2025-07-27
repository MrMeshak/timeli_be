package cc.timeli.core.domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.{Codec => SkunkCodec}
import skunk.codec.all.*
import skunk.circe.codec.all.*

import java.util.UUID
import java.time.LocalDate

object pricePolicy {

  final case class PricePolicy(
      id: UUID,
      startDate: LocalDate,
      policy: List[List[Int]],
      roomId: UUID,
  ) {}

  object PricePolicy {
    given Encoder[BigInt]      = Encoder.encodeString.contramap[BigInt](_.toString)
    given Decoder[PricePolicy] = deriveDecoder[PricePolicy]
    given Encoder[PricePolicy] = deriveEncoder[PricePolicy]
  }

  val pricePolicyCodec: SkunkCodec[PricePolicy] =
    (uuid, date, jsonb[List[List[Int]]], uuid).tupled.imap({
      case (id, startDate, policy, roomId) =>
        PricePolicy(id, startDate, policy, roomId)
    })(pp => (pp.id, pp.startDate, pp.policy, pp.roomId))
}
