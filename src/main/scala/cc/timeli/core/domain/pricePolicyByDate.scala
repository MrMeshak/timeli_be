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

object pricePolicyByDate {

  final case class PricePolicyByDate(
      id: UUID,
      activeDate: LocalDate,
      policy: List[Int],
      roomId: UUID,
  )

  object PricePolicyByDate {
    given Encoder[BigInt]            = Encoder.encodeString.contramap[BigInt](_.toString)
    given Decoder[PricePolicyByDate] = deriveDecoder[PricePolicyByDate]
    given Encoder[PricePolicyByDate] = deriveEncoder[PricePolicyByDate]
  }

  val pricePolicyByDateCodec: SkunkCodec[PricePolicyByDate] =
    (uuid, date, jsonb[List[Int]], uuid).tupled.imap({
      case (id, activeDate, policy, roomId) => PricePolicyByDate(id, activeDate, policy, roomId)
    })(ppbd => (ppbd.id, ppbd.activeDate, ppbd.policy, ppbd.roomId))
}
