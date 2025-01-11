package models.office.office_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime

case class InitiateOfficeListingRequest(
  businessId: String,
  officeId: String,
  officeName: String,
  description: String
)

object InitiateOfficeListingRequest {
  implicit val initiateOfficeListingRequestEncoder: Encoder[InitiateOfficeListingRequest] = deriveEncoder[InitiateOfficeListingRequest]
  implicit val initiateOfficeListingRequestDecoder: Decoder[InitiateOfficeListingRequest] = deriveDecoder[InitiateOfficeListingRequest]
}
