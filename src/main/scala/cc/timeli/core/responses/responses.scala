package cc.timeli.core.responses

import io.circe.Encoder
import io.circe.generic.semiauto.*

object responses {

  case class FailureRes(error: String, message: String, details: List[FailureRes])
  object FailureRes { given Encoder[FailureRes] = deriveEncoder[FailureRes] }
}
