package cc.timeli.core.shared

import io.circe.{Encoder, Decoder}

object enums {
  enum UserStatus(val value: String) {
    case ACTIVE    extends UserStatus("ACTIVE")
    case PENDING   extends UserStatus("PENDING")
    case SUSPENDED extends UserStatus("SUSPENDED")
  }

  object UserStatus {
    given Decoder[UserStatus] =
      Decoder.decodeString.emap(v => UserStatus.fromString(v).toRight(s"Invalid UserStatus: $v"))
    given Encoder[UserStatus] = Encoder.encodeString.contramap(_.value)

    def fromString(value: String): Option[UserStatus] =
      UserStatus.values.find(_.value == value)

    def fromStringUnsafe(value: String): UserStatus =
      UserStatus.values.find(_.value == value) match
        case Some(status) => status
        case None         => throw new IllegalArgumentException(s"Invalid UserStatus: '$value'")
  }

  enum ThemeColor(val value: String) {
    case ZINC   extends ThemeColor("ZINC")
    case SLATE  extends ThemeColor("SLATE")
    case PURPLE extends ThemeColor("PURPLE")
    case MAROON extends ThemeColor("MAROON")
    case BROWN  extends ThemeColor("BROWN")
    case GOLD   extends ThemeColor("GOLD")
    case GREEN  extends ThemeColor("GREEN")
  }

  object ThemeColor {
    given Decoder[ThemeColor] =
      Decoder.decodeString.emap(v => ThemeColor.fromString(v).toRight(s"Invalid ThemeColor: $v"))
    given Encoder[ThemeColor] = Encoder.encodeString.contramap(_.value)

    def fromString(value: String): Option[ThemeColor] = ThemeColor.values.find(_.value == value)

    def fromStringUnsafe(value: String): ThemeColor =
      ThemeColor.values.find(_.value == value) match
        case Some(color) => color
        case None        => throw new IllegalArgumentException(s"Invalid ThemeColor: '$value'")
  }

  enum BookingSlotStatus(val value: String) {
    case BOOKED    extends BookingSlotStatus("BOOKED")
    case PENDING   extends BookingSlotStatus("PENDING")
    case CANCELLED extends BookingSlotStatus("CANCELLED")
  }

  object BookingSlotStatus {
    given Decoder[BookingSlotStatus] =
      Decoder.decodeString.emap(v => BookingSlotStatus.fromString(v).toRight(s"Invalid BookingSlotStatus"))
    given Encoder[BookingSlotStatus] = Encoder.encodeString.contramap(_.value)

    def fromString(value: String): Option[BookingSlotStatus] = BookingSlotStatus.values.find(_.value == value)
    def fromStringUnsafe(value: String): BookingSlotStatus = BookingSlotStatus.values.find(_.value == value) match {
      case Some(status) => status
      case None         => throw new IllegalArgumentException(s"Invalid BookingSlotStatus")
    }
  }

}
