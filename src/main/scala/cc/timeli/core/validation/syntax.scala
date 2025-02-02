package cc.timeli.core.validation

import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*

import cats.*
import cats.implicits.*
import cats.data.Validated.*
import cats.data.ValidatedNel
import org.typelevel.log4cats.LoggerFactory
import org.http4s.*
import org.http4s.dsl.Http4sDsl

import cc.timeli.core.validation.Validator

object syntax {

  def validateEntity[A](entity: A)(using validator: Validator[A]): ValidatedNel[ValidationFailure, A] = {
    validator.validate(entity)
  }

  trait HttpValidationDsl[F[_]: MonadThrow: LoggerFactory] extends Http4sDsl[F] {
    extension (req: Request[F])
      def validate[A: Validator](serverLogicIfValid: A => F[Response[F]])(using EntityDecoder[F, A]) = {
        req
          .as[A]
          .map(validateEntity)
          .flatMap({
            case Valid(entity)   => serverLogicIfValid(entity)
            case Invalid(errors) => BadRequest(errors.toList.map(_.message))
          })
      }
  }
}
