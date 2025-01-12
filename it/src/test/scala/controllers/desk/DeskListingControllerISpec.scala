package controllers.desk

import cats.effect.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import controllers.constants.DeskListingControllerConstants.testDeskListingRequest
import controllers.desk_listing.DeskListingController
import controllers.fragments.DeskListingControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk_listing.Availability
import models.desk_listing.DeskListingPartial
import models.desk_listing.PrivateDesk
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.Method.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.desk.DeskListingRepositoryImpl
import services.desk_listing.DeskListingServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*
import models.responses.DeletedResponse

class DeskListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createDeskListingsTable.update.run.transact(transactor.xa).void *>
          resetDeskListingTable.update.run.transact(transactor.xa).void *>
          insertDeskListings.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/desk/listing/create - should get the desk for a given desk id"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/desk001")

    val expectedDeskListing =
      DeskListingPartial(
        deskName = "Mikey Desk 1",
        description = Some("A quiet, private desk perfect for focused work with a comfortable chair and good lighting."),
        deskType = PrivateDesk,
        quantity = 5,
        pricePerHour = 20.0,
        pricePerDay = 100.0,
        features = List("Wi-Fi", "Power Outlets", "Ergonomic Chair", "Desk Lamp"),
        availability = Availability(
          List("Monday", "Tuesday", "Wednesday"),
          LocalTime.of(9, 0, 0),
          LocalTime.of(17, 0, 0)
        ),
        rules = Some("No loud conversations, please keep the workspace clean.")
      )

    client.run(request).use { response =>
      response.as[DeskListingPartial].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedDeskListing
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/desk/listing/details/find/all/office01 - should find all the desk listings for office01" +
      "\nPOST - /pistachio/business/desk/listing/create - should create a desk listing" +
      "\nDELETE - /pistachio/business/desk/listing/details/delete/desk003 - should delete a desk listing for a given deskId"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val findAllRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/all/office01")

    val createRequest =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/create")
        .withEntity(testDeskListingRequest.asJson)

    val deleteRequest =
        Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/delete/desk003")

    val expectedDeskListing = CreatedResponse("Business Desk created successfully")

    for {
      findAllResponseBefore <- client.run(findAllRequest).use(_.as[List[DeskListingPartial]])
      _ <- IO(expect(findAllResponseBefore.size == 5))
      createResponse <- client.run(createRequest).use(_.as[CreatedResponse])
      deleteResponse <- client.run(deleteRequest).use(_.as[DeletedResponse])
      _ <- IO(expect(deleteResponse.message == "All Business listings deleted successfully"))
      findAllResponseAfter <- client.run(findAllRequest).use(_.as[List[DeskListingPartial]])
      _ <- IO(expect(findAllResponseAfter.isEmpty))
    } yield success

  }
}
