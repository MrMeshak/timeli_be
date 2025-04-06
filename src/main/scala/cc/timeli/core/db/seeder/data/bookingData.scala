package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.booking.*

val bookingIds: List[UUID] = List(
  UUID.fromString("560d1d90-c9cb-43d0-b1bd-4f89dfe33967"),
)

val bookingSeedData: List[Booking] = List(
  Booking(bookingIds(0), BookingStatus.COMPLETED, userIds(0)),
)
