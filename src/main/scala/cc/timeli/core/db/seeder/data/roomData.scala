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
  UUID.fromString("23182706-0b98-410b-8476-872d7c067ce5"),
  UUID.fromString("07c14183-9179-4ad1-b709-a6cd39f1fe04"),
  UUID.fromString("c3387588-e66a-491d-893a-81ca68ed54da"),
  UUID.fromString("9437d75a-5c4c-41e8-98f6-8dd7c61b7fe8"),
  UUID.fromString("dd4bcfa3-0e19-4409-a804-49c322e95afd"),
  UUID.fromString("67ae9e03-7369-4782-aabe-b7fefb7ea7e2"),
  UUID.fromString("c7e62a3c-cd53-44eb-b874-ca71f2fe1641"),
)

val roomSeedData: List[Room] = List(
  Room(roomIds(0), "Court 1", "C1", "B-C1", "", 4, 2200, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(1), "Court 2", "C2", "B-C2", "", 4, 1550, 60, roomTypeIds(1), locationIds(0)),
  Room(roomIds(2), "Court 3", "C3", "B-C3", "", 4, 2400, 15, roomTypeIds(2), locationIds(0)),
  Room(roomIds(3), "Court 4", "C4", "B-C4", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(4), "Court 5", "C5", "B-C5", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(5), "Court 6", "C6", "B-C6", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(6), "Court 7", "C7", "B-C7", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(7), "Court 8", "C8", "B-C8", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(8), "Court 9", "C9", "B-C9", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(9), "Court 10", "C10", "B-C10", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
  Room(roomIds(10), "Court 11", "C11", "B-C11", "", 4, 2400, 30, roomTypeIds(0), locationIds(0)),
)
