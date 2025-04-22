package cc.timeli.core.config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class JwtConfig(
    accessTokenSecret: String,
    accessTokenExpTime: Int,
    refreshTokenSecret: String,
    refreshTokenExpTime: Int,
    passwordResetTokenSecret: String,
    passwordResetTokenExpTime: Int,
) derives ConfigReader {}
