package cc.timeli.core.db.seeder.data

import java.util.UUID
import java.time.LocalTime

import cc.timeli.core.domain.pricePolicy.*

val pricePolicyIds: List[UUID] = List(
  UUID.fromString("e06527df-2933-4f53-9a53-bcafdb8841c8"),
  UUID.fromString("1b4245c0-6915-4c8e-9392-5492c22fed36"),
  UUID.fromString("b594fb61-c4b5-46fa-9da4-604f7b12da44"),
  UUID.fromString("c448afdd-ecc0-4017-bf09-cf7a3666c1f4"),
  UUID.fromString("a5b8c4b4-4140-4a66-8b8b-db9df4df882b"),
  UUID.fromString("6a93d86c-f6c0-4d19-83e0-a5a8fe1fb037"),
  UUID.fromString("b4c94489-8954-4cdb-8f3a-1b7138110061"),
)

val pricePolicyData: List[PricePolicy] = List(
  PricePolicy(pricePolicyIds(0), 0, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(32.50), roomIds(0)),
  PricePolicy(pricePolicyIds(1), 1, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(24.50), roomIds(0)),
  PricePolicy(pricePolicyIds(2), 2, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(24.50), roomIds(0)),
  PricePolicy(pricePolicyIds(3), 3, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(24.50), roomIds(0)),
  PricePolicy(pricePolicyIds(4), 4, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(24.50), roomIds(0)),
  PricePolicy(pricePolicyIds(5), 5, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(24.50), roomIds(0)),
  PricePolicy(pricePolicyIds(6), 6, LocalTime.of(9, 0, 0), LocalTime.of(23, 59, 59), BigDecimal(32.50), roomIds(0)),
)
