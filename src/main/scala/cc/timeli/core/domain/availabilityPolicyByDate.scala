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

object availabilityPolicyByDate {

  final case class AvailabilityPolicyByDate(
      id: UUID,
      activeDate: LocalDate,
      policy: BigInt,
      roomId: UUID,
  )

  val availabilityPolicyByDateCodec: SkunkCodec[AvailabilityPolicyByDate] =
    (uuid, date, text, uuid).tupled.imap({
      case (id, activeDate, policy, roomId) => AvailabilityPolicyByDate(id, activeDate, BigInt(policy), roomId)
    })(apbd => (apbd.id, apbd.activeDate, apbd.policy.toString, apbd.roomId))
}
