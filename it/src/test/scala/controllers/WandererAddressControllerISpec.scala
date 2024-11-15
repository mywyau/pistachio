package controllers

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.wanderer_address.WandererAddressController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import models.users.*
import models.users.wanderer_address.errors.AddressNotFound
import models.users.wanderer_address.responses.error.WandererAddressErrorResponse
import models.users.wanderer_address.service.WandererAddress
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import repositories.users.WandererAddressRepository
import services.wanderer_address.WandererAddressService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class WandererAddressControllerISpec(global: GlobalRead) extends IOSuite {

  //  sbt "it/testOnly *WandererAddressControllerISpec* controllers.ControllerSharedResource"

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        sql"""
          CREATE TABLE IF NOT EXISTS wanderer_address (
             id BIGSERIAL PRIMARY KEY,
             user_id VARCHAR(255) NOT NULL,
             street VARCHAR(255) NOT NULL,
             city VARCHAR(255) NOT NULL,
             country VARCHAR(255) NOT NULL,
             county VARCHAR(255) NOT NULL,
             postcode VARCHAR(255) NOT NULL,
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          );
        """.update.run.transact(transactor.xa).void *>
          sql"TRUNCATE TABLE wanderer_address RESTART IDENTITY"
            .update.run.transact(transactor.xa).void *>
          sql"""
              INSERT INTO wanderer_address (
                  user_id,
                  street,
                  city,
                  country,
                  county,
                  postcode,
                  created_at,
                  updated_at
              ) VALUES (
                  'fake_user_id_1',
                  '123 Example Street',
                  'Sample City',
                  'United Kingdom',
                  'South Glamorgan',
                  'CF5 3NJ',
                  '2025-01-01 00:00:00',
                  '2025-01-01 00:00:00'
              )
          """.update.run.transact(transactor.xa).void
      )
      //      _ <- Resource.eval(sql"DELETE FROM wanderer_address".update.run.transact(transactor.xa).void)
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  // Set up actual service implementations using the transactor resource
  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val wandererAddressRepository = WandererAddressRepository(transactor)
    val wandererAddressService = WandererAddressService(wandererAddressRepository)
    val wandererAddressController = WandererAddressController(wandererAddressService)

    Router(
      "/cashew" -> wandererAddressController.routes
    )
  }

  test("POST - /cashew/wanderer/address/details/fake_user_id_1 - should find the user address associated with the user") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request = Request[IO](GET, uri"http://127.0.0.1:9999/cashew/wanderer/address/details/fake_user_id_1")

    client.run(request).use { response =>
      response.as[WandererAddress].map { body =>
        expect.all(
          response.status == Status.Ok,
          body ==
            WandererAddress(
              id = Some(1),
              user_id = "fake_user_id_1",
              street = "123 Example Street",
              city = "Sample City",
              country = "United Kingdom",
              county = Some("South Glamorgan"),
              postcode = "CF5 3NJ",
              created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
              updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
            )
        )
      }
    }
  }

  test("POST - /cashew/wanderer/address/details/fake_user_id_2 - attempting to find user address for an unknown user should return an WandererAddressErrorResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request = Request[IO](GET, uri"http://127.0.0.1:9999/cashew/wanderer/address/details/fake_user_id_2")

    client.run(request).use { response =>
      response.as[WandererAddressErrorResponse].map { body =>
        expect.all(
          response.status == Status.BadRequest,
          body.code == AddressNotFound.code,
          body.message == AddressNotFound.errorMessage
        )
      }
    }
  }

}