package controllers.office_listing

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.office_listing.OfficeListingController
import controllers.fragments.OfficeAddressRepoFragments.*
import controllers.fragments.OfficeContactDetailsRepoFragments.*
import controllers.fragments.OfficeSpecsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.office.adts.*
import models.office.office_specs.OfficeAvailability
import models.office.office_listing.requests.OfficeListingRequest
import models.office.office_specs.OfficeSpecs
import models.office.office_address.OfficeAddress
import models.office.office_contact_details.OfficeContactDetails
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.office.OfficeAddressRepository
import repositories.office.OfficeContactDetailsRepository
import repositories.office.OfficeSpecsRepository
import services.office.office_listing.OfficeListingService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class OfficeListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

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
        createOfficeAddressTable.update.run.transact(transactor.xa).void *>
          resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
          createOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetOfficeContactDetailsTable.update.run.transact(transactor.xa).void *>
          createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          resetOfficeSpecsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeAddressRepository =  OfficeAddressRepository(transactor)
    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)
    val officeSpecsRepository = OfficeSpecsRepository(transactor)

    val officeListingService = OfficeListingService(officeAddressRepository, officeContactDetailsRepository, officeSpecsRepository)
    val officeListingController = OfficeListingController(officeListingService)

    Router(
      "/pistachio" -> officeListingController.routes
    )
  }

  val testOfficeSpecs: OfficeSpecs =
    OfficeSpecs(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability =
        OfficeAvailability(
          days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          startTime = LocalDateTime.of(2024, 11, 21, 10, 0, 0),
          endTime = LocalDateTime.of(2024, 11, 21, 10, 30, 0)
        ),
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeAddress =
    OfficeAddress(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("OfficeListingControllerISpec Building"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeContactDetails =
    OfficeContactDetails(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeAvailability =
    OfficeAvailability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def testOfficeListingRequest(officeId: String): OfficeListingRequest =
    OfficeListingRequest(
      officeId = officeId,
      addressDetails = testOfficeAddress,
      officeSpecs = testOfficeSpecs,
      contactDetails = testOfficeContactDetails,
      availability = testOfficeAvailability,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("GET - /pistachio/business/businesses/office/listing/create - should generate the user profile associated with the user") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val officeListingRequest: Json = testOfficeListingRequest("office_id_1").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/businesses/office/listing/create")
        .withEntity(officeListingRequest)

    val expectedOfficeListing = CreatedResponse("Business Office created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedOfficeListing
        )
      }
    }
  }
}
