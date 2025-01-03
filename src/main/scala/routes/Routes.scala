package routes

import cats.effect.*
import cats.NonEmptyParallel
import controllers.*
import controllers.business.BusinessAddressController
import controllers.business.BusinessContactDetailsController
import controllers.business.BusinessSpecificationsController
import controllers.business_listing.BusinessListingController
import controllers.business_listing.BusinessListingControllerImpl
import controllers.desk_listing.DeskListingControllerImpl
import controllers.office.OfficeAddressController
import controllers.office.OfficeContactDetailsController
import controllers.office.OfficeSpecificationsController
import controllers.office_listing.OfficeListingController
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger
import repositories.*
import repositories.business.BusinessAddressRepository
import repositories.business.BusinessContactDetailsRepository
import repositories.business.BusinessListingRepository
import repositories.business.BusinessSpecificationsRepository
import repositories.desk.DeskListingRepository
import repositories.office.OfficeAddressRepository
import repositories.office.OfficeContactDetailsRepository
import repositories.office.OfficeListingRepository
import repositories.office.OfficeSpecificationsRepository
import services.*
import services.business.address.BusinessAddressService
import services.business.business_listing.BusinessListingService
import services.business.contact_details.BusinessContactDetailsService
import services.business.specifications.BusinessSpecificationsService
import services.desk_listing.DeskListingService
import services.office.address.OfficeAddressService
import services.office.contact_details.OfficeContactDetailsService
import services.office.office_listing.OfficeListingService
import services.office.OfficeSpecificationsService

object Routes {

  def deskListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val deskListingRepo = DeskListingRepository(transactor)
    val deskListingService = DeskListingService(deskListingRepo)
    val deskListingController = new DeskListingControllerImpl[F](deskListingService)

    deskListingController.routes
  }

  def officeAddressRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeAddressRepository = OfficeAddressRepository(transactor)
    val officeAddressService = OfficeAddressService(officeAddressRepository)
    val officeAddressController = OfficeAddressController(officeAddressService)

    officeAddressController.routes
  }

  def officeContactDetailsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeContactDetailsRepository = OfficeContactDetailsRepository(transactor)
    val officeContactDetailsService = OfficeContactDetailsService(officeContactDetailsRepository)
    val officeContactDetailsController = OfficeContactDetailsController(officeContactDetailsService)

    officeContactDetailsController.routes
  }

  def officeSpecificationsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeSpecificationsRepository = OfficeSpecificationsRepository(transactor)
    val officeSpecificationsService = OfficeSpecificationsService(officeSpecificationsRepository)
    val officeSpecificationsController = OfficeSpecificationsController(officeSpecificationsService)

    officeSpecificationsController.routes
  }

  def businessAddressRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessAddressRepository = BusinessAddressRepository(transactor)
    val businessAddressService = BusinessAddressService(businessAddressRepository)
    val businessAddressController = BusinessAddressController(businessAddressService)

    businessAddressController.routes
  }

  def businessContactDetailsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessContactDetailsRepository = BusinessContactDetailsRepository(transactor)
    val businessContactDetailsService = BusinessContactDetailsService(businessContactDetailsRepository)
    val businessContactDetailsController = BusinessContactDetailsController(businessContactDetailsService)

    businessContactDetailsController.routes
  }

  def businessSpecificationsRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessSpecificationsRepository = BusinessSpecificationsRepository(transactor)
    val businessSpecificationsService = BusinessSpecificationsService(businessSpecificationsRepository)
    val businessSpecificationsController = BusinessSpecificationsController(businessSpecificationsService)

    businessSpecificationsController.routes
  }

  def officeListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val officeListingRepository = OfficeListingRepository(transactor)
    val officeListingService = OfficeListingService(officeListingRepository)
    val officeListingController = OfficeListingController(officeListingService)

    officeListingController.routes
  }

  def businessListingRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Logger](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val businessListingRepository = BusinessListingRepository(transactor)

    val businessListingService = BusinessListingService(businessListingRepository)
    val businessListingController = BusinessListingController(businessListingService)

    businessListingController.routes
  }
}
