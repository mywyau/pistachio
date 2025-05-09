package controllers.office

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.ErrorResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.office.OfficeContactDetailsServiceAlgebra

trait OfficeContactDetailsControllerAlgebra[F[_]] {
  def routes: HttpRoutes[F]
}

class OfficeContactDetailsControllerImpl[F[_] : Concurrent](
  officeContactDetailsService: OfficeContactDetailsServiceAlgebra[F]
)(implicit logger: Logger[F])
    extends Http4sDsl[F]
    with OfficeContactDetailsControllerAlgebra[F] {

  implicit val createRequestDecoder: EntityDecoder[F, CreateOfficeContactDetailsRequest] = jsonOf[F, CreateOfficeContactDetailsRequest]
  implicit val updateRequestDecoder: EntityDecoder[F, UpdateOfficeContactDetailsRequest] = jsonOf[F, UpdateOfficeContactDetailsRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "business" / "offices" / "contact" / "details" / officeId =>
      logger.info(s"[OfficeContactDetailsControllerImpl] GET - Office contact details for officeId: $officeId") *>
        officeContactDetailsService.getByOfficeId(officeId).flatMap {
          case Some(contactDetails) =>
            logger.info(s"[OfficeContactDetailsControllerImpl] GET - Successfully retrieved office specification") *>
              Ok(contactDetails.asJson)
          case _ =>
            val errorResponse = ErrorResponse("error", "")
            BadRequest(errorResponse.asJson)  
        }

    case req @ POST -> Root / "business" / "offices" / "contact" / "details" / "create" =>
      logger.info(s"[OfficeContactDetailsControllerImpl] POST - Creating office listing") *>
        req.decode[CreateOfficeContactDetailsRequest] { request =>
          officeContactDetailsService.create(request).flatMap {
            case Valid(response) =>
              logger.info(s"[OfficeContactDetailsControllerImpl] POST - Successfully created a office contact details") *>
                Created(CreatedResponse(response.toString, "Office contact details created successfully").asJson)
            case Invalid(_) =>
              InternalServerError(ErrorResponse(code = "VALIDATION_ERROR", message = "An error occurred").asJson)
          }
        }

    case req @ PUT -> Root / "business" / "offices" / "contact" / "details" / "update" / officeId =>
      logger.info(s"[OfficeContactDetailsControllerImpl] PUT - Updating office contact details with ID: $officeId") *>
        req.decode[UpdateOfficeContactDetailsRequest] { request =>
          officeContactDetailsService.update(officeId, request).flatMap {
            case Valid(updatedAddress) =>
              logger.info(s"[OfficeContactDetailsControllerImpl] PUT - Successfully updated office contact details for ID: $officeId") *>
                Ok(UpdatedResponse("Update_Success", "Office contact details updated successfully").asJson)
            case Invalid(errors) =>
              logger.warn(s"[OfficeContactDetailsControllerImpl] PUT - Validation failed for office contact details update: ${errors.toList}") *>
                BadRequest(ErrorResponse(code = "UPDATE_ERROR", message = errors.toList.mkString(", ")).asJson)
          }
        }

    case DELETE -> Root / "business" / "offices" / "contact" / "details" / officeId =>
      logger.info(s"[OfficeContactDetailsControllerImpl] DELETE - Attempting to delete the office contact details") *>
        officeContactDetailsService.delete(officeId).flatMap {
          case Valid(response) =>
            logger.info(s"[OfficeContactDetailsControllerImpl] DELETE - Successfully deleted office contact details for $officeId") *>
              Ok(DeletedResponse(response.toString, "Office contact details deleted successfully").asJson)
          case Invalid(error) =>
            val errorResponse = ErrorResponse("DELETE_ERROR", "unable to delete office contact details for office id")
            BadRequest(errorResponse.asJson)
        }
  }
}

object OfficeContactDetailsController {
  def apply[F[_] : Concurrent](officeContactDetailsService: OfficeContactDetailsServiceAlgebra[F])(implicit logger: Logger[F]): OfficeContactDetailsControllerAlgebra[F] =
    new OfficeContactDetailsControllerImpl[F](officeContactDetailsService)
}
