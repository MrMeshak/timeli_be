package cc.timeli.core.validation

import cats.data.ValidatedNel
import cats.implicits.*

import cc.timeli.algebra.user.userDtos.*
import cc.timeli.core.validation.baseValidators.*

object userValidators {

  given userTableDtoValidator: Validator[UserTableDto] with {
    override def validate(value: UserTableDto): ValidatedNel[ValidationFailure, UserTableDto] = {
      val UserTableDto(pageIndex, pageSize, searchTerm, fRole, fStatus) = value;
      (
        validateCondition[Int](pageIndex, "pageIndex")(_ >= 0, "must be a positive integer"),
        validateCondition[Int](pageSize, "pageSize")(v => v >= 0 && v <= 100, "must be an integer between 0 and 100"),
        validateCondition[Option[String]](searchTerm, "searchTerm")(
          _.forall(v => v.nonEmpty && v.length < 100),
          "must be between 0 and 100 characters",
        ),
        validateCondition[Option[String]](fRole, "fRole")(
          _.forall(v => v.nonEmpty && v.length < 100),
          "must be between 0 and 100 characters",
        ),
        fStatus.validNel,
      ).mapN(UserTableDto.apply)
    }
  }
}
