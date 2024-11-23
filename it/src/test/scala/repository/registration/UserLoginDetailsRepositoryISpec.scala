package repository.registration

import cats.effect.{IO, Resource}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import models.users.adts.Wanderer
import models.wanderer.wanderer_profile.profile.UserLoginDetails
import repositories.user_profile.UserLoginDetailsRepositoryImpl
import shared.TransactorResource
import weaver.{GlobalRead, IOSuite, ResourceTag}

import java.time.LocalDateTime

class UserLoginDetailsRepositoryISpec(global: GlobalRead) extends IOSuite {

  type Res = UserLoginDetailsRepositoryImpl[IO] // needed for the shared resource, set the type of 'Res' here to be used in tests

  private def initializeSchema(transactor: TransactorResource): Resource[IO, Unit] =
    Resource.eval(
      sql"""
        CREATE TABLE IF NOT EXISTS user_login_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          username VARCHAR(255) NOT NULL,
          password_hash TEXT NOT NULL,
          email VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
      """.update.run.transact(transactor.xa).void *>
        sql"TRUNCATE TABLE user_login_details RESTART IDENTITY".update.run.transact(transactor.xa).void
    )

  def testUserLoginDetails(id: Option[Int], userId: String, username: String, email: String) =
    UserLoginDetails(
      id = id,
      userId = userId,
      username = username,
      passwordHash = "hashedpassword",
      email = email,
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  private def seedTestUsers(userLoginDetailsRepo: UserLoginDetailsRepositoryImpl[IO]): IO[Unit] = {
    val users = List(
      testUserLoginDetails(Some(1), "user_id_1", "mikey1", "mikey1@gmail.com"),
      testUserLoginDetails(Some(2), "user_id_2", "mikey2", "mikey2@gmail.com"),
      testUserLoginDetails(Some(3), "user_id_3", "mikey3", "mikey3@gmail.com")
    )
    users.traverse(userLoginDetailsRepo.createUserLoginDetails).void
  }

  def sharedResource: Resource[IO, UserLoginDetailsRepositoryImpl[IO]] = {
    val setup = for {
      transactor <- global.getOrFailR[TransactorResource]()
      userLoginDetailsRepo = new UserLoginDetailsRepositoryImpl[IO](transactor.xa)
      _ <- initializeSchema(transactor) // Create the table if it doesn't exist and reset the id
      _ <- Resource.eval(seedTestUsers(userLoginDetailsRepo)) // seed the table with user data
    } yield userLoginDetailsRepo

    setup
  }


  def sampleUser(
                  id: Option[Int],
                  userId: String,
                  username: String,
                  contactNumber: String,
                  email: String
                ) =
    UserLoginDetails(
      id = id,
      userId = userId,
      username = username,
      passwordHash = "hashedpassword",
      email = email,
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".createUser() - should insert a new user") { userLoginDetailsRepo =>

    val user = sampleUser(Some(4), userId = "user_id_4", username = "mikey4", contactNumber = "07402205071", email = "mikey4@gmail.com")

    for {
      result <- userLoginDetailsRepo.createUserLoginDetails(user)
      userOpt <- userLoginDetailsRepo.findByUsername("mikey4")
    } yield expect(result == 1) and expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return the user if username exists") { userLoginDetailsRepo =>

    val user = testUserLoginDetails(Some(1), "user_id_1", "mikey1", "mikey1@gmail.com")

    for {
      userOpt <- userLoginDetailsRepo.findByUsername("mikey1")
    } yield expect(userOpt == Some(user))
  }

  test(".findByEmail() - should return the user if email exists") { userLoginDetailsRepo =>

    val user = testUserLoginDetails(Some(1), "user_id_1", "mikey1", "mikey1@gmail.com")

    for {
      userOpt <- userLoginDetailsRepo.findByEmail("mikey1@gmail.com")
    } yield expect(userOpt.contains(user))
  }

  test(".findByUsername() - should return None if username does not exist") { userLoginDetailsRepo =>

    for {
      userOpt <- userLoginDetailsRepo.findByUsername("nonexistentuser")
    } yield expect(userOpt.isEmpty)
  }
}