package cc.timeli.core.db.seeder.data

import java.util.UUID
import java.time.LocalDate

import cc.timeli.core.domain.bookingSlot.*
import cc.timeli.core.shared.enums.*

val bookingSlotIds: List[UUID] = List(
  UUID.fromString("4a4e48a3-a804-47c6-bff5-5021cc30d2ce"),
  UUID.fromString("a9560027-0f44-4450-8c7b-efd05e3b20ed"),
  UUID.fromString("cfcdc1d8-c55c-4309-851b-ed6aa385663c"),
  UUID.fromString("7d80c931-7156-4afb-a154-75c9866ee331"),
)

val bookingSlotSeedData: List[BookingSlot] = List(
  BookingSlot(
    bookingSlotIds(0),
    LocalDate.parse("2025-06-01"),
    20,
    BookingSlotStatus.BOOKED,
    roomIds(0),
    bookingIds(0),
    userIds(0),
  ),
  BookingSlot(
    bookingSlotIds(1),
    LocalDate.parse("2025-06-01"),
    21,
    BookingSlotStatus.BOOKED,
    roomIds(0),
    bookingIds(0),
    userIds(0),
  ),
  BookingSlot(
    bookingSlotIds(2),
    LocalDate.parse("2025-06-01"),
    25,
    BookingSlotStatus.BOOKED,
    roomIds(0),
    bookingIds(1),
    userIds(0),
  ),
  BookingSlot(
    bookingSlotIds(3),
    LocalDate.parse("2025-06-01"),
    27,
    BookingSlotStatus.BOOKED,
    roomIds(0),
    bookingIds(1),
    userIds(0),
  ),
)
