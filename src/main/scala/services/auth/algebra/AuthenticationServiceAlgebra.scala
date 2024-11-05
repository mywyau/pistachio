package services.auth.algebra

import models.users.*


trait AuthenticationServiceAlgebra[F[_]] {

  def loginUser(request: UserLoginRequest): F[Either[String, UserProfile]]

  def authUser(token: String): F[Option[UserProfile]]

  def authorize(userAuth: UserAuth[F], requiredRole: Role): F[Either[String, UserAuth[F]]]

  def updateUserRole(requestingUserId: String, targetUserId: String, newRole: Role): F[Either[String, UserProfile]]
}
