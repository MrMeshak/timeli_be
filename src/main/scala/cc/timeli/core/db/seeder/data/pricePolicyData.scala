package cc.timeli.core.db.seeder.data

import java.util.UUID
import java.time.LocalTime

import cc.timeli.core.domain.pricePolicy.*

val pricePolicyIds: List[UUID] = List(
  UUID.fromString("e06527df-2933-4f53-9a53-bcafdb8841c8"),
  UUID.fromString("1b4245c0-6915-4c8e-9392-5492c22fed36"),
)

val pricePolicyData: List[PricePolicy] = List(
  PricePolicy(pricePolicyIds(0), 0, 2500, BigInt(786432), roomIds(0)),
  PricePolicy(pricePolicyIds(1), 2, 1550, BigInt(786432), roomIds(0)),
)
