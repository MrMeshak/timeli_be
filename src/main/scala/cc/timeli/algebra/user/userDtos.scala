package cc.timeli.algebra.user
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import java.util.UUID

object userDtos {
  case class MeDto(id: UUID)
  case class MeData(id: UUID, email: String, firstName: String, lastName: String)
  object MeData { given Encoder[MeData] = deriveEncoder[MeData] }
}
