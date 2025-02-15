

final class AuthAlgebra[F[_]] {
  def fetchUserByEmail(fetchUserByEmailDto: FetchUserByEmailDto)
  def createUser(createUserDto: CreateUserDto)
}


