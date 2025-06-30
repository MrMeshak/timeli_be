package cc.timeli.core.db.seeder.data

import java.util.UUID
import java.time.LocalDate

import cc.timeli.core.domain.availability.*

val availabilityIds: List[UUID] = List(
  UUID.fromString("06f8b54f-9f68-47ca-acd1-2b5f4fba2f35"),
  UUID.fromString("b675c1a0-f908-4908-87c1-96b5235cf892"),
  UUID.fromString("1c79a721-928e-4628-8701-c2bc72870bc3"),
  UUID.fromString("9908a1d4-0b2c-49c3-8d52-f74856d86042"),
  UUID.fromString("959a129c-1d45-43fc-8a94-a596ae5ebc48"),
  UUID.fromString("2acafe04-7a41-4420-9e69-0e76bae7eca3"),
  UUID.fromString("dab75c39-522e-4261-984d-136999df011c"),
  UUID.fromString("462597b3-100a-42f5-8dba-7dc24c1c7fc6"),
  UUID.fromString("45ee6e36-f820-475b-b62f-6b5fbcd023ff"),
  UUID.fromString("96fdbd09-c4c3-4f97-adde-dfff7e52d5f2"),
  UUID.fromString("07c9a35b-649f-46b2-bda3-497ccb6b3d38"),
  UUID.fromString("88f345f1-5553-4b1f-b30e-ee540915a278"),
  UUID.fromString("ebad7a4e-c5fa-43ab-92d1-70ae2c21aadf"),
  UUID.fromString("65b59ee6-fa94-43aa-be70-6a82df2fa43f"),
  UUID.fromString("9133aeeb-ad19-4be0-a942-b1df0129678f"),
  UUID.fromString("6503dc5f-f3c7-41d5-805c-c6d3cc6d1d2e"),
  UUID.fromString("f84d76b0-6a2d-4134-9fd2-db2aa0d8929b"),
  UUID.fromString("3847f911-7be8-4a9e-a7df-4033776aeb18"),
  UUID.fromString("d01e350a-8283-42c2-9830-d25717e3ea48"),
  UUID.fromString("359c9211-c830-4d8b-9bf8-14fa58aeaa63"),
  UUID.fromString("4d373840-9651-40c9-88fa-0155fed08358"),
)

val availabilitySeedData: List[Availability] = List(
  Availability(availabilityIds(0), LocalDate.parse("2025-06-01"), 0, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(1), LocalDate.parse("2025-06-01"), 1, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(2), LocalDate.parse("2025-06-01"), 2, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(3), LocalDate.parse("2025-06-01"), 3, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(4), LocalDate.parse("2025-06-01"), 4, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(5), LocalDate.parse("2025-06-01"), 5, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(6), LocalDate.parse("2025-06-01"), 6, BigInt(1073741823), roomIds(0)),
  Availability(availabilityIds(7), LocalDate.parse("2025-06-01"), 0, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(8), LocalDate.parse("2025-06-01"), 1, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(9), LocalDate.parse("2025-06-01"), 2, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(10), LocalDate.parse("2025-06-01"), 3, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(11), LocalDate.parse("2025-06-01"), 4, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(12), LocalDate.parse("2025-06-01"), 5, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(13), LocalDate.parse("2025-06-01"), 6, BigInt(1073741823), roomIds(1)),
  Availability(availabilityIds(14), LocalDate.parse("2025-06-01"), 0, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(15), LocalDate.parse("2025-06-01"), 1, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(16), LocalDate.parse("2025-06-01"), 2, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(17), LocalDate.parse("2025-06-01"), 3, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(18), LocalDate.parse("2025-06-01"), 4, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(19), LocalDate.parse("2025-06-01"), 5, BigInt(1073741823), roomIds(4)),
  Availability(availabilityIds(20), LocalDate.parse("2025-06-01"), 6, BigInt(1073741823), roomIds(4)),
)
