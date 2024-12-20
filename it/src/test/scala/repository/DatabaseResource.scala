package repository

import cats.effect.{IO, Resource}
import configuration.BaseAppConfig
import doobie.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import shared.TransactorResource
import weaver.{GlobalResource, GlobalWrite}

import scala.concurrent.ExecutionContext

object DatabaseResource extends GlobalResource with BaseAppConfig {

  private def fetchSchemaQuery: Fragment =
    sql"""
      SELECT column_name, data_type, is_nullable
      FROM information_schema.columns
      WHERE table_name = 'user_login_details'
    """

  private def printSchema(xa: Transactor[IO]): Resource[IO, Unit] =
    Resource.eval(
      fetchSchemaQuery.query[(String, String, String)]
        .to[List]
        .transact(xa)
        .flatMap { schema =>
          IO {
            println("Table Schema for 'user_login_details':")
            schema.foreach { case (name, typ, nullable) =>
              println(s"Column: $name, Type: $typ, Nullable: $nullable")
            }
          }
        }
    )

  private def testInsertQuery: Update0 =
    sql"""
      INSERT INTO user_login_details (
        user_id,
        username,
        password_hash,
        email,
        role,
        created_at,
        updated_at
      ) VALUES (
        'test_user_id',
        'test_user',
        'hashed_password',
        'test@example.com',
        'Wanderer',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
      )
    """.update

  private def testInsert(xa: Transactor[IO]): Resource[IO, Unit] =
    Resource.eval(
      testInsertQuery.run.transact(xa).attempt.flatMap {
        case Right(_) => IO(println("Test insert succeeded"))
        case Left(e) => IO(println(s"Test insert failed: ${e.getMessage}"))
      }
    )

  def executionContextResource: Resource[IO, ExecutionContext] =
    ExecutionContexts.fixedThreadPool(4)

  def transactorResource(host: String, port: Int, ce: ExecutionContext): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = s"jdbc:postgresql://$host:$port/shared_test_db",
      user = sys.env.getOrElse("TEST_DB_USER", "shared_user"),
      pass = sys.env.getOrElse("TEST_DB_PASS", "share"),
      connectEC = ce
    )

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      appConfig <- configResource
      postgresqlHost <- postgresqlHostResource(appConfig)
      postgresqlPort <- postgresqlPortResource(appConfig)
      ce <- executionContextResource
      xa <- transactorResource(postgresqlHost, postgresqlPort, ce)
      _ <- global.putR(TransactorResource(xa))
      // Uncomment the following lines to enable schema printing and test insertion during initialization
      // _ <- printSchema(xa)
      // _ <- testInsert(xa)
    } yield ()
  }
}
