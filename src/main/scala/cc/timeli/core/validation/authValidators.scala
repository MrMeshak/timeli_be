package cc.timeli.core.validation

import cats.data.ValidatedNel
import cats.implicits.*

import cc.timeli.algebra.auth.authDtos.*
import cc.timeli.core.validation.baseValidators.*

object authValidators {

  given loginDtoValidator: Validator[LoginDto] with {
    override def validate(value: LoginDto): ValidatedNel[ValidationFailure, LoginDto] = {
      val LoginDto(email, password) = value;
      (
        validateEmail(email, "email"),
        validateRequired(password, "password")(_.nonEmpty),
      ).mapN(LoginDto.apply)
    }
  }

  given signupDtoValidator: Validator[SignupDto] with {
    override def validate(value: SignupDto): ValidatedNel[ValidationFailure, SignupDto] = {
      val SignupDto(email, password, firstName, lastName) = value

      (
        validateEmail(email, "email"),
        validatePassword(password, "password"),
        validateRequired(firstName, "firstname")(_.nonEmpty),
        validateRequired(lastName, "lastname")(_.nonEmpty),
      ).mapN(SignupDto.apply)
    }
  }

  given passwordForgotDtoValidator: Validator[PasswordForgotDto] with {
    override def validate(value: PasswordForgotDto): ValidatedNel[ValidationFailure, PasswordForgotDto] = {
      val PasswordForgotDto(email) = value

      (
        validateEmail(email, "email")
      ).map(PasswordForgotDto.apply)
    }
  }

  given passwordResetDtoValidator: Validator[PasswordResetDto] with {
    override def validate(value: PasswordResetDto): ValidatedNel[ValidationFailure, PasswordResetDto] = {
      val PasswordResetDto(token, password) = value;
      (
        validateRequired(token, "token")(_.nonEmpty),
        validatePassword(password, "password"),
      ).mapN(PasswordResetDto.apply)

    }
  }
}
