package cc.timeli.algebra.auth

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import org.http4s.ResponseCookie

object authDtos {

  case class LoginDto(email: String, password: String)
  object LoginDto { given Decoder[LoginDto] = deriveDecoder[LoginDto] }

  case class SignupDto(email: String, password: String, firstName: String, lastName: String)
  object SignupDto { given Decoder[SignupDto] = deriveDecoder[SignupDto] }

  case class LoginData(accessTokenCookie: ResponseCookie, refreshTokenCookie: ResponseCookie)
}
