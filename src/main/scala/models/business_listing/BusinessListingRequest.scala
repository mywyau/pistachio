package models.business_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.business.address.CreateBusinessAddressRequest
import models.business.contact_details.CreateBusinessContactDetailsRequest
import models.business.specifications.requests.CreateBusinessSpecificationsRequest
import models.business.address.CreateBusinessAddressRequest

case class BusinessListingRequest(
  businessId: String,
  addressDetails: CreateBusinessAddressRequest,
  businessSpecs: CreateBusinessSpecificationsRequest,
  contactDetails: CreateBusinessContactDetailsRequest,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object BusinessListingRequest {
  implicit val businessListingRequestEncoder: Encoder[BusinessListingRequest] = deriveEncoder[BusinessListingRequest]
  implicit val businessListingRequestDecoder: Decoder[BusinessListingRequest] = deriveDecoder[BusinessListingRequest]
}
