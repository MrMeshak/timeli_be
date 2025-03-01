package cc.timeli.core.errors

sealed trait BaseError extends Product with Serializable {
  def message: String
}

object baseErrors {

  case class InvalidCredentialsError(override val message: String = "Invalid credentials") extends BaseError {}
  case class AlreadyExistsError(override val message: String)                              extends BaseError {}
  case class NotFoundError(override val message: String)                                   extends BaseError {}
}
