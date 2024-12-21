package controllers.business

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import configuration.models.AppConfig
import controllers.constants.BusinessContactDetailsConstants.*
import controllers.fragments.business.BusinessContactDetailsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.adts.*
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.*
import models.business.contact_details.requests.BusinessContactDetailsRequest
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.responses.{CreatedResponse, DeletedResponse, ErrorResponse}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessContactDetailsRepository, BusinessSpecificationsRepository}
import services.business.contact_details.BusinessContactDetailsService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class BusinessContactDetailsControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
          insertBusinessContactDetailsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "GET - /pistachio/business/businesses/contact/details/business_id_1 - " +
      "given a business_id, find the business contactDetails data for given id, returning OK and the contact details json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/businesses/contact/details/business_id_1")

    val expectedBusinessContactDetails = testBusinessContactDetails(Some(1), "user_id_1", "business_id_1")

    client.run(request).use { response =>
      response.as[BusinessContactDetails].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBusinessContactDetails
        )
      }
    }
  }

  test(
    "GET - /pistachio/business/businesses/contact/details/id_does_not_exists - " +
      "given a business id for data which does not exist, return NotFound and "
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/businesses/contact/details/id_does_not_exists")

    val expectedResponseBody = ErrorResponse(BusinessContactDetailsNotFound.code, BusinessContactDetailsNotFound.errorMessage)

    client.run(request).use { response =>
      response.as[ErrorResponse].map { body =>
        expect.all(
          response.status == Status.NotFound,
          body == expectedResponseBody
        )
      }
    }
  }

  test(
    "GET - /pistachio/bad/url - " +
      "given an error in the url, return NotFound and appropriate body"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/bad/url")

    val expectedResponseBody = "Not found"

    client.run(request).use { response =>
      response.bodyText.compile.string.map { body =>
        expect.all(
          response.status == Status.NotFound,
          body == expectedResponseBody
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/businesses/contact/details/business_id_2 - " +
      "given a business_id, delete the business contact details data for given business id, returning OK and Deleted response json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/businesses/contact/details/business_id_2")

    val expectedBody = DeletedResponse("Business contact details deleted successfully")

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
