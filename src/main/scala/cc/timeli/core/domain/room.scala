package cc.timeli.core.domain

import io.circe.Encoder
import io.circe.generic.semiauto.*

import cats.implicits.*
import cats.syntax.*
import skunk.codec.all.*
import skunk.circe.codec.all.*
import skunk.{Codec => SkunkCodec}

import java.util.UUID

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
}
