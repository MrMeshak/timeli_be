package cc.timeli.algebra.auth

object AuthDto {

  case class LoginDto(email: String, password: String)
  case class SignupDto(email: String, password: String, firstName: String, lastName: String)

  case class LoginData(authToken: String, refreshToken: String)
}
