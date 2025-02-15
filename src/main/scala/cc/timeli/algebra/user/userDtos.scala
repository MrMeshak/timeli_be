package cc.timeli.algebra.user

import io.circe.{Decoder}
import io.circe.generic.semiauto.*

object userDtos {
  case class FetchUserByEmailDto(email: String)
  object FetchUserByEmailDto { given Decoder[FetchUserByEmailDto] = deriveDecoder[FetchUserByEmailDto] }

  case class CreateUserDto(
      email: String,
      password: String,
      firstName: String,
      lastName: String,
  )
  object CreateUserDto { given Decoder[CreateUserDto] = deriveDecoder[CreateUserDto] }
}
