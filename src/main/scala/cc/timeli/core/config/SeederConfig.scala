package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class SeederConfig(
    userEmail: String,
    userPassword: String,
) derives ConfigReader {}
