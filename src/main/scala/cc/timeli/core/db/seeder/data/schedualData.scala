package cc.timeli.core.db.seeder.data

import java.util.UUID
import java.time.LocalDate

import cc.timeli.core.domain.schedule.Schedule

val scheduleIds: List[UUID] = List(
  UUID.fromString("776dbb8f-2cfe-4e1a-b176-6045f0a98de0"),
  UUID.fromString("83707d21-0a8c-4825-8f4a-805ee7de21ee"),
  UUID.fromString("1fe4d299-8313-4485-8b44-1f3dce694792"),
)

val scheduleSeedData: List[Schedule] = List(
  Schedule(scheduleIds(0), LocalDate.parse("2025-05-01"), 30, BigInt(1073741823), roomIds(0)),
  Schedule(scheduleIds(1), LocalDate.parse("2025-05-02"), 30, BigInt(1073741823), roomIds(0)),
  Schedule(scheduleIds(2), LocalDate.parse("2025-05-03"), 30, BigInt(1073741823), roomIds(0)),
)
