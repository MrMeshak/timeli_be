package cc.timeli.app

import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*

import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.*
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.server.Router
import org.typelevel.log4cats.{Logger, LoggerFactory}

import cc.timeli.core.validation.syntax.*
import cc.timeli.core.validation.bookingValidators.given
import cc.timeli.core.logging.syntax.*
import cc.timeli.middleware.{AuthMP, AuthContext}
import cc.timeli.core.responses.responses.FailureRes

import cc.timeli.algebra.booking.BookingAlgebra
import cc.timeli.algebra.booking.bookingDtos.*

class BookingRoutes[F[_]: Concurrent: LoggerFactory](bookingAlgebra: BookingAlgebra[F]) extends HttpValidationDsl[F] {
  given logger1: Logger[F] = LoggerFactory.getLogger

  private val bookingContextRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "context" =>
      bookingAlgebra.bookingContext.value
        .flatMap({
          case Right(bookingContextData) => Ok(bookingContextData)
          case Left(error)               => BadRequest(FailureRes(error.name, error.message, List()))
        })
  }

  private val bookingMatrixRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "matrix" =>
      req.validate[BookingMatrixDto](bookingMatrixDto =>
        bookingAlgebra
          .bookingMatrix(bookingMatrixDto)
          .value
          .flatMap({
            case Right(bookingMatrixData) => Ok(bookingMatrixData)
            case Left(error) =>
              BadRequest(FailureRes(error.name, error.message, List()))
          }),
      )
  }

  val routes: HttpRoutes[F] = Router(
    "booking" -> (bookingContextRoute <+> bookingMatrixRoute),
  )
}

object BookingRoutes {
  def apply[F[_]: Concurrent: LoggerFactory](bookingAlgebra: BookingAlgebra[F]) =
    new BookingRoutes[F](bookingAlgebra)
}
