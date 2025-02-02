package cc.timeli.core.errors

sealed trait BaseError {
  def message: String
}

object baseErrors {}
