package cc.timeli.core.db.seeder.data

import at.favre.lib.crypto.bcrypt.BCrypt

import java.util.UUID

import cc.timeli.core.config.SeederConfig
import cc.timeli.core.domain.user.*
import cc.timeli.core.shared.enums.*

val userIds: List[UUID] = List(
  UUID.fromString("eb19b801-eba7-4af2-be90-b239b8190649"),
  UUID.fromString("bfd9d4e6-a128-461b-a9f9-a0555840a2b4"),
  UUID.fromString("ced14da6-0889-409a-9519-7b83215b6439"),
  UUID.fromString("4941bf75-4fcd-4c39-a85a-d2275374ba9b"),
)

def userSeedData(seederConfig: SeederConfig) = List(
  User(
    userIds(0),
    seederConfig.userEmail,
    BCrypt.withDefaults().hashToString(12, seederConfig.userPassword.toCharArray()),
    "Meshak",
    "Bain",
    UserStatus.ACTIVE,
  ),
  User(
    userIds(1),
    "ahrinishin8@mail.ru",
    BCrypt.withDefaults().hashToString(12, seederConfig.userPassword.toCharArray()),
    "Ashton",
    "Hrinishin",
    UserStatus.ACTIVE,
  ),
  User(
    userIds(2),
    "ghicklingbottom9@studiopress.com",
    BCrypt.withDefaults().hashToString(12, seederConfig.userPassword.toCharArray()),
    "Gabey",
    "Hicklingbottom",
    UserStatus.ACTIVE,
  ),
  User(
    userIds(3),
    "cmaggiore2@dell.com",
    BCrypt.withDefaults().hashToString(12, seederConfig.userPassword.toCharArray()),
    "Cilka",
    "Maggiore",
    UserStatus.ACTIVE,
  ),
)
