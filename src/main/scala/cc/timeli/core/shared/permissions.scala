package cc.timeli.core.shared

enum Permission(val mask: BigInt) {
  // General

  // User
  case READ_USER_INFO extends Permission(BigInt(2).pow(10))

}
