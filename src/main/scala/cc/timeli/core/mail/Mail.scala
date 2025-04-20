package cc.timeli.core.mail

import cats.effect.Async
import cats.implicits.*
import fs2.io.net.Network
import com.comcast.ip4s.{Host, Port, SocketAddress}
import pencil.{Client => MailClient}
import pencil.data.Credentials
import pencil.data.UsernameType.Username
import pencil.data.PasswordType.Password
import org.typelevel.log4cats.{Logger, LoggerFactory}

import cc.timeli.core.config.MailConfig

object Mail {
  def mailer[F[_]: Async: Network: LoggerFactory](mailConfig: MailConfig): F[MailClient[F]] = {
    given logger: Logger[F] = LoggerFactory[F].getLogger()
    for {
      tls <- Network[F].tlsContext.system
      client = MailClient[F](
        SocketAddress(mailConfig.host, mailConfig.port),
        Some(Credentials(Username(mailConfig.username), Password(mailConfig.password))),
      )(tls, logger)
    } yield client
  }
}
