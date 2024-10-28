package service.users

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.{User, UserLoginRequest, UserRegistrationRequest, Wanderer}
import repositories.UserRepositoryAlgebra
import services.AuthenticationService
import weaver.SimpleIOSuite

import org.passay.*
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Base64

import java.time.LocalDateTime

// The test suite using tagless final style
object AuthenticationServiceSpec extends SimpleIOSuite {

  // Helper method to create a test user
  def testUser(username: String): User =
    User(
      username = username,
      password_hash = "hashed_password",
      first_name = "John",
      last_name = "Doe",
      contact_number = "123456789",
      email = "john.doe@example.com",
      role = Wanderer,
      created_at = LocalDateTime.now()
    )

  // Define the tagless final mock for UserRepositoryAlgebra using Cats Ref to simulate state
  case class MockUserRepository(ref: Ref[IO, List[User]]) extends UserRepositoryAlgebra[IO] {
    override def findByUsername(username: String): IO[Option[User]] =
      ref.get.map(_.find(_.username == username))

    override def findByContactNumber(contactNumber: String): IO[Option[User]] =
      ref.get.map(_.find(_.contact_number == contactNumber))

    override def findByEmail(email: String): IO[Option[User]] =
      ref.get.map(_.find(_.email == email))

    override def createUser(user: User): IO[Int] =
      ref.modify(users => (user :: users, 1)) // Simulate user creation by adding to the list
  }

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[User]): IO[MockUserRepository] =
    Ref.of[IO, List[User]](initialUsers).map(MockUserRepository.apply)

  // Unit tests for AuthenticationService

  test(".registerUser() should successfully register a new user") {

    val newUserRequest =
      UserRegistrationRequest(
        username = "newuser",
        password = "password123",
        first_name = "Jane",
        last_name = "Doe",
        contact_number = "123456789",
        email = "jane.doe@example.com",
        role = Wanderer,
      )

    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      authService = AuthenticationService[IO](mockRepo)
      result <- authService.registerUser(newUserRequest)
    } yield expect(result.isValid)
  }

  test(".registerUser() should fail if the username already exists") {
    val existingUser = testUser("existinguser")
    val newUserRequest = UserRegistrationRequest(
      username = "existinguser",
      password = "password123",
      first_name = "Jane",
      last_name = "Doe",
      contact_number = "123456789",
      email = "jane.doe@example.com",
      role = Wanderer,
    )

    for {
      mockRepo <- createMockRepo(List(existingUser)) // User already exists
      authService = AuthenticationService[IO](mockRepo)
      result <- authService.registerUser(newUserRequest)
    } yield expect(result.isInvalid) // Expect failure due to existing username
  }

  test(".loginUser() should successfully authenticate with correct password") {

    // Simple password hashing function using SHA-256
    def hashPassword(password: String): String = {
      val digest = MessageDigest.getInstance("SHA-256")
      val hashBytes = digest.digest(password.getBytes("UTF-8"))
      Base64.getEncoder.encodeToString(hashBytes)
    }

    val existingUser = testUser("validuser").copy(password_hash = hashPassword("password123"))
    val loginRequest = UserLoginRequest(
      username = "validuser",
      password = "password123"
    )

    for {
      mockRepo <- createMockRepo(List(existingUser)) // User exists with correct password
      authService = AuthenticationService[IO](mockRepo)
      result <- authService.loginUser(loginRequest)
    } yield expect(result == Right(existingUser))
  }

  test(".loginUser() should fail with incorrect password") {
    val existingUser = testUser("validuser").copy(password_hash = "password123")
    val loginRequest = UserLoginRequest(
      username = "validuser",
      password = "wrongpassword"
    )

    for {
      mockRepo <- createMockRepo(List(existingUser)) // User exists, but password is incorrect
      authService = AuthenticationService[IO](mockRepo)
      result <- authService.loginUser(loginRequest)
    } yield expect(result.isLeft) // Expect failure due to incorrect password
  }

  test(".loginUser() should fail if username not found") {
    val loginRequest = UserLoginRequest(
      username = "nonexistentuser",
      password = "password123"
    )

    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      authService = AuthenticationService[IO](mockRepo)
      result <- authService.loginUser(loginRequest)
    } yield expect(result.isLeft) // Expect failure due to username not found
  }
}
