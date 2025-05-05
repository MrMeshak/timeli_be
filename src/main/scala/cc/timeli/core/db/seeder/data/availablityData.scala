package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.availability.*

val availabilityIds: List[UUID] = List(
  UUID.fromString("06f8b54f-9f68-47ca-acd1-2b5f4fba2f35"),
  UUID.fromString("b675c1a0-f908-4908-87c1-96b5235cf892"),
  UUID.fromString("1c79a721-928e-4628-8701-c2bc72870bc3"),
  UUID.fromString("9908a1d4-0b2c-49c3-8d52-f74856d86042"),
  UUID.fromString("959a129c-1d45-43fc-8a94-a596ae5ebc48"),
  UUID.fromString("2acafe04-7a41-4420-9e69-0e76bae7eca3"),
  UUID.fromString("dab75c39-522e-4261-984d-136999df011c"),
)

val availabilitySeedData: List[Availability] = List(
  Availability(availabilityIds(0), 0, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(1), 1, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(2), 2, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(3), 3, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(4), 4, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(5), 5, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(6), 6, BigInt(1073741823), roomIds(0)),
)
