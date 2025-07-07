package cc.timeli.core.db.seeder.data

import java.util.UUID

import cc.timeli.core.domain.role.*
import cc.timeli.core.shared.Permission

val roleIds: List[UUID] = List(
  UUID.fromString("52f39797-a765-43e8-9473-accf7f34a6a7"),
  UUID.fromString("f6861433-709e-4fcb-a1ba-1eb79d733f5b"),
  UUID.fromString("498741e4-d4b5-4d99-8c03-625e4fdc7ddf"),
)

val roleSeedData: List[Role] = List(
  Role(roleIds(0), "USER", Permission.READ_USER_INFO.mask),
  Role(roleIds(1), "ADMIN", Permission.READ_USER_INFO.mask),
  Role(roleIds(2), "SUPERADMIN", Permission.READ_USER_INFO.mask),
)
