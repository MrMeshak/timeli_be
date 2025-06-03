package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.booking.*

val bookingIds: List[UUID] = List(
  UUID.fromString("560d1d90-c9cb-43d0-b1bd-4f89dfe33967"),
  UUID.fromString("a2255606-2b22-4e0c-be4d-885b4f2f42b7"),
)

val bookingSeedData: List[Booking] = List(
  Booking(bookingIds(0), BookingStatus.COMPLETED, userIds(0)),
  Booking(bookingIds(1), BookingStatus.COMPLETED, userIds(0)),
)
