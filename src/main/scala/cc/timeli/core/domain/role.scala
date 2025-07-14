package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import io.circe.{Encoder, Decoder, Json}
import io.circe.generic.semiauto.*

import cc.timeli.core.shared.Permission
import cc.timeli.core.shared.enums.*
import java.util.UUID

object role {

  case class Role(id: UUID, name: String, label: String, color: ThemeColor, mask: BigInt)

  val roleCodec: Codec[Role] = (uuid, varchar(50), varchar(50), varchar(50), text).tupled.imap({
    case (id, role, label, color, mask) => Role(id, role, label, ThemeColor.fromString(color), BigInt(mask))
  })(role => (role.id, role.name, role.label, role.color.value, role.mask.toString))
}
