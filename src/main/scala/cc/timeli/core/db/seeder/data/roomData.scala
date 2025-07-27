package cc.timeli.core.db.seeder.data

import java.util.UUID
import cc.timeli.core.domain.room.Room
import cc.timeli.core.domain.roomType.*
import cc.timeli.core.db.seeder.data.locationIds

val roomIds: List[UUID] = List(
  UUID.fromString("6fe2d5aa-3f39-47c1-a7a9-1171578d8f7a"),
  UUID.fromString("6ead87b5-9f5c-4158-ba4c-9f70f002b919"),
  UUID.fromString("e48a7389-a6ec-4c08-9828-85da0b90145a"),
  UUID.fromString("34d3f93e-cca1-49bc-84e0-7e720a2a29d3"),
)

val roomSeedData: List[Room] = List(
  Room(roomIds(0), "Court 1", "C1", "B-C1", "", 4, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(1), "Court 2", "C2", "B-C2", "", 4, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(2), "Court 3", "C3", "B-C3", "", 4, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(3), "Court 4", "C4", "B-C4", "", 4, 30, roomTypeIds(0), locationIds(0)),
)
