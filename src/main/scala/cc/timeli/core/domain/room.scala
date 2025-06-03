package cc.timeli.core.domain

import io.circe.Encoder
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec, Decoder => SkunkDecoder}

import java.util.UUID

import cc.timeli.core.domain.roomType.*
import cc.timeli.core.domain.location.*
import cc.timeli.core.domain.availability.*
import cc.timeli.core.domain.pricePolicy.*
import cc.timeli.core.domain.slot.*

object room {

  final case class Room(
      id: UUID,
      name: String,
      displayName: String,
      roomCode: String,
      description: String,
      capacity: Int,
      defaultPrice: Int,
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
          defaultPrice,
          slotSize,
          roomTypeId,
          locationId,
        ) =>
      Room(id, name, displayName, roomCode, description, capacity, defaultPrice, slotSize, roomTypeId, locationId)
  })(r =>
    (
      r.id,
      r.name,
      r.displayName,
      r.roomCode,
      r.description,
      r.capacity,
      r.defaultPrice,
      r.slotSize,
      r.roomTypeId,
      r.locationId,
    ),
  )

  final case class RoomWithDetails(
      id: UUID,
      name: String,
      displayName: String,
      roomCode: String,
      description: String,
      capacity: Int,
      defaultPrice: Int,
      slotSize: Int,
      roomType: RoomType,
      location: Location,
      availability: List[Availability],
      pricePolicies: List[PricePolicy],
      slots: List[Slot],
  )

  val roomWithDetailsDecoder: SkunkDecoder[RoomWithDetails] = (
    uuid,
    varchar(255),
    varchar(6),
    varchar(255),
    text,
    int4,
    int4,
    int4,
    jsonb[RoomType],
    jsonb[Location],
    jsonb[List[Availability]],
    jsonb[List[PricePolicy]],
    jsonb[List[Slot]],
  ).tupled.map({
    case (
          id,
          name,
          displayName,
          roomCode,
          description,
          capacity,
          defaultPrice,
          slotSize,
          roomType,
          location,
          availability,
          pricePolicies,
          slots,
        ) =>
      RoomWithDetails(
        id,
        name,
        displayName,
        roomCode,
        description,
        capacity,
        defaultPrice,
        slotSize,
        roomType,
        location,
        availability,
        pricePolicies,
        slots,
      )
  })

  object RoomWithDetails {
    given Encoder[RoomWithDetails] = deriveEncoder[RoomWithDetails]
  }
}
