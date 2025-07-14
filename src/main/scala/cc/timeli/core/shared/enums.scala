package cc.timeli.core.shared

import io.circe.Encoder

object enums {
  enum UserStatus(val value: String) {
    case ACTIVE    extends UserStatus("ACTIVE")
    case PENDING   extends UserStatus("PENDING")
    case SUSPENDED extends UserStatus("SUSPENDED")
  }

  object UserStatus {
    given Encoder[UserStatus] = Encoder.encodeString.contramap(_.value)

    def fromString(value: String): UserStatus =
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
    def fromString(value: String): ThemeColor =
      ThemeColor.values.find(_.value == value) match
        case Some(color) => color
        case None        => throw new IllegalArgumentException(s"Invalid ThemeColor: '$value")
  }

}
