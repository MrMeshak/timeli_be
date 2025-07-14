package cc.timeli.core.guard

import io.circe.generic.semiauto.*
import org.http4s.circe.CirceEntityCodec.*

import cats.*
import cats.implicits.*
import org.typelevel.log4cats.LoggerFactory
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import cc.timeli.middleware.AuthContext
import cc.timeli.core.responses.responses.FailureRes
import cc.timeli.core.shared.Permission
object syntax {

  trait HttpGuardDsl[F[_]: MonadThrow: LoggerFactory] extends Http4sDsl[F] {

    extension (authContext: AuthContext)
      def guard(permission: Permission)(onPass: => F[Response[F]]) = {
        if ((authContext.permissions & permission.mask) != 0) {
          onPass
        } else {
          Forbidden(
            FailureRes("Forbidden", "insufficent permissions", List()),
          )
        }
      }
  }
}
