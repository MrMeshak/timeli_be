package cc.timeli.core.db.seeder.data

import java.util.UUID
import cc.timeli.core.domain.location.*

val locationIds: List[UUID] = List(
  UUID.fromString("41854c3e-a8ce-4896-9101-ff63e4436bec"),
  UUID.fromString("51b3340e-6848-481c-8c6f-3bddb20b56ae"),
)

val locationSeedData: List[Location] = List(
  Location(
    id = locationIds(0),
    name = "Melbourne Badminton Center",
    description = "",
    street = "6/16 Joseph St",
    city = "Blackburn North",
    state = "Vic",
    country = "Australia",
    postCode = "3130",
  ),
  Location(
    id = locationIds(1),
    name = "Sports Point",
    description = "",
    street = "64-66 McArthurs Road",
    city = "Altona North",
    state = "VIC",
    country = "Australia",
    postCode = "3025",
  ),
)
