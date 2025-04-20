package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

import org.http4s.Uri

final case class CorsConfig(
    host: String,
    port: Int,
) derives ConfigReader {}
