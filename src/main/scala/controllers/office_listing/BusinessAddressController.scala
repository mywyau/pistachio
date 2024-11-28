package controllers.office_listing

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.business.desk_listing.requests.DeskListingRequest
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import models.office.office_listing.requests.OfficeListingRequest

trait OfficeListingControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeListingControllerImpl[F[_] : Concurrent](
                                                      officeListingService: OfficeListingServiceAlgebra[F]
                                                    )(implicit logger: Logger[F])
  extends Http4sDsl[F] with OfficeListingControllerAlgebra[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "business" / "businesses" / "office" / "listing" / "create" =>
      logger.info(s"[OfficeListingControllerImpl] POST - Creating office listing") *>
        req.decode[OfficeListingRequest] { request =>
          officeListingService.createListing(request).flatMap {
            case Right(listing) =>
              logger.info(s"[OfficeListingControllerImpl] POST - Successfully created office for a business") *>
                Ok(listing.asJson)
            case Left(error) =>
              val errorResponse = ErrorResponse(error.code, error.errorMessage)
              BadRequest(errorResponse.asJson)
          }
        }
  }

  object OfficeListingController {
    def apply[F[_] : Concurrent](
                                  officeListingService: OfficeListingServiceAlgebra[F]
                                )(implicit logger: Logger[F]): OfficeListingControllerAlgebra[F] =
      new OfficeListingControllerImpl[F](officeListingService)
  }
