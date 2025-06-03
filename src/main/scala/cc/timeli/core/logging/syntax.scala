package cc.timeli.core.logging

import cats.MonadError
import cats.data.EitherT
import cats.implicits.*
import org.typelevel.log4cats.Logger

object syntax {
  extension [F[_], E, A](fa: F[A])(using me: MonadError[F, E], logger: Logger[F]) {
    def log(success: A => String, error: E => String): F[A] = fa.attemptTap({
      case Left(e)  => logger.error(error(e))
      case Right(a) => logger.info(success(a))
    })

    def logError(error: E => String): F[A] = fa.attemptTap({
      case Left(e)  => logger.error(error(e))
      case Right(_) => ().pure[F]
    })
  }

  extension [F[_], L, R, E](et: EitherT[F, L, R])(using me: MonadError[F, E], logger: Logger[F]) {
    def log(left: L => String, right: R => String, error: E => String): EitherT[F, L, R] = {
      EitherT(
        et.value.log(
          {
            case Left(l)  => left(l)
            case Right(r) => right(r)
          },
          e => error(e),
        ),
      )
    }

    def logError(error: E => String): EitherT[F, L, R] = {
      EitherT(
        et.value.logError(e => error(e)),
      )
    }
  }

}
