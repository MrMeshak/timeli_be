package cc.timeli.core.errors

object authErrors {

  sealed trait AuthError {
    def message: String
  }
  case class InvalidCredentials(message: String) extends AuthError {}
}
