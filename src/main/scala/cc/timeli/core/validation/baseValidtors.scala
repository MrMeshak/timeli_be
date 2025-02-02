package cc.timeli.core.validation

import cats.*
import cats.implicits.*
import cats.data.*
import cats.data.Validated.*
import scala.util.{Try, Failure, Success}

import java.net.URL

object baseValidators {
  case class EmptyField(fieldName: String)   extends ValidationFailure(s"'${fieldName}' is required")
  case class InvalidUrl(fieldName: String)   extends ValidationFailure(s"'${fieldName}' is an invalid url")
  case class InvalidEmail(fieldName: String) extends ValidationFailure(s"'${fieldName}' is an invalid Email")
  case class InvalidPassword(fieldName: String)
      extends ValidationFailure(
        s"'${fieldName}' requires a minimum of - 8 characters - symbol - uppercase - lowercase - number",
      )
  case class InvalidPhone(fieldName: String) extends ValidationFailure(s"'${fieldName}' is an invalid phone number")

  def validateRequired[A](field: A, fieldName: String)(
      predicate: A => Boolean,
  ): ValidatedNel[ValidationFailure, A] = {
    if (predicate(field)) field.validNel
    else EmptyField(fieldName).invalidNel
  }

  def validateUrl(field: String, fieldName: String) = {
    Try(URL(field).toURI()) match {
      case Success(_) => field.validNel
      case Failure(_) => InvalidUrl(fieldName).invalidNel
    }
  }

  def validateEmail(field: String, fieldName: String) = {
    """^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$""".r.findFirstMatchIn(field) match {
      case Some(_) => field.validNel
      case None    => InvalidEmail(fieldName).invalidNel
    }
  }

  def validatePassword(field: String, fieldName: String) = {
    """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$""".r.findFirstMatchIn(field) match {
      case Some(_) => field.validNel
      case None    => InvalidPassword(fieldName).invalidNel
    }
  }

  def validatePhone(field: String, fieldName: String) = {
    """^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$""".r.findFirstMatchIn(field) match {
      case Some(_) => field.validNel
      case None    => InvalidPhone(fieldName).invalidNel
    }
  }
}
