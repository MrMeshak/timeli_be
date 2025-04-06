package cc.timeli.core.db.seeder.data

import java.util.UUID
import cc.timeli.core.domain.slot.*
import cc.timeli.core.domain.booking.*

val slotIds: List[UUID] = List(
  UUID.fromString("a8eef9c4-cbd7-4e37-ac0a-8ddcd2acc4b3"),
  UUID.fromString("73c3cecd-3716-41f6-abd8-997e90d89929"),
  UUID.fromString("285b487d-f6fb-4cdc-ab55-a368021237ff"),
)

val slotSeedData: List[Slot] = List(
  Slot(slotIds(0), SlotStatus.BOOKED, BigInt(2).pow(20), scheduleIds(0), bookingIds(0)),
  Slot(slotIds(1), SlotStatus.BOOKED, BigInt(2).pow(22), scheduleIds(0), bookingIds(0)),
  Slot(slotIds(2), SlotStatus.BOOKED, BigInt(2).pow(23), scheduleIds(0), bookingIds(0)),
)
