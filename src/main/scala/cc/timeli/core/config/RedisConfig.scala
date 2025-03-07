package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class RedisConfig(
    url: String,
) derives ConfigReader {}
