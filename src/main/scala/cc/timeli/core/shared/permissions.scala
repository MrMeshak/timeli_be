package cc.timeli.core.shared

enum Permission(val mask: BigInt) {
  // General

  // User
  case READ_USER_META  extends Permission(BigInt(1) << 20)
  case READ_USER_TABLE extends Permission(BigInt(1) << 21)

  // Room

  // Booking

}
