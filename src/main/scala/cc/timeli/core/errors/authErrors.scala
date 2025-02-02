package cc.timeli.core.errors

object authErrors {

  sealed trait AuthError {
    val message: String
  }
  case class InvalidCredentials(override val message: String) extends AuthError {}
}
