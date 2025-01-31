package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port

final case class ServerConfig(host: Host, port: Port) derives ConfigReader {}

object ServerConfig {
  given hostReader: ConfigReader[Host] = ConfigReader[String].emap(hostStr =>
    Host
      .fromString(hostStr)
      .toRight(CannotConvert(hostStr, Host.getClass().toString(), "invalid host string")),
  )

  given portReader: ConfigReader[Port] = ConfigReader[Int].emap(portInt =>
    Port
      .fromInt(portInt)
      .toRight(CannotConvert(portInt.toString(), Port.getClass.toString(), "invalid port string")),
  )
}
