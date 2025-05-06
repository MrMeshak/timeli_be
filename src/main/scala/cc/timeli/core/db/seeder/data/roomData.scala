package cc.timeli.core.db.seeder.data

import java.util.UUID
import cc.timeli.core.domain.room.Room
import cc.timeli.core.domain.roomType.*
import cc.timeli.core.db.seeder.data.locationIds

val roomIds: List[UUID] = List(
  UUID.fromString("6fe2d5aa-3f39-47c1-a7a9-1171578d8f7a"),
  UUID.fromString("6ead87b5-9f5c-4158-ba4c-9f70f002b919"),
  UUID.fromString("e48a7389-a6ec-4c08-9828-85da0b90145a"),
)

val roomSeedData: List[Room] = List(
  Room(roomIds(0), "Court 1", "", 4, BigDecimal(22.00), 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(1), "Court 2", "", 4, BigDecimal(15.50), 60, roomTypeIds(1), locationIds(0)),
  Room(roomIds(2), "Court 3", "", 4, BigDecimal(24.00), 15, roomTypeIds(2), locationIds(0)),
)
