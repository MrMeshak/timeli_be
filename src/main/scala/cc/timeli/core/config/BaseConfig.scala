package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class BaseConfig(
    timeliFeUrl: String,
) derives ConfigReader {}
