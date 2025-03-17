package cc.timeli.algebra.user
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import java.util.UUID

object userDtos {
  case class UserInfoDto(id: UUID)
  case class UserInfoData(id: UUID, email: String, firstName: String, lastName: String)
  object UserInfoData { given Encoder[UserInfoData] = deriveEncoder[UserInfoData] }
}
