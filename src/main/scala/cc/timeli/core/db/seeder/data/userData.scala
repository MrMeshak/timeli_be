package cc.timeli.core.db.seeder.data

import at.favre.lib.crypto.bcrypt.BCrypt

import java.util.UUID

import cc.timeli.core.config.SeederConfig
import cc.timeli.core.domain.user.*

val userIds: List[UUID] = List(
  UUID.fromString("eb19b801-eba7-4af2-be90-b239b8190649"),
)

def userSeedData(seederConfig: SeederConfig) = List(
  User(
    userIds(0),
    seederConfig.userEmail,
    BCrypt.withDefaults().hashToString(12, seederConfig.userPassword.toCharArray()),
    "Meshak",
    "Bain",
  ),
)
