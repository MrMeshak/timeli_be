package cc.timeli.core.validation

import cats.data.ValidatedNel

trait ValidationFailure {
  val message: String
  val fieldName: String
}

trait Validator[A] {
  def validate(value: A): ValidatedNel[ValidationFailure, A]
}
