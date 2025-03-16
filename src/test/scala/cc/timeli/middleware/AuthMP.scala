import collection.mutable.Stack
import org.scalatest.funspec.AnyFunSpec

class AuthMPSpec extends AnyFunSpec {

  describe("middleware - AuthMP") {

    describe("authContextK") {
      describe("when accessToken is missing") {
        ignore("should return OptionT[F,None]") {}
      }

      describe("when the accessToken (valid)") {
        ignore("should return OptionT[F,authContext] with AuthContext(userId = userId, setTokens=false)") {}
      }

      describe("when the accessToken (expired) and the refreshToken is missing") {
        ignore("should return OptionT[F,None]") {}
      }

      describe("when the accessToken (expired) and the refreshToken (invalid)") {
        ignore("should return OptionT[F,None]") {}
      }

      describe(
        "when the accessToken (expired) and refreshToken (valid) but accessToken and refreshToken are not a pair",
      ) {
        ignore("should return Option[F,None]") {}
      }

      describe(
        "when the accessToken(expired) and refreshToken (valid) and stored RefreshToken mismatch and cachedRefreshToken mismatch",
      ) {}

      describe(
        "when the accessToken (expired) and refreshToken (valid) and stored RefreshToken match",
      ) {}

      describe(
        "when the accessToken(expired) and refreshToken(valid) and stored refreshToken mismatch and cachedRefreshToken match",
      ) {}

    }

  }
}
