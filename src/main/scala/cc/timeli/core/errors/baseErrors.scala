package cc.timeli.core.errors

sealed trait BaseError extends Product with Serializable {
  def message: String
  def name: String = this.getClass().getSimpleName().replace("$", "")
}

object baseErrors {
  case class InvalidInputError(override val message: String)                               extends BaseError {}
  case class InvalidCredentialsError(override val message: String = "Invalid credentials") extends BaseError {}
  case class InvalidTokenError(override val message: String)                               extends BaseError {}
  case class AlreadyExistsError(override val message: String)                              extends BaseError {}
  case class NotFoundError(override val message: String)                                   extends BaseError {}
  case class RateLimitedError(override val message: String)                                extends BaseError {}
  case class UserStatusSuspendedError(override val message: String = "User is suspended")  extends BaseError {}
}
