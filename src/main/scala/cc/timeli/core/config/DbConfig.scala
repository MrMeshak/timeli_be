package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class DbConfig(
    host: String,
    port: Int,
    username: String,
    password: String,
    database: String,
) derives ConfigReader {}
