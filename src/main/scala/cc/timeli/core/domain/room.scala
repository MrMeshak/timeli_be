package cc.timeli.core.domain

import io.circe.Encoder
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec}

import java.util.UUID
import cc.timeli.core.domain.availabilityPolicy.AvailabilityPolicy
import cc.timeli.core.domain.availabilityPolicyByDate.AvailabilityPolicyByDate
import cc.timeli.core.domain.pricePolicy.PricePolicy
import cc.timeli.core.domain.pricePolicyByDate.PricePolicyByDate
import cc.timeli.core.domain.roomType.RoomType
import cc.timeli.core.domain.bookingSlot.BookingSlotWithU

object room {

  final case class Room(
      id: UUID,
      name: String,
      displayName: String,
      roomCode: String,
      description: String,
      capacity: Int,
      slotSize: Int,
      roomTypeId: String,
      locationId: UUID,
  ) {}

  val roomCodec: SkunkCodec[Room] = (
    uuid,
    varchar(255),
    varchar(6),
    varchar(255),
    text,
    int4,
    int4,
    varchar(255),
    uuid,
  ).tupled.imap({
    case (
          id,
          name,
          displayName,
          roomCode,
          description,
          capacity,
          slotSize,
          roomTypeId,
          locationId,
        ) =>
      Room(id, name, displayName, roomCode, description, capacity, slotSize, roomTypeId, locationId)
  })(r =>
    (
      r.id,
      r.name,
      r.displayName,
      r.roomCode,
      r.description,
      r.capacity,
      r.slotSize,
      r.roomTypeId,
      r.locationId,
    ),
  )

  // RoomWithTAP - Room With Room[T]ype & [A]vailabilityPolicy & [P]ricePolicy & Booking[S]lots
  final case class RoomWithTAP_S(
      id: UUID,
      name: String,
      displayName: String,
      roomCode: String,
      description: String,
      capacity: Int,
      slotSize: Int,
      roomTypeId: String,
      locationId: UUID,
      roomType: RoomType,
      availabilityPolicy: AvailabilityPolicy,
      availabilityPolicyByDate: Option[AvailabilityPolicyByDate],
      pricePolicy: PricePolicy,
      pricePolicyByDate: Option[PricePolicyByDate],
      bookingSlots: List[BookingSlotWithU],
  )

  val roomWithTAP_SCodec: SkunkCodec[RoomWithTAP_S] =
    (
      uuid,
      varchar(255),
      varchar(6),
      varchar(255),
      text,
      int4,
      int4,
      varchar(255),
      uuid,
      jsonb[RoomType],
      jsonb[AvailabilityPolicy],
      jsonb[AvailabilityPolicyByDate].opt,
      jsonb[PricePolicy],
      jsonb[PricePolicyByDate].opt,
      jsonb[List[BookingSlotWithU]],
    ).tupled.imap({
      case (
            id,
            name,
            displayName,
            roomCode,
            description,
            capacity,
            slotSize,
            roomTypeId,
            locationId,
            roomType,
            availabilityPolicy,
            availabilityPolicyByDate,
            pricePolicy,
            pricePolicyByDate,
            bookingSlots,
          ) =>
        RoomWithTAP_S(
          id,
          name,
          displayName,
          roomCode,
          description,
          capacity,
          slotSize,
          roomTypeId,
          locationId,
          roomType,
          availabilityPolicy,
          availabilityPolicyByDate,
          pricePolicy,
          pricePolicyByDate,
          bookingSlots,
        )
    })(r =>
      (
        r.id,
        r.name,
        r.displayName,
        r.roomCode,
        r.description,
        r.capacity,
        r.slotSize,
        r.roomTypeId,
        r.locationId,
        r.roomType,
        r.availabilityPolicy,
        r.availabilityPolicyByDate,
        r.pricePolicy,
        r.pricePolicyByDate,
        r.bookingSlots,
      ),
    )
}
