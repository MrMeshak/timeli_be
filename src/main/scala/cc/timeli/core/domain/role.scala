package cc.timeli.core.domain

import cats.implicits.*
import cats.syntax.*
import skunk.Codec
import skunk.codec.all.*

import io.circe.{Encoder, Decoder, Json}
import io.circe.generic.semiauto.*

import cc.timeli.core.shared.Permission
import java.util.UUID

object role {

  case class Role(id: UUID, name: String, label: String, mask: BigInt)

  val roleCodec: Codec[Role] = (uuid, varchar(50), varchar(50), text).tupled.imap({
    case (id, role, label, mask) => Role(id, role, label, BigInt(mask))
  })(role => (role.id, role.name, role.label, role.mask.toString))
}
