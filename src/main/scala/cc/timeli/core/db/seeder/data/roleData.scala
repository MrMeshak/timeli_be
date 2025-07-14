package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.role.*
import cc.timeli.core.shared.Permission
import cc.timeli.core.shared.enums.*

val roleIds: List[UUID] = List(
  UUID.fromString("52f39797-a765-43e8-9473-accf7f34a6a7"),
  UUID.fromString("f6861433-709e-4fcb-a1ba-1eb79d733f5b"),
  UUID.fromString("498741e4-d4b5-4d99-8c03-625e4fdc7ddf"),
  UUID.fromString("9b67cdec-59fd-4a38-82c2-98491ee1ed8f"),
  UUID.fromString("85f253e8-c62d-4b4f-ae19-fcae527ce39e"),
)

val roleSeedData: List[Role] = List(
  Role(roleIds(0), "USER", "User", ThemeColor.SLATE, BigInt(0)),
  Role(roleIds(1), "ADMIN", "Admin", ThemeColor.MAROON, Permission.READ_USER_TABLE.mask),
  Role(roleIds(2), "SUPERADMIN", "Super Admin", ThemeColor.MAROON, Permission.READ_USER_TABLE.mask),
  Role(roleIds(3), "MEMBER", "Member", ThemeColor.PURPLE, BigInt(0)),
  Role(roleIds(4), "COACH", "Coach", ThemeColor.GOLD, BigInt(0)),
)
