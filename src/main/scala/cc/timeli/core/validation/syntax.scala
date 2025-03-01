package cc.timeli.core.validation

import io.circe.generic.semiauto.*
import org.http4s.circe.CirceEntityCodec.*

import cats.*
import cats.implicits.*
import cats.data.Validated.*
import cats.data.ValidatedNel
import org.typelevel.log4cats.LoggerFactory
import org.http4s.*
import org.http4s.dsl.Http4sDsl

import cc.timeli.core.validation.Validator
import cc.timeli.core.responses.responses.FailureRes

object syntax {

  def validateEntity[A](entity: A)(using validator: Validator[A]): ValidatedNel[ValidationFailure, A] = {
    validator.validate(entity)
  }

  trait HttpValidationDsl[F[_]: MonadThrow: LoggerFactory] extends Http4sDsl[F] {
    val logger = LoggerFactory.getLogger()

    extension (req: Request[F])
      def validate[A: Validator](serverLogicIfValid: A => F[Response[F]])(using EntityDecoder[F, A]) = {
        req
          .as[A]
          .map(validateEntity)
          .flatMap({
            case Valid(entity) => serverLogicIfValid(entity)
            case Invalid(errors) => {
              logger.info("errors in validation")
              UnprocessableEntity(
                FailureRes(
                  error = "Invalid Payload",
                  message = "one or more fields are invalid",
                  details = errors.toList.map(error => FailureRes(error.fieldName, error.message, List())),
                ),
              )
            }
          })
          .recoverWith({
            case InvalidMessageBodyFailure(_, cause) =>
              UnprocessableEntity(
                FailureRes(
                  "Invalid Payload Structure",
                  s"payload structure mismatch with expected payload ${cause.map(c => ": " + c.getMessage()).orEmpty}",
                  List(),
                ),
              )
            // case error =>
            //   logger.info(error.getMessage())
            //   UnprocessableEntity(
            //     FailureRes(
            //       "Parsing JSON Error",
            //       "unexpected error while passing JSON body, probably a JSON syntax error",
            //       List(),
            //     ),
            //   )
          })
      }
  }
}
