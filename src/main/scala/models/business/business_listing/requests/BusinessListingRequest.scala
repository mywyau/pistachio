package models.business.business_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.business.business_address.requests.BusinessAddressRequest
import models.business.business_specs.BusinessAvailability
import models.business.business_specs.BusinessSpecifications
import models.business.business_contact_details.BusinessContactDetails

import java.time.LocalDateTime

case class BusinessListingRequest(
                                   businessId: String,
                                   addressDetails: BusinessAddressRequest,
                                   businessSpecs: BusinessSpecifications,
                                   contactDetails: BusinessContactDetails,
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                               )

object BusinessListingRequest {
  implicit val businessListingRequestEncoder: Encoder[BusinessListingRequest] = deriveEncoder[BusinessListingRequest]
  implicit val businessListingRequestDecoder: Decoder[BusinessListingRequest] = deriveDecoder[BusinessListingRequest]
}

