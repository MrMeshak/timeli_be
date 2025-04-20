package cc.timeli.algebra.auth

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import org.http4s.ResponseCookie

import java.util.UUID

object authDtos {

  case class LoginDto(email: String, password: String)
  object LoginDto { given Decoder[LoginDto] = deriveDecoder[LoginDto] }

  case class LoginData(accessTokenCookie: ResponseCookie, refreshTokenCookie: ResponseCookie)

  case class SignupDto(email: String, password: String, firstName: String, lastName: String)
  object SignupDto { given Decoder[SignupDto] = deriveDecoder[SignupDto] }

  case class LogoutDto(userId: UUID)
  case class LogoutData(accessTokenCookieEmpty: ResponseCookie, refreshTokenCookieEmpty: ResponseCookie)

  case class PasswordResetRequestDto(email: String)

}
