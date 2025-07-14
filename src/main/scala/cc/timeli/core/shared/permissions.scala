package cc.timeli.core.shared

enum Permission(val mask: BigInt) {
  // General

  // User
  case READ_USER_ME    extends Permission(BigInt(1) << 1)
  case READ_USER_TABLE extends Permission(BigInt(1) << 20)

}
