package controllers

import cats.effect.*
import cats.implicits.*
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import controllers.business_listing.BusinessListingController
import controllers.desk_listing.DeskListingController
import controllers.office_listing.OfficeListingController
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.adts.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepository, BusinessContactDetailsRepository, BusinessSpecsRepository}
import repositories.desk.DeskListingRepository
import repositories.office.{OfficeAddressRepository, OfficeContactDetailsRepository, OfficeSpecsRepository}
import services.business.business_listing.BusinessListingService
import services.desk_listing.DeskListingService
import services.office.office_listing.OfficeListingService
import weaver.*

import java.time.LocalDateTime

object TestRoutes {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def businessListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessAddressRepository = BusinessAddressRepository(transactor)
    val businessContactDetailsRepository = BusinessContactDetailsRepository(transactor)
    val businessSpecsRepository = BusinessSpecsRepository(transactor)

    val businessListingService = BusinessListingService(businessAddressRepository, businessContactDetailsRepository, businessSpecsRepository)
    val businessListingController = BusinessListingController(businessListingService)

    businessListingController.routes
  }

  def deskListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val deskListingRepository = DeskListingRepository(transactor)
    val deskListingService = DeskListingService(deskListingRepository)
    val deskListingController = DeskListingController(deskListingService)

    deskListingController.routes
  }

  def officeListingRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val officeAddressRepository = OfficeAddressRepository(transactor)
    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)
    val officeSpecsRepository = OfficeSpecsRepository(transactor)

    val officeListingService = OfficeListingService(officeAddressRepository, officeContactDetailsRepository, officeSpecsRepository)
    val officeListingController = OfficeListingController(officeListingService)

    officeListingController.routes
  }

  def createTestRouter(transactor: Transactor[IO]): HttpRoutes[IO] = {

    Router(
      "/pistachio" -> (
        businessListingRoutes(transactor) <+>
          deskListingRoutes(transactor) <+>
          officeListingRoutes(transactor)
        )
    )
  }
}
