package cc.timeli.core.validation

import cats.data.ValidatedNel
import cats.implicits.*

import cc.timeli.algebra.room.roomDtos.*
import cc.timeli.core.validation.baseValidators.*

object roomValidators {

  given roomGridDtoValidator: Validator[RoomGridDto] with {
    override def validate(value: RoomGridDto): ValidatedNel[ValidationFailure, RoomGridDto] = {
      val RoomGridDto(date) = value
      (
        date.validNel
      ).map(RoomGridDto.apply)

    }
  }
}
