package services.auth.mocks

import cats.effect.{IO, Ref}
import cats.syntax.all.*
import models.users.*
import models.users.adts.{Role, Wanderer}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}
import repositories.RefreshTokenRepositoryAlgebra
import repositories.users.UserProfileRepositoryAlgebra
import services.auth.algebra.TokenServiceAlgebra
import services.auth.{InvalidToken, TokenStatus}

import java.time.{Instant, LocalDateTime}
import scala.concurrent.duration.*

class MockTokenService extends TokenServiceAlgebra[IO] {

  override def createToken(user: UserProfile, expiration: Instant): IO[String] =
    IO.pure(s"accessToken-${user.userId}")

  override def validateToken(token: String): IO[Either[TokenStatus, String]] =
    if (token.contains("valid")) IO.pure(Right("userId")) else IO.pure(Left(InvalidToken))

  override def invalidateToken(token: String): IO[Boolean] = ???

  override def refreshAccessToken(refreshToken: String): IO[Option[String]] = ???
}


class MockRefreshTokenRepository(ref: Ref[IO, Map[String, (String, Instant)]]) extends RefreshTokenRepositoryAlgebra[IO] {

  override def storeToken(token: String, userId: String, expiration: Instant): IO[Unit] =
    ref.update(tokens => tokens + (token -> (userId, expiration)))

  override def findUserIdByToken(token: String): IO[Option[String]] =
    ref.get.map(_.get(token).filter { case (_, expiration) => expiration.isAfter(Instant.now) }.map(_._1))

  override def revokeToken(token: String): IO[Long] =
    ref.modify { tokens =>
      if (tokens.contains(token)) (tokens - token, 1L)
      else (tokens, 0L)
    }
}

// Companion object for easy creation
object MockRefreshTokenRepository {
  def create(): IO[MockRefreshTokenRepository] =
    Ref.of[IO, Map[String, (String, Instant)]](Map.empty).map(new MockRefreshTokenRepository(_))
}

class MockUserRepository extends UserProfileRepositoryAlgebra[IO] {

  private val users =
    Map("username" ->
      UserProfile(
        userId = "userId",
        UserLoginDetails(
          id = Some(1),
          userId = "userId",
          username = "username",
          passwordHash = "hashed_password",
          email = "john@example.com",
          role = Wanderer,
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
        firstName = "John",
        lastName = "Doe",
        UserAddress(
          userId = "userId",
          street = Some("fake street 1"),
          city = Some("fake city 1"),
          country = Some("UK"),
          county = Some("County 1"),
          postcode = Some("CF3 3NJ"),
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        ),
        contactNumber = "07402205071",
        email = "john@example.com",
        role = Wanderer,
        createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
        updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      )
    )

  override def findByUsername(username: String): IO[Option[UserProfile]] = IO.pure(users.get(username))

  override def createUserProfile(user: UserProfile): IO[Int] = ???

  override def findByContactNumber(contactNumber: String): IO[Option[UserProfile]] = ???

  override def findByEmail(email: String): IO[Option[UserProfile]] = ???

  override def findByUserId(userId: String): IO[Option[UserProfile]] = ???

  override def updateUserRole(userId: String, desiredRole: Role): IO[Option[UserProfile]] = ???
}
