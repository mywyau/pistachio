package controllers.office

import cats.effect.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import configuration.models.AppConfig
import controllers.constants.OfficeSpecificationsControllerITConstants.*
import controllers.fragments.OfficeSpecificationRepoFragments.createOfficeSpecsTable
import controllers.fragments.OfficeSpecificationRepoFragments.insertOfficeSpecificationsTable
import controllers.fragments.OfficeSpecificationRepoFragments.resetOfficeSpecsTable
import controllers.office.OfficeSpecificationsController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import io.circe.Json
import java.time.LocalDateTime
import models.database.CreateSuccess
import models.database.DeleteSuccess
import models.office.adts.*

import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.Method.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.office.OfficeSpecificationsRepository
import services.office.OfficeSpecificationsService
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

class OfficeSpecificationsControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          resetOfficeSpecsTable.update.run.transact(transactor.xa).void *>
          insertOfficeSpecificationsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/offices/specifications/OFF001 - " +
      "given a office_id, find the office specifications data for given id, returning OK and the specifications json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/offices/specifications/OFF001")

    val expectedOfficeSpecifications = testOfficeSpecs1("BUS001", "OFF001")

    client.run(request).use { response =>
      response.as[OfficeSpecificationsPartial].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedOfficeSpecifications
        )
      }
    }
  }

  test(
    "POST - /pistachio/business/offices/specifications/create - " +
      "should generate the office specifications data in postgresql, returning Created response"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val businessListingRequest: Json = testCreateOfficeSpecificationsRequest("BUSINESS1337", "OFFICE1337").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/offices/specifications/create")
        .withEntity(businessListingRequest)

    val expectedBody = CreatedResponse(CreateSuccess.toString, "Office specifications created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/offices/specifications/OFF002 - " +
      "given a office_id, delete the office specifications data for given office id, returning OK and Deleted response json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/offices/specifications/OFF002")

    val expectedBody = DeletedResponse(DeleteSuccess.toString, "Office specifications deleted successfully")

    client.run(request).use { response =>
      response.as[DeletedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBody
        )
      }
    }
  }
}
