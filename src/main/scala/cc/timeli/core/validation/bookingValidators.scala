package cc.timeli.core.validation

import cats.data.{ValidatedNel}

import cats.implicits.*

import cc.timeli.algebra.booking.bookingDtos.*
import cc.timeli.core.validation.baseValidators.*

object bookingValidators {

  given BookingMatrixDtoValidator: Validator[BookingMatrixDto] with {
    override def validate(value: BookingMatrixDto): ValidatedNel[ValidationFailure, BookingMatrixDto] = {
      val BookingMatrixDto(roomTypeId, date) = value;
      (
        validateRequired(roomTypeId, "roomTypeId")(_.nonEmpty),
        date.validNel,
      ).mapN(BookingMatrixDto.apply)
    }
  }
}
