package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

import cc.timeli.core.domain.roomType.*

object room {

  final case class Room(
      id: UUID,
      name: String,
      description: String,
      capacity: Int,
      defaultPrice: Int,
      slotSize: Int,
      roomTypeId: String,
      locationId: UUID,
  )

  val roomCodec: Codec[Room] = (uuid, varchar(255), text, int4, int4, int4, varchar(255), uuid).tupled.imap({
    case (id, name, description, capacity, defaultPrice, slotSize, roomTypeId, locationId) =>
      Room(id, name, description, capacity, defaultPrice, slotSize, roomTypeId, locationId)
  })(r => (r.id, r.name, r.description, r.capacity, r.defaultPrice, r.slotSize, r.roomTypeId, r.locationId))

  final case class RoomWithRoomType(room: Room, roomType: RoomType)

  val roomWithRoomTypeCodec: Codec[RoomWithRoomType] = (roomCodec, roomTypeCodec).tupled.imap({
    case (room, roomType) => RoomWithRoomType(room, roomType)
  })(rwrt => (rwrt.room, rwrt.roomType))

}
