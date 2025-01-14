package controllers.desk

import cats.effect.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import controllers.ControllerISpecBase
import controllers.constants.DeskListingControllerConstants.testDeskListingRequest
import controllers.desk_listing.DeskListingController
import controllers.fragments.DeskListingControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.desk_listing.Availability
import models.desk_listing.DeskListingPartial
import models.desk_listing.PrivateDesk
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.desk.DeskListingRepositoryImpl
import services.desk_listing.DeskListingServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

import java.time.LocalDateTime
import java.time.LocalTime

class DeskListingControllerDeleteAllISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

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
          sameOfficeIdInsertDeskListings.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "DELETE - /pistachio/business/desk/listing/details/delete/all/office01 - should delete all desk listing for a given officeId"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val findAllRequest =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/find/all/office01")

    val deleteRequest =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/delete/all/office01")

    for {
      findAllResponseBefore <- client.run(findAllRequest).use(_.as[List[DeskListingPartial]])
      _ <- IO(expect(findAllResponseBefore.size == 1))
      deleteResponse <- client.run(deleteRequest).use(_.as[DeletedResponse])
      _ <- IO(expect(deleteResponse.message == "All Business listings deleted successfully"))

      findAllResponseAfter <- client.run(findAllRequest).use(_.as[ErrorResponse])
      _ <- IO(expect(findAllResponseAfter.message == "An error occurred did not find any desks for given office id"))
    } yield success

  }
}
