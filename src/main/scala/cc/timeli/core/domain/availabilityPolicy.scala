package cc.timeli.core.domain

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.{Codec => SkunkCodec, Decoder => SkunkDecoder}
import skunk.codec.all.*
import skunk.circe.codec.all.*

import java.util.UUID
import java.time.LocalTime
import java.time.LocalDate

object availabilityPolicy {

  final case class AvailabilityPolicy(
      id: UUID,
      startDate: LocalDate,
      policy: List[BigInt],
      roomId: UUID,
  )

  object AvailabilityPolicy {
    given Encoder[BigInt]             = Encoder.encodeString.contramap[BigInt](_.toString)
    given Encoder[AvailabilityPolicy] = deriveEncoder[AvailabilityPolicy]
    given Decoder[AvailabilityPolicy] = deriveDecoder[AvailabilityPolicy]
  }

  val availabilityPolicyCodec: SkunkCodec[AvailabilityPolicy] =
    (uuid, date, jsonb[List[String]], uuid).tupled.imap({
      case (id, startDate, policy, roomId) =>
        AvailabilityPolicy(id, startDate, policy.map(BigInt(_)), roomId)
    })(ap => (ap.id, ap.startDate, ap.policy.map(_.toString), ap.roomId))
}
