package cc.timeli.algebra.user

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

import cc.timeli.core.shared.enums.*

import java.util.UUID
import cc.timeli.core.domain.role.Role
import cc.timeli.core.domain.user.UserWithRole

object userDtos {
  case class MeDto(id: UUID)
  case class MeData(id: UUID, email: String, firstName: String, lastName: String, status: UserStatus)
  object MeData { given Encoder[MeData] = deriveEncoder[MeData] }

  case class MetaData(roles: List[Role])

  case class TableDto(
      pageIndex: Int,
      pageSize: Int,
      searchTerm: Option[String],
      fRole: Option[String],
      fstatus: Option[UserStatus],
  )

  case class TableData(
      rowCount: Int,
      rowData: List[UserWithRole],
  )

}
