package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.roomType.*

val roomTypeIds: List[UUID] = List(
  UUID.fromString("26e97a93-6c09-4ee0-9bc6-6abf2dfd7fb3"),
  UUID.fromString("9460bc38-436d-4082-84e1-c04341c548df"),
  UUID.fromString("92304e15-7ee3-4b33-bcbb-9a655508d53f"),
)

val roomTypeSeedData: List[RoomType] = List(
  RoomType(roomTypeIds(0), "Badminton"),
  RoomType(roomTypeIds(1), "BasketBall"),
  RoomType(roomTypeIds(2), "Tennis"),
)
