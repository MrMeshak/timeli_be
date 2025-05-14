package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.roomType.*

val roomTypeIds: List[String] = List(
  "badminton",
  "basketball",
  "tennis",
)

val roomTypeSeedData: List[RoomType] = List(
  RoomType(roomTypeIds(0), "Badminton"),
  RoomType(roomTypeIds(1), "Basketball"),
  RoomType(roomTypeIds(2), "Tennis"),
)
